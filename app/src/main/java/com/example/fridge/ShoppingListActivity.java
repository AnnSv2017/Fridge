package com.example.fridge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ShoppingListActivity extends AppCompatActivity implements ShoppingListCategoryAdapter.OnProductClickListener, OnFragmentInteractionListener {

    private DBHelper dbHelper;

    private ArrayList<Category> categoriesList;
    private ShoppingListCategoryAdapter shoppingListCategoryAdapter;

    private ListView listViewCategories;
    private ImageView btnAddProductToList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        dbHelper = DBHelper.getInstance(this);

        categoriesList = dbHelper.getAllCategoriesForShoppingList();
        if (categoriesList == null || categoriesList.isEmpty()) {
            Log.e("ShoppingListActivity", "categoriesList is empty or null!");
        } else {
            Log.d("ShoppingListActivity", "categoriesList size: " + categoriesList.size());
            Log.d("ShoppingListActivity", "productsInFridgeList size: " + dbHelper.getAllProductsInShoppingList().size());
        }
        shoppingListCategoryAdapter = new ShoppingListCategoryAdapter(this, categoriesList, this);

        listViewCategories = findViewById(R.id.list_view_categories);
        listViewCategories.setAdapter(shoppingListCategoryAdapter);

        btnAddProductToList = findViewById(R.id.image_view_add_product);
        btnAddProductToList.setOnClickListener(view -> {addProductToListOnClick();});
    }

    private void addProductToListOnClick(){
        CreateProductFragment createProductFragment = CreateProductFragment.newInstance("ShoppingListActivity");
        switchToFragment(createProductFragment);
    }


    // Метод, вызываемый при нажатии на элемент productListView
    @Override
    public void onProductClick(DataProductInShoppingList product) {
        AddToFridgeFromShoppingListFragment addToFridgeFromShoppingListFragment = AddToFridgeFromShoppingListFragment.newInstance(product);
        switchToFragment(addToFridgeFromShoppingListFragment);
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
        categoriesList = dbHelper.getAllCategoriesForShoppingList();
        shoppingListCategoryAdapter.updateCategories(categoriesList);
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

    public void btnHome(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void btnQrCodeScanner(View v){
        Intent intent = new Intent(this, QrCodeScannerActivity.class);
        startActivity(intent);
    }

    public void btnAnalytics(View v){
        Intent intent = new Intent(this, AnalyticsActivity.class);
        startActivity(intent);
    }
}