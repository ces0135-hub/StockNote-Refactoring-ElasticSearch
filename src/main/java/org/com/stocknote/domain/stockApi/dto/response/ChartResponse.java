package org.com.stocknote.domain.stockApi.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChartResponse {
    private List<CandleData> candles;  // ✅ output2에서 가져오기
    private StockSummary summary;      // ✅ output1에서 가져오기
    private String stockCode;          // ✅ output1의 stck_shrn_iscd 매핑
    private String stockName;          // ✅ output1의 hts_kor_isnm 매핑

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CandleData {
        private String time;
        private double open;
        private double high;
        private double low;
        private double close;
        private long volume;
        private long value;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StockSummary {
        private double changePrice;   // ✅ output1의 prdy_vrss 매핑
        private double changeRate;    // ✅ output1의 prdy_ctrt 매핑
        private long volume;          // ✅ output1의 acml_vol 매핑
    }
}
