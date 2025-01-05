package com.example.fridge;

public class DataProductInFridge {
    private Integer id;
    private String manufacture_date;
    private String expiry_date;
    private Integer product_id;
    private Integer quantity;

    public DataProductInFridge(Integer id, String manufacture_date, String expiry_date, Integer product_id, Integer quantity) {
        this.id = id;
        this.manufacture_date = manufacture_date;
        this.expiry_date = expiry_date;
        this.product_id = product_id;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
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

    public Integer getQuantity() {
        return quantity;
    }
}
