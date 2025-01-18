package com.example.fridge;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DataProduct implements Parcelable {
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

    public String getFull_name(){
        return type + ", " + name + ", " + firm + ", " + mass_value + " " + mass_unit;
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

    // Конструктор для восстановления объекта из Parcel
    protected DataProduct(Parcel in) {
        id = in.readInt();
        type = in.readString();
        name = in.readString();
        firm = in.readString();
        mass_value = in.readDouble();
        mass_unit = in.readString();
        proteins = in.readDouble();
        fats = in.readDouble();
        carbohydrates = in.readDouble();
        calories_kcal = in.readInt();
        calories_KJ = in.readInt();
        measurement_type = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(type);
        parcel.writeString(name);
        parcel.writeString(firm);
        parcel.writeDouble(mass_value);
        parcel.writeString(mass_unit);
        parcel.writeDouble(proteins);
        parcel.writeDouble(fats);
        parcel.writeDouble(carbohydrates);
        parcel.writeInt(calories_kcal);
        parcel.writeInt(calories_KJ);
        parcel.writeString(measurement_type);
    }

    // Статический CREATOR
    public static final Parcelable.Creator<DataProduct> CREATOR = new Parcelable.Creator<DataProduct>() {
        @Override
        public DataProduct createFromParcel(Parcel in) {
            return new DataProduct(in);
        }

        @Override
        public DataProduct[] newArray(int size) {
            return new DataProduct[size];
        }
    };
}
