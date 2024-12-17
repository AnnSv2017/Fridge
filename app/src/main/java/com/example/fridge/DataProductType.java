package com.example.fridge;

public class DataProductType {
    private Integer id;
    private String name;
    private Integer type_id;
    private Double mass_value;
    private String mass_unit;
    private Double proteins;
    private Double fats;
    private Double carbohydrates;
    private Integer calories_kcal;
    private Integer calories_KJ;
    private String measurement_type;

    public DataProductType(Integer id, String name, Integer type_id, Double mass_value, String mass_unit, Double proteins, Double fats, Double carbohydrates, Integer calories_kcal, Integer calories_KJ, String measurement_type) {
        this.id = id;
        this.name = name;
        this.type_id = type_id;
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

    public String getName() {
        return name;
    }

    public Integer getType_id() {
        return type_id;
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

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType_id(Integer type_id) {
        this.type_id = type_id;
    }

    public void setMass_value(Double mass_value) {
        this.mass_value = mass_value;
    }

    public void setMass_unit(String mass_unit) {
        this.mass_unit = mass_unit;
    }

    public void setProteins(Double proteins) {
        this.proteins = proteins;
    }

    public void setFats(Double fats) {
        this.fats = fats;
    }

    public void setCarbohydrates(Double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public void setCalories_kcal(Integer calories_kcal) {
        this.calories_kcal = calories_kcal;
    }

    public void setCalories_KJ(Integer calories_KJ) {
        this.calories_KJ = calories_KJ;
    }

    public void setMeasurement_type(String measurement_type) {
        this.measurement_type = measurement_type;
    }
}
