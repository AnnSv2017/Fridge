package com.example.fridge;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class AnalyticsProduct implements Parcelable {
    private int productId;
    private String manufactureDate;
    private String expiryDate;
    private int addLogsCount;
    private int usedLogsCount;
    private int overdueLogsCount;

    public AnalyticsProduct(int productId, String manufactureDate, String expiryDate, int addLogsCount, int usedLogsCount, int overdueLogsCount) {
        this.productId = productId;
        this.manufactureDate = manufactureDate;
        this.expiryDate = expiryDate;
        this.addLogsCount = addLogsCount;
        this.usedLogsCount = usedLogsCount;
        this.overdueLogsCount = overdueLogsCount;
    }

    public int getProductId() {
        return productId;
    }

    public String getManufactureDate() {
        return manufactureDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public int getAddLogsCount() {
        return addLogsCount;
    }

    public int getUsedLogsCount() {
        return usedLogsCount;
    }

    public int getOverdueLogsCount() {
        return overdueLogsCount;
    }

    // Конструктор для восстановления объекта из Parcel
    protected AnalyticsProduct(Parcel in) {
        productId = in.readInt();
        manufactureDate = in.readString();
        expiryDate = in.readString();
        addLogsCount = in.readInt();
        usedLogsCount = in.readInt();
        overdueLogsCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(productId);
        parcel.writeString(manufactureDate);
        parcel.writeString(expiryDate);
        parcel.writeInt(addLogsCount);
        parcel.writeInt(usedLogsCount);
        parcel.writeInt(overdueLogsCount);
    }

    // Статический CREATOR
    public static final Parcelable.Creator<AnalyticsProduct> CREATOR = new Parcelable.Creator<AnalyticsProduct>() {
        @Override
        public AnalyticsProduct createFromParcel(Parcel in) {
            return new AnalyticsProduct(in);
        }

        @Override
        public AnalyticsProduct[] newArray(int size) {
            return new AnalyticsProduct[size];
        }
    };
}
