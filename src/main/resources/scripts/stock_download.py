import urllib.request
import ssl
import zipfile
import os
import pandas as pd
from sqlalchemy import create_engine, text

base_dir = os.getcwd()

class KospiAPI:
    def kospi_master_download(base_dir, verbose=False):
        cwd = os.getcwd()
        if (verbose): print(f"current directory is {cwd}")
        ssl._create_default_https_context = ssl._create_unverified_context

        urllib.request.urlretrieve("https://new.real.download.dws.co.kr/common/master/kospi_code.mst.zip",
                                   base_dir + "/kospi_code.zip")

        os.chdir(base_dir)
        if (verbose): print(f"change directory to {base_dir}")
        kospi_zip = zipfile.ZipFile('kospi_code.zip')
        kospi_zip.extractall()

        kospi_zip.close()

        if os.path.exists("kospi_code.zip"):
            os.remove("kospi_code.zip")

    def get_kospi_master_dataframe(base_dir):
        file_name = base_dir + "/kospi_code.mst"
        tmp_fil1 = base_dir + "/kospi_code_part1.tmp"
        tmp_fil2 = base_dir + "/kospi_code_part2.tmp"

        wf1 = open(tmp_fil1, mode="w")
        wf2 = open(tmp_fil2, mode="w")

        with open(file_name, mode="r", encoding="cp949") as f:
            for row in f:
                rf1 = row[0:len(row) - 228]
                rf1_1 = rf1[0:9].rstrip()
                rf1_2 = rf1[9:21].rstrip()
                rf1_3 = rf1[21:].strip()
                wf1.write(rf1_1 + ',' + rf1_2 + ',' + rf1_3 + '\n')
                rf2 = row[-228:]
                wf2.write(rf2)

        wf1.close()
        wf2.close()

        part1_columns = ['단축코드', '표준코드', '한글명']
        df1 = pd.read_csv(tmp_fil1, header=None, names=part1_columns)

        field_specs = [2]
        part2_columns = ['그룹코드']

        df2 = pd.read_fwf(tmp_fil2, widths=field_specs, names=part2_columns)
        df = pd.merge(df1, df2, how='outer', left_index=True, right_index=True)

        # ST 종목만 필터링
        df = df[df['그룹코드'] == 'ST']
        df = df.drop('그룹코드', axis=1)

        os.remove(tmp_fil1)
        os.remove(tmp_fil2)
        if os.path.exists(file_name):
            os.remove(file_name)

        return df

class KosdaqAPI:
    def kosdaq_master_download(base_dir, verbose=False):
        cwd = os.getcwd()
        if (verbose): print(f"current directory is {cwd}")
        ssl._create_default_https_context = ssl._create_unverified_context

        urllib.request.urlretrieve("https://new.real.download.dws.co.kr/common/master/kosdaq_code.mst.zip",
                                   base_dir + "/kosdaq_code.zip")

        os.chdir(base_dir)
        kosdaq_zip = zipfile.ZipFile('kosdaq_code.zip')
        kosdaq_zip.extractall()
        kosdaq_zip.close()

        if os.path.exists("kosdaq_code.zip"):
            os.remove("kosdaq_code.zip")

    def get_kosdaq_master_dataframe(base_dir):
        file_name = base_dir + "/kosdaq_code.mst"
        tmp_fil1 = base_dir + "/kosdaq_code_part1.tmp"
        tmp_fil2 = base_dir + "/kosdaq_code_part2.tmp"

        wf1 = open(tmp_fil1, mode="w")
        wf2 = open(tmp_fil2, mode="w")

        with open(file_name, mode="r", encoding="cp949") as f:
            for row in f:
                rf1 = row[0:len(row) - 222]
                rf1_1 = rf1[0:9].rstrip()
                rf1_2 = rf1[9:21].rstrip()
                rf1_3 = rf1[21:].strip()
                wf1.write(rf1_1 + ',' + rf1_2 + ',' + rf1_3 + '\n')
                rf2 = row[-222:]
                wf2.write(rf2)

        wf1.close()
        wf2.close()

        part1_columns = ['단축코드', '표준코드', '한글종목명']
        df1 = pd.read_csv(tmp_fil1, header=None, names=part1_columns)

        field_specs = [2]
        part2_columns = ['증권그룹구분코드']

        df2 = pd.read_fwf(tmp_fil2, widths=field_specs, names=part2_columns)
        df = pd.merge(df1, df2, how='outer', left_index=True, right_index=True)

        df = df[df['증권그룹구분코드'] == 'ST']
        df = df.drop('증권그룹구분코드', axis=1)

        os.remove(tmp_fil1)
        os.remove(tmp_fil2)
        if os.path.exists(file_name):
            os.remove(file_name)

        return df

class StockDownloader:
    def __init__(self, base_dir):
        self.base_dir = base_dir
        self.engine = create_engine(
            'mysql+mysqlconnector://root:1234@43.203.126.129/default_db',
            connect_args={
                'connect_timeout': 30,
                'auth_plugin': 'mysql_native_password'
            }
        )

    def update_stock_data(self, df):
        with self.engine.begin() as connection:
            # 기존 데이터 삭제 대신 MERGE(UPSERT) 방식으로 변경
            for _, row in df.iterrows():
                connection.execute(
                    text("""
                        INSERT INTO stock (code, name, market) 
                        VALUES (:code, :name, :market)
                        ON DUPLICATE KEY UPDATE 
                            name = VALUES(name),
                            market = VALUES(market)
                    """),
                    {
                        "code": row['code'],
                        "name": row['name'],
                        "market": row['market']
                    }
                )

    def kospi_download(self):
        KospiAPI.kospi_master_download(self.base_dir)
        df = KospiAPI.get_kospi_master_dataframe(self.base_dir)

        column_mapping = {
            '단축코드': 'code',
            '한글명': 'name'
        }
        df = df[['단축코드', '한글명']].rename(columns=column_mapping)
        df['market'] = 'KOSPI'  # 마켓 정보 추가

        self.update_stock_data(df)

    def kosdaq_download(self):
        KosdaqAPI.kosdaq_master_download(self.base_dir)
        df = KosdaqAPI.get_kosdaq_master_dataframe(self.base_dir)

        column_mapping = {
            '단축코드': 'code',
            '한글종목명': 'name'
        }
        df = df[['단축코드', '한글종목명']].rename(columns=column_mapping)
        df['market'] = 'KOSDAQ'  # 마켓 정보 추가

        self.update_stock_data(df)

if __name__ == "__main__":
    base_dir = os.getcwd()
    downloader = StockDownloader(base_dir)

    # KOSPI 데이터 다운로드 (기존 데이터 삭제 후 새로운 데이터 추가)
    downloader.kospi_download()

    # KOSDAQ 데이터 다운로드 (기존 데이터에 추가)
    downloader.kosdaq_download()
