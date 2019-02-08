package com.ibiscus.shopnchek.application.shopper;

public class ImportItemResult {
    private final String identifier;
    private final String detail;
    private final String status;

    public ImportItemResult(String identifier) {
        this.identifier = identifier;
        this.detail = "-";
        this.status = "OK";
    }

    public ImportItemResult(String identifier, String detail) {
        this.identifier = identifier;
        this.detail = detail;
        this.status = "ERROR";
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDetail() {
        return detail;
    }

    public String getStatus() {
        return status;
    }
}
