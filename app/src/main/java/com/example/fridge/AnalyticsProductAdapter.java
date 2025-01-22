package com.example.fridge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class AnalyticsProductAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<AnalyticsProduct> productsList;


    public AnalyticsProductAdapter(Context context, ArrayList<AnalyticsProduct> productsList){
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
            view = inflater.inflate(R.layout.product_analytics_item, viewGroup, false);
        }

        // Получение названия продукта
        DBHelper dbHelper = DBHelper.getInstance(context);
        AnalyticsProduct data = productsList.get(position);
        int productId = data.getProductId();
        DataProduct dataProduct = dbHelper.getProductById(productId);
        String name = dataProduct.getName();

        String manufactureDate = data.getManufactureDate();
        String expiryDate = data.getExpiryDate();

        TextView textViewName = view.findViewById(R.id.text_view_name);
        TextView textViewAdd = view.findViewById(R.id.text_view_add);
        TextView textViewUsed = view.findViewById(R.id.text_view_used);
        TextView textViewOverdue = view.findViewById(R.id.text_view_overdue);
        RelativeLayout rlProduct = view.findViewById(R.id.rl_product);

        textViewName.setText(name);
        textViewAdd.setText(String.valueOf(data.getAddLogsCount()));
        textViewUsed.setText("+" + String.valueOf(data.getUsedLogsCount()));
        textViewOverdue.setText("-" + String.valueOf(data.getOverdueLogsCount()));



        return view;
    }
}
