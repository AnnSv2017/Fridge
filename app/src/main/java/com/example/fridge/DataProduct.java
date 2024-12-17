package com.example.fridge;

public class DataProduct {
    private Integer id;
    private Integer product_type_id;
    private String manufacture_date;
    private String expiry_date;

    public Integer getId() {
        return id;
    }

    public Integer getProduct_type_id() {
        return product_type_id;
    }

    public String getManufacture_date() {
        return manufacture_date;
    }

    public String getExpiry_date() {
        return expiry_date;
    }
}
