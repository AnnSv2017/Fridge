package com.example.fridge;

public class DataProductAllergens {
    private Integer id;
    private Integer product_id;
    private String allergen;

    public DataProductAllergens(Integer id, Integer product_id, String allergen) {
        this.id = id;
        this.product_id = product_id;
        this.allergen = allergen;
    }

    public Integer getId() {
        return id;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public String getAllergen() {
        return allergen;
    }
}
