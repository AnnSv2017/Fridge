package com.example.fridge;

public class DataProductAllergens {
    private Integer id;
    private Integer product_type_id;
    private Integer allergen_id;

    public DataProductAllergens(Integer id, Integer product_type_id, Integer allergen_id) {
        this.id = id;
        this.product_type_id = product_type_id;
        this.allergen_id = allergen_id;
    }

    public Integer getId() {
        return id;
    }

    public Integer getProduct_type_id() {
        return product_type_id;
    }

    public Integer getAllergen_id() {
        return allergen_id;
    }
}
