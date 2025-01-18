package com.example.fridge;

public class DataProductInShoppingList {
    private int id;
    private int product_id;
    private int quantity;

    public DataProductInShoppingList(int id, int product_id, int quantity) {
        this.id = id;
        this.product_id = product_id;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public int getQuantity() {
        return quantity;
    }
}
