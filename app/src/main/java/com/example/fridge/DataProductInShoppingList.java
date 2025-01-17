package com.example.fridge;

public class DataProductInShoppingList {
    private int id;
    private String manufacture_date;
    private String expiry_date;
    private int product_id;
    private int quantity;

    public DataProductInShoppingList(int id, String manufacture_date, String expiry_date, int product_id, int quantity) {
        this.id = id;
        this.manufacture_date = manufacture_date;
        this.expiry_date = expiry_date;
        this.product_id = product_id;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getManufacture_date() {
        return manufacture_date;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public int getProduct_id() {
        return product_id;
    }

    public int getQuantity() {
        return quantity;
    }
}
