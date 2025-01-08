package com.example.fridge;

import java.util.ArrayList;

public class Category {
    private String name;
    private int imageResourceId;
    private boolean isExpanded; // Расширенный
    private ArrayList<DataProductInFridge> products;

    public Category(String name, int imageResourceId, ArrayList<DataProductInFridge> products) {
        this.name = name;
        this.imageResourceId = imageResourceId;
        this.products = products;
        this.isExpanded = false;
    }

    public String getName() {
        return name;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public ArrayList<DataProductInFridge> getProducts() {
        return products;
    }
}
