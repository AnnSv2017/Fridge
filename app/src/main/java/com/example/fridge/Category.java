package com.example.fridge;

import java.util.ArrayList;

public class Category<T> {
    private String name;
    private int imageResourceId;
    private boolean isExpanded; // Расширенный
    private ArrayList<T> products;

    public Category(String name, boolean isExpanded, ArrayList<T> products) {
        this.name = name;
        this.isExpanded = isExpanded;
        this.products = products;
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

    public ArrayList<T> getProducts() { return products; }
}
