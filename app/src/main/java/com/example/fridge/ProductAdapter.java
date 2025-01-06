package com.example.fridge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<DataProductInFridge> productsList;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ProductAdapter(Context context, ArrayList<DataProductInFridge> productsList){
        this.context = context;
        this.productsList = productsList;
    }

    @Override
    public int getCount() {
        return productsList.size();
    }

    @Override
    public Object getItem(int position) {
        return productsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.product_item, viewGroup, false);
        }

        // Получение названия продукта
        DBHelper dbHelper = DBHelper.getInstance(context);
        DataProductInFridge dataProductInFridge = productsList.get(position);
        int productId = dataProductInFridge.getProduct_id();
        DataProduct dataProduct = dbHelper.getProductById(productId);
        String name = dataProduct.getName();

        // Получения количества дней до истечения срока
        LocalDate manufactureDate = LocalDate.parse(dataProductInFridge.getManufacture_date(), formatter);
        LocalDate expiryDate = LocalDate.parse(dataProductInFridge.getExpiry_date(), formatter);
        long days = ChronoUnit.DAYS.between(manufactureDate, expiryDate);

        // Получение количества товара
        int quantity = (Integer) dataProductInFridge.getQuantity();

        TextView textViewName = view.findViewById(R.id.text_view_name);
        TextView textViewDays = view.findViewById(R.id.text_view_days);
        TextView textViewQuantity = view.findViewById(R.id.text_view_quantity);

        textViewName.setText(name);
        textViewDays.setText(String.valueOf(days) + " дн.");
        textViewQuantity.setText(String.valueOf(quantity));

        return view;
    }
}
