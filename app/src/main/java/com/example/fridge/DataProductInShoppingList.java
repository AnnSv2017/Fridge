package com.example.fridge;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DataProductInShoppingList implements Parcelable {
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

    // Конструктор для восстановления объекта из Parcel
    protected DataProductInShoppingList(Parcel in) {
        id = in.readInt();
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
        parcel.writeInt(product_id);
        parcel.writeInt(quantity);
    }

    // Статический CREATOR
    public static final Creator<DataProductInShoppingList> CREATOR = new Creator<DataProductInShoppingList>() {
        @Override
        public DataProductInShoppingList createFromParcel(Parcel in) {
            return new DataProductInShoppingList(in);
        }

        @Override
        public DataProductInShoppingList[] newArray(int size) {
            return new DataProductInShoppingList[size];
        }
    };
}
