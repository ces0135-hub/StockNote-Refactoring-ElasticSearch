package org.com.stocknote;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application.yml")
class StockNoteApplicationTests {
    @Test
    void contextLoads() {
        // 기본 컨텍스트 로드 테스트
    }
}
