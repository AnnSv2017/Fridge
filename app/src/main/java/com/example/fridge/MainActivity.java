package com.example.fridge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.fridge.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.OnProductClickListener, OnFragmentInteractionListener {

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
        categoryAdapter = new CategoryAdapter(this, categoriesList, this);

        listViewCategories = findViewById(R.id.list_view_categories);
        listViewCategories.setAdapter(categoryAdapter);
    }

    @Override
    public void onProductClick(DataProductInFridge product) {
        // Вызываем публичный метод активности
        ProductDetailFragment productDetailFragment = ProductDetailFragment.newInstance(product);
        switchToFragment(productDetailFragment);
    }

    public void switchToFragment(Fragment fragment) {
        try {
            if (fragment == null) return;
            // Скрываем активность
            findViewById(R.id.activity_container).setVisibility(View.GONE);
            // Отображаем фрагмент
            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

            // Вставка фрагмента в пустой fragment_container
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.commit();
        } catch (Exception e){
            Log.e("switchToFragment", "Ошибка при переключении фрагмента", e);
        }
    }

    @Override
    public void onFragmentClose() {
        // Восстанавливаем активность
        findViewById(R.id.activity_container).setVisibility(View.VISIBLE);
        // Скрываем контейнер фрагмента
        findViewById(R.id.fragment_container).setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        // Проверяем, есть ли фрагменты в стеке
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // Если есть, убираем верхний фрагмент из стека
            getSupportFragmentManager().popBackStack();
        } else {
            // Если стека нет, завершаем активность
            super.onBackPressed();
        }
        // Восстанавливаем активность
        findViewById(R.id.activity_container).setVisibility(View.VISIBLE);
        // Скрываем контейнер фрагмента
        findViewById(R.id.fragment_container).setVisibility(View.GONE);
    }


    public void btnQrCodeScanner(View v){
        Intent intent = new Intent(this, QrCodeScannerActivity.class);
        startActivity(intent);
    }

    public void btnShoppingList(View v){
        Intent intent = new Intent(this, ShoppingListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Закрываем БД
        DBManager.getInstance(this).closeDatabase();
    }
}