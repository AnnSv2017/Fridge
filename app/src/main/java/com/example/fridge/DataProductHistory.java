package com.example.fridge;

public class DataProductHistory {
    private Integer id;
    private Integer product_id;
    private String operation_type;
    private Double quantity;

    public DataProductHistory(Integer id, Integer product_id, String operation_type, Double quantity) {
        this.id = id;
        this.product_id = product_id;
        this.operation_type = operation_type;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public String getOperation_type() {
        return operation_type;
    }

    public Double getQuantity() {
        return quantity;
    }
}
