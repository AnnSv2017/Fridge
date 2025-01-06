package com.example.fridge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ListView;

import com.example.fridge.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ArrayList<DataProductInFridge> productsList;
    private ProductAdapter productAdapter;

    private ListView listViewProducts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = DBHelper.getInstance(this);

        productsList = dbHelper.getAllProductsInFridge();

        productAdapter = new ProductAdapter(this, productsList);

        listViewProducts = findViewById(R.id.list_view_products);

        listViewProducts.setAdapter(productAdapter);
    }

    public void btnQrCodeScanner(View v){
        Intent intent = new Intent(this, QrCodeScannerActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Закрываем БД
        DBManager.getInstance(this).closeDatabase();
    }
}