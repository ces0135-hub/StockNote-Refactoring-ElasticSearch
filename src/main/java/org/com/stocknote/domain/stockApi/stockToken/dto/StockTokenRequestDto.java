package org.com.stocknote.domain.stockApi.stockToken.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class StockTokenRequestDto {
    private String grant_type;
    private String appkey;
    private String appsecret;

    public StockTokenRequestDto (String grant_type, String appKey, String appSecret) {
        this.grant_type = grant_type;
        this.appkey = appKey;
        this.appsecret = appSecret;
    }
}
