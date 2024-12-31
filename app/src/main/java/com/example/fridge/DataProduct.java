package com.example.fridge;

public class DataProduct {
    private Integer id;
    private String type;
    private String name;
    private String firm;
    private Double mass_value;
    private String mass_unit;
    private Double proteins;
    private Double fats;
    private Double carbohydrates;
    private Integer calories_kcal;
    private Integer calories_KJ;
    private String measurement_type;

    private String full_name;

    public DataProduct(Integer id, String type, String name, String firm, Double mass_value, String mass_unit, Double proteins, Double fats, Double carbohydrates, Integer calories_kcal, Integer calories_KJ, String measurement_type) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.firm = firm;
        this.mass_value = mass_value;
        this.mass_unit = mass_unit;
        this.proteins = proteins;
        this.fats = fats;
        this.carbohydrates = carbohydrates;
        this.calories_kcal = calories_kcal;
        this.calories_KJ = calories_KJ;
        this.measurement_type = measurement_type;
    }

    public Integer getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getFirm() {
        return firm;
    }

    public Double getMass_value() {
        return mass_value;
    }

    public String getMass_unit() {
        return mass_unit;
    }

    public Double getProteins() {
        return proteins;
    }

    public Double getFats() {
        return fats;
    }

    public Double getCarbohydrates() {
        return carbohydrates;
    }

    public Integer getCalories_kcal() {
        return calories_kcal;
    }

    public Integer getCalories_KJ() {
        return calories_KJ;
    }

    public String getMeasurement_type() {
        return measurement_type;
    }
}
