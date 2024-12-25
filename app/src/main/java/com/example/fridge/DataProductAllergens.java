package com.example.fridge;

public class DataProductAllergens {
    private Integer id;
    private Integer product_id;
    private Integer allergen_id;

    public DataProductAllergens(Integer id, Integer product_id, Integer allergen_id) {
        this.id = id;
        this.product_id = product_id;
        this.allergen_id = allergen_id;
    }

    public Integer getId() {
        return id;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public Integer getAllergen_id() {
        return allergen_id;
    }
}
