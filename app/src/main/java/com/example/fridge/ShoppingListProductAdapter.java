package com.example.fridge;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListProductAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<DataProductInShoppingList> productsList;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ShoppingListProductAdapter(Context context, ArrayList<DataProductInShoppingList> productsList){
        this.context = context;
        if (productsList == null || productsList.isEmpty()) {
            Log.e("ShoppingListAdapter", "Список продуктов пуст!");
        } else {
            Log.e("ShoppingListAdapter", "Список НЕ пуст! " + productsList.size());
        }
        this.productsList = productsList;
    }

//    @Override
//    public int getCount() {
//        return productsList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return productsList.get(position);
//    }

    @Override
    public int getCount() {
        return (productsList != null) ? productsList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return (productsList != null && position >= 0 && position < productsList.size()) ? productsList.get(position) : null;
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
        DataProductInShoppingList dataProductInShoppingList = productsList.get(position);
        int productId = dataProductInShoppingList.getProduct_id();
        DataProduct dataProduct = dbHelper.getProductById(productId);
        String name = dataProduct.getName();

        // Получение количества товара
        int quantity = (Integer) dataProductInShoppingList.getQuantity();

        TextView textViewName = view.findViewById(R.id.text_view_name);
        TextView textViewDays = view.findViewById(R.id.text_view_days);
        TextView textViewQuantity = view.findViewById(R.id.text_view_quantity);
        RelativeLayout rlProduct = view.findViewById(R.id.rl_product);

        textViewName.setText(name);
        textViewDays.setVisibility(View.GONE);
        textViewQuantity.setText(String.valueOf(quantity));

        return view;
    }
}