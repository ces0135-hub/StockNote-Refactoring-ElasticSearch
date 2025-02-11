package org.com.stocknote.domain.stockApi.type;

public enum PeriodType {
    DAILY("D"),
    WEEKLY("W"),
    MONTHLY("M"),
    YEARLY("Y"),
    TIME("T");

    private final String code;

    PeriodType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
