package com.example.fridge;

public class DataProductLogs {
    private Integer id;
    private String date;
    private String manufacture_date;
    private String expiry_date;
    private Integer product_id;
    private String operation_type;
    private Integer quantity;

    public DataProductLogs(Integer id, String date, String manufacture_date, String expiry_date, Integer product_id, String operation_type, Integer quantity) {
        this.id = id;
        this.date = date;
        this.manufacture_date = manufacture_date;
        this.expiry_date = expiry_date;
        this.product_id = product_id;
        this.operation_type = operation_type;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getManufacture_date() {
        return manufacture_date;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public String getOperation_type() {
        return operation_type;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
