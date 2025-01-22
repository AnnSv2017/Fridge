package com.example.fridge;

public class AnalyticsProduct {
    private int productId;
    private String manufactureDate;
    private String expiryDate;
    private int addLogsCount;
    private int usedLogsCount;
    private int overdueLogsCount;

    public AnalyticsProduct(int productId, String manufactureDate, String expiryDate, int addLogsCount, int usedLogsCount, int overdueLogsCount) {
        this.productId = productId;
        this.manufactureDate = manufactureDate;
        this.expiryDate = expiryDate;
        this.addLogsCount = addLogsCount;
        this.usedLogsCount = usedLogsCount;
        this.overdueLogsCount = overdueLogsCount;
    }

    public int getProductId() {
        return productId;
    }

    public String getManufactureDate() {
        return manufactureDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public int getAddLogsCount() {
        return addLogsCount;
    }

    public int getUsedLogsCount() {
        return usedLogsCount;
    }

    public int getOverdueLogsCount() {
        return overdueLogsCount;
    }
}
