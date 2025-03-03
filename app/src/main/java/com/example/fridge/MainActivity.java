package com.example.fridge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.OnProductClickListener, OnFragmentInteractionExpendedListener {

    private DBHelper dbHelper;

    private ArrayList<Category> categoriesList;
    private CategoryAdapter categoryAdapter;

    private PopupWindow popupWindow;
    private RecyclerView.Adapter adapter;

    private ListView listViewCategories;
    private EditText editTextSearch;
    private ImageView imageViewErase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Уведомления
        createNotificationChannel(this);
        // Запросить разрешение на отправку уведомлений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Запрашиваем разрешение
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        101); // 101 — любой уникальный код запроса
            }
        }
        scheduleDailyCheck();

        dbHelper = DBHelper.getInstance(this);

        // ListView
        categoriesList = dbHelper.getAllCategoriesForFridge();
        if (categoriesList == null || categoriesList.isEmpty()) {
            Log.e("MainActivity", "categoriesList is empty or null!");
        } else {
            Log.d("MainActivity", "categoriesList size: " + categoriesList.size());
            Log.d("MainActivity", "productsInFridgeList size: " + dbHelper.getAllProductsInFridge().size());
        }
        categoryAdapter = new CategoryAdapter(this, categoriesList, this);

        listViewCategories = findViewById(R.id.list_view_categories);
        listViewCategories.setAdapter(categoryAdapter);

        // Поисковик
        editTextSearch = findViewById(R.id.edit_text_search);
        imageViewErase = findViewById(R.id.image_view_erase);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                showSuggestionNames(charSequence.toString());
                showPossibleProductsInFridge(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imageViewErase.setOnClickListener(v -> {
            editTextSearch.setText("");
            categoriesList = dbHelper.getAllCategoriesForFridge();
            categoryAdapter.updateCategories(categoriesList);
        });
    }

    private void showSuggestionNames(String query) {
        ArrayList<String> suggestionNames = dbHelper.searchProducts(query);

        if (suggestionNames.isEmpty()) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            return;
        }

        if (popupWindow == null || !popupWindow.isShowing()) {
            RecyclerView recyclerView = new RecyclerView(this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            adapter = new SuggestionNamesAdapter(suggestionNames, selectedValue -> {
                //fillFields(selectedValue);
                editTextSearch.setText(selectedValue);
                showSelectedProductsInFridge(selectedValue);
                popupWindow.dismiss();
            });

            recyclerView.setAdapter(adapter);

            popupWindow = new PopupWindow(recyclerView, editTextSearch.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            popupWindow.setElevation(10); // Задаем стиль
            popupWindow.setFocusable(false); // Окно не перехватывает фокус
            popupWindow.setOutsideTouchable(true); // Позволяет закрывать окно при нажатии вне его области

            popupWindow.showAsDropDown(editTextSearch);
        } else {
            ((SuggestionNamesAdapter) adapter).updateData(suggestionNames);
        }
    }

    private void showPossibleProductsInFridge(String fullName){
        // Обновляем ListView
        categoriesList = dbHelper.getAllCategoriesForFridgeByQuery(fullName);
        categoryAdapter.updateCategories(categoriesList);
    }
    private void showSelectedProductsInFridge(String fullName){
        // Обновляем ListView
        categoriesList = dbHelper.getCategoryForFridgeByFullName(fullName);
        categoryAdapter.updateCategories(categoriesList);
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
    public void onActivityUpdate(){
        // Обновляем ListView
        if(editTextSearch.getText().toString().isEmpty()){
            categoriesList = dbHelper.getAllCategoriesForFridge();
            categoryAdapter.updateCategories(categoriesList);
        }
        else{
            categoriesList = dbHelper.getAllCategoriesForFridgeByQuery(editTextSearch.getText().toString());
            categoryAdapter.updateCategories(categoriesList);
        }
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

    public void btnAnalytics(View v){
        Intent intent = new Intent(this, AnalyticsActivity.class);
        startActivity(intent);
    }

    public void btnShoppingList(View v){
        Intent intent = new Intent(this, ShoppingListActivity.class);
        startActivity(intent);
    }

    // TODO: Уведомления

    //Вызывается, когда пользователь отвечает на запрос разрешений.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("Notifications", "Разрешение на уведомления предоставлено");
            } else {
                Log.w("Notifications", "Разрешение на уведомления отклонено");
            }
        }
    }

    // Создание канала для уведомлений
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "FRIDGE_CHANNEL";
            String channelName = "Fridge Notifications";
            String channelDescription = "Уведомления о продуктах в холодильнике";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    // Метод для проверку испорченности продуктов
    private void scheduleDailyCheck() {
        // Установите время выполнения (полночь)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Рассчитываем начальную задержку
        long initialDelay = calendar.getTimeInMillis() - System.currentTimeMillis();
        if (initialDelay < 0) {
            // Если текущее время позже настроенного времени, добавляем 1 день
            initialDelay += TimeUnit.DAYS.toMillis(1);
        }

        // Создаем задачу с временем
        PeriodicWorkRequest dailyWorkRequest = new PeriodicWorkRequest.Builder(
                ExpiryCheckWorker.class,
                1, TimeUnit.DAYS // Интервал выполнения
        ).setInitialDelay(initialDelay, TimeUnit.MILLISECONDS).build(); // Задержка перед первым запуском

        // Планируем задачу с уникальным именем
        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(
                "ExpiryCheck",
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyWorkRequest
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Закрываем БД
        DBManager.getInstance(this).closeDatabase();
    }
}