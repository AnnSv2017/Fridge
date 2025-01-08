package com.example.fridge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.fridge.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    private ArrayList<Category> categoriesList;
    private CategoryAdapter categoryAdapter;

    private ListView listViewCategories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = DBHelper.getInstance(this);

        categoriesList = dbHelper.getAllCategories();
        if (categoriesList == null || categoriesList.isEmpty()) {
            Log.e("MainActivity", "categoriesList is empty or null!");
        } else {
            Log.d("MainActivity", "categoriesList size: " + categoriesList.size());
            Log.d("MainActivity", "productsInFridgeList size: " + dbHelper.getAllProductsInFridge().size());
        }
        categoryAdapter = new CategoryAdapter(this, categoriesList);

        listViewCategories = findViewById(R.id.list_view_categories);
        listViewCategories.setAdapter(categoryAdapter);
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