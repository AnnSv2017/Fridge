package com.example.fridge;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DataProductInFridge implements Parcelable {
    private int id;
    private String manufacture_date;
    private String expiry_date;
    private int product_id;
    private int quantity;

    public DataProductInFridge(int id, String manufacture_date, String expiry_date, int product_id, int quantity) {
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


    // Конструктор для восстановления объекта из Parcel
    protected DataProductInFridge(Parcel in) {
        id = in.readInt();
        manufacture_date = in.readString();
        expiry_date = in.readString();
        product_id = in.readInt();
        quantity = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(manufacture_date);
        parcel.writeString(expiry_date);
        parcel.writeInt(product_id);
        parcel.writeInt(quantity);
    }

    // Статический CREATOR
    public static final Creator<DataProductInFridge> CREATOR = new Creator<DataProductInFridge>() {
        @Override
        public DataProductInFridge createFromParcel(Parcel in) {
            return new DataProductInFridge(in);
        }

        @Override
        public DataProductInFridge[] newArray(int size) {
            return new DataProductInFridge[size];
        }
    };
}
