package org.com.stocknote.domain.stockApi.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockPriceResponse {

    // 기존 rt_cd가 새로운 구조에서는 "status"로 넘어온다면 둘 다 매핑되도록 함
    @JsonAlias({"rt_cd", "status"})
    private String rt_cd;

    // 기존 msg_cd가 새로운 구조에서는 "code"로 넘어온다면 둘 다 매핑되도록 함
    @JsonAlias({"msg_cd", "code"})
    private String msg_cd;

    // 기존 msg1가 새로운 구조에서는 "message"로 넘어온다면 둘 다 매핑되도록 함
    @JsonAlias({"msg1", "message"})
    private String msg1;

    // 내부 데이터가 "output" 또는 "data"에 들어올 수 있도록 함
    @JsonAlias({"output", "data"})
    private Output output;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Output {
        // 현재가: 기존 "stck_prpr" 또는 새로 "currentPrice"로 넘어올 경우
        @JsonAlias({"stck_prpr", "currentPrice"})
        private String stck_prpr;

        // 시가: 기존 "stck_oprc" 또는 새로 "openPrice"로 넘어올 경우
        @JsonAlias({"stck_oprc", "openPrice"})
        private String stck_oprc;

        // 고가: 기존 "stck_hgpr" 또는 새로 "highPrice"로 넘어올 경우
        @JsonAlias({"stck_hgpr", "highPrice"})
        private String stck_hgpr;

        // 저가: 기존 "stck_lwpr" 또는 새로 "lowPrice"로 넘어올 경우
        @JsonAlias({"stck_lwpr", "lowPrice"})
        private String stck_lwpr;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DailyData {
        @JsonAlias({"stck_bsop_date", "businessDate"})
        private String stck_bsop_date;

        @JsonAlias({"stck_clpr", "closingPrice"})
        private String stck_clpr;

        @JsonAlias({"stck_oprc", "openPrice"})
        private String stck_oprc;

        @JsonAlias({"stck_hgpr", "highPrice"})
        private String stck_hgpr;

        @JsonAlias({"stck_lwpr", "lowPrice"})
        private String stck_lwpr;

        @JsonAlias({"acml_vol", "accumulatedVolume"})
        private String acml_vol;

        @JsonAlias({"acml_tr_pbmn", "accumulatedTradingAmount"})
        private String acml_tr_pbmn;
    }
}
