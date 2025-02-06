# Init script 생성
resource "ncloud_init_script" "init" {
  name    = "${var.prefix}-init"
  content = <<-EOF
              #!/bin/bash

              # Setup logging
              exec 1> >(tee -a "/var/log/user-data.log") 2>&1

              echo "[INFO] Starting installation..."

              # Update system and install Docker
              echo "[INFO] Installing Docker..."
              apt-get update
              apt-get install -y docker.io

              # Start and enable Docker
              echo "[INFO] Starting Docker service..."
              systemctl start docker
              systemctl enable docker
              sleep 10

              # Install Docker Compose
              echo "[INFO] Installing Docker Compose..."
              apt-get install -y docker-compose-plugin
              apt-get install -y docker-compose

              # Create directory for ELK
              echo "[INFO] Setting up ELK stack..."
              mkdir -p /dockerProjects/elk
              cd /dockerProjects/elk

              # Wait for network connectivity
              echo "[INFO] Waiting for network connectivity..."
              max_attempts=30
              attempt=1
              while [ $attempt -le $max_attempts ]; do
                if docker pull docker.elastic.co/elasticsearch/elasticsearch:8.3.3; then
                  echo "Successfully pulled elasticsearch image"
                  break
                fi
                echo "Attempt $attempt of $max_attempts: Waiting for network... (sleeping 10s)"
                sleep 10
                attempt=$((attempt + 1))
              done

              if [ $attempt -gt $max_attempts ]; then
                echo "Failed to pull docker images after $max_attempts attempts"
                exit 1
              fi

              # Pull Kibana image separately
              docker pull docker.elastic.co/kibana/kibana:8.3.3

              # Create docker-compose.yml
              echo "[INFO] Creating docker-compose.yml..."
              cat << 'DOCKEREOF' > docker-compose.yml
              version: '3'
              services:
                elasticsearch:
                  image: docker.elastic.co/elasticsearch/elasticsearch:8.3.3
                  container_name: elasticsearch
                  environment:
                    - discovery.type=single-node
                    - xpack.security.enabled=false
                    - ES_JAVA_OPTS=-Xms512m -Xmx512m
                  command: >
                    bash -c '
                      bin/elasticsearch-plugin install analysis-nori;
                      /usr/local/bin/docker-entrypoint.sh elasticsearch
                    '
                  ports:
                    - "9200:9200"
                  networks:
                    - elastic
                kibana:
                  image: docker.elastic.co/kibana/kibana:8.3.3
                  container_name: kibana
                  environment:
                    SERVER_NAME: kibana
                    ELASTICSEARCH_HOSTS: http://elasticsearch:9200
                  ports:
                    - 5601:5601
                  depends_on:
                    - elasticsearch
                  networks:
                    - elastic

                logstash:
                  image: docker.elastic.co/logstash/logstash:8.3.3
                  container_name: logstash
                  environment:
                    - LS_JAVA_OPTS=-Xms512m -Xmx512m
                  volumes:
                    - ./logstash/pipeline:/usr/share/logstash/pipeline:ro
                    - ./logstash/lib/mysql-connector-j-9.2.0.jar:/usr/share/logstash/logstash-core/lib/jars/mysql-connector-j-9.2.0.jar:ro
                  ports:
                    - "5001:5001"
                    - "5044:5044"
                  depends_on:
                    - elasticsearch
                  networks:
                    - elastic

              networks:
                elastic:
                  driver: bridge
              DOCKEREOF

              # Set proper permissions
              chmod +x docker-compose.yml
              chown -R root:root /dockerProjects/elk

              # Run docker-compose
              echo "[INFO] Starting containers..."
              cd /dockerProjects/elk && docker-compose up -d

              # Wait for containers to be ready
              echo "[INFO] Waiting for containers to be ready..."
              max_wait=30
              count=0
              while [ $count -lt $max_wait ]; do
                if docker ps | grep -q "elasticsearch" && docker ps | grep -q "logstash" && docker ps | grep -q "kibana"; then
                  echo "All containers are running"
                  # Additional wait for services to be fully operational
                  sleep 30
                  break
                fi
                echo "Waiting for containers to be ready... ($count/$max_wait)"
                sleep 10
                count=$((count + 1))
              done

              if [ $count -eq $max_wait ]; then
                echo "Timeout waiting for containers"
                exit 1
              fi

              # Create directories for Logstash
              echo "[INFO] Setting up Logstash configuration..."
              mkdir -p /dockerProjects/elk/logstash/pipeline
              mkdir -p /dockerProjects/elk/logstash/lib

              # Download MySQL Connector
              echo "[INFO] Downloading MySQL Connector..."
              wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.2.0/mysql-connector-j-9.2.0.jar -P /dockerProjects/elk/logstash/lib/

              # Set proper permissions for MySQL Connector
              echo "[INFO] Setting permissions for MySQL Connector..."
              chmod 644 /dockerProjects/elk/logstash/lib/mysql-connector-j-9.2.0.jar
              chown root:root /dockerProjects/elk/logstash/lib/mysql-connector-j-9.2.0.jar

              # Create logstash.conf
              echo "[INFO] Creating logstash configuration..."
              cat << 'LOGSTASHEOF' > /dockerProjects/elk/logstash/pipeline/logstash.conf
              input {
                jdbc {
                  jdbc_driver_library => "/usr/share/logstash/logstash-core/lib/jars/mysql-connector-j-9.2.0.jar"
                  jdbc_driver_class => "com.mysql.cj.jdbc.Driver"
                  jdbc_connection_string => "jdbc:mysql://43.203.126.129:3306/elk_dev"
                  jdbc_user => "llddlocal"
                  jdbc_password => "1234"
                  statement => "SELECT * FROM post"
                  schedule => "*/30 * * * * *"
                  sql_log_level => "debug"
                  tracking_column => "id"
                  use_column_value => true
                  record_last_run => true
                  last_run_metadata_path => "/usr/share/logstash/last_run_metadata"
                  type => "mysql"
                }
                tcp {
                  port => 5001
                  codec => json_lines
                  type => "spring-boot-log"
                }
              }

              filter {
                if [type] == "spring-boot-log" {
                  date {
                    match => [ "@timestamp", "ISO8601" ]
                  }
                } else if [type] == "mysql" {
                  mutate {
                    remove_field => ["@version", "jdbc_connection_string", "jdbc_user", "jdbc_password"]
                  }
                }
              }

              output {
                if [type] == "spring-boot-log" {
                  elasticsearch {
                    hosts => ["elasticsearch:9200"]
                    index => "springlogs-%%{+YYYY.MM}"
                  }
                  stdout {
                    codec => rubydebug
                  }
                } else if [type] == "mysql" {
                  elasticsearch {
                    hosts => ["elasticsearch:9200"]
                    index => "app1_posts"
                    document_id => "%%{id}"
                  }
                  stdout {
                    codec => rubydebug { metadata => true }
                  }
                }
              }
              LOGSTASHEOF

              # Check installation and log status
              echo "[INFO] Checking container status..."
              docker ps
              docker-compose logs

              echo "[INFO] Installation completed!"
              EOF
}
