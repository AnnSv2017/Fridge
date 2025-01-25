package com.example.fridge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;

public class AnalyticsActivity extends AppCompatActivity implements AnalyticsCategoryAdapter.OnProductClickListener, OnFragmentInteractionListener {

    private DBHelper dbHelper;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private DateTimeFormatter formatterDB = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ArrayList<Category> categoriesList;
    private AnalyticsCategoryAdapter categoryAdapter;

    private PopupWindow popupWindow;
    private RecyclerView.Adapter adapter;

    private EditText editTextSearch;
    private ImageView imageViewErase;
    private TextInputEditText editTextFirstDate, editTextLastDate, editTextDays;
    private ListView listViewCategories;

    private boolean searchHasFullName = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        dbHelper = DBHelper.getInstance(this);

        // Выбор дат
        editTextFirstDate = findViewById(R.id.edit_text_first_date);
        editTextLastDate = findViewById(R.id.edit_text_last_date);
        editTextDays = findViewById(R.id.edit_text_days);

        // Настраиваем даты
        editTextFirstDate.setOnClickListener(view1 -> {showDatePicker(editTextFirstDate);});
        editTextLastDate.setOnClickListener(view1 -> {showDatePicker(editTextLastDate);});

        editTextDays.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Вызывается при каждом изменении текста
                updateDates();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Адаптер и ListView
        if(editTextFirstDate.getText().toString().isEmpty() || editTextLastDate.getText().toString().isEmpty()){
            categoriesList = dbHelper.getAllCategoriesForAnalytics();
        } else {
            String firstDate = LocalDate.parse(editTextFirstDate.getText().toString(), formatter).format(formatterDB);
            String lastDate = LocalDate.parse(editTextLastDate.getText().toString(), formatter).format(formatterDB);
            categoriesList = dbHelper.getAllCategoriesByDatesForAnalytics(firstDate, lastDate);
        }
        categoryAdapter = new AnalyticsCategoryAdapter(this, categoriesList, this);

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
                searchHasFullName = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imageViewErase.setOnClickListener(v -> {
            editTextSearch.setText("");
            searchHasFullName = false;
            updateScreen();
        });
    }

    private void updateScreen(){
        if(editTextFirstDate.getText().toString().isEmpty() || editTextLastDate.getText().toString().isEmpty()){
            categoriesList = dbHelper.getAllCategoriesForAnalytics();
        } else {
            String firstDate = LocalDate.parse(editTextFirstDate.getText().toString(), formatter).format(formatterDB);
            String lastDate = LocalDate.parse(editTextLastDate.getText().toString(), formatter).format(formatterDB);
            if(searchHasFullName){
                String fullName = editTextSearch.getText().toString();
                categoriesList = dbHelper.getCategoryForAnalyticsByFullNameWithDates(fullName, firstDate, lastDate);
            } else{
                categoriesList = dbHelper.getAllCategoriesByDatesForAnalytics(firstDate, lastDate);
            }
        }
        categoryAdapter.updateCategories(categoriesList);
    }

    @Override
    public void onProductClick(AnalyticsProduct analyticsProduct) {
        // Обработка нажатия на элемент списка продуктов
        AnalyticsProductDetailFragment fragment = AnalyticsProductDetailFragment.newInstance(analyticsProduct);
        switchToFragment(fragment);
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

    // Даты
    private void showDatePicker(EditText editText) {
        // Получаем текущую дату
        Calendar calendar = Calendar.getInstance();

        // Создаем DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Устанавливаем выбранную дату в EditText
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);

                        // Преобразуем Calendar в LocalDate
                        LocalDate localDate = LocalDate.of(year, month + 1, dayOfMonth);

                        editText.setText(localDate.format(formatter));

                        updateDays();
                    }
                },
                calendar.get(Calendar.YEAR), // Год по умолчанию
                calendar.get(Calendar.MONTH), // Месяц по умолчанию
                calendar.get(Calendar.DAY_OF_MONTH) // День по умолчанию
        );

        // Показываем диалог
        datePickerDialog.show();
    }

    private void updateDays(){
        if (editTextFirstDate.getText().toString().trim().isEmpty() || editTextLastDate.getText().toString().trim().isEmpty()){
            return;
        }
        LocalDate firstDate = LocalDate.parse(editTextFirstDate.getText().toString(), formatter);
        LocalDate lastDate = LocalDate.parse(editTextLastDate.getText().toString(), formatter);

        long differenceInDays = ChronoUnit.DAYS.between(firstDate, lastDate);
        editTextDays.setText(String.valueOf(differenceInDays));

        // Когда обнавляются дни, значит две даты поменялись! Следовательно вызываем метод обновления ListView
        updateScreen();
    }

    private void updateDates(){
        if(!editTextFirstDate.getText().toString().trim().isEmpty()){
            String firstDate = editTextFirstDate.getText().toString();

            long days;
            try {
                days = Long.parseLong(editTextDays.getText().toString());
            } catch (NumberFormatException e) {
                // Обработка ошибки: если строка не число
                ToastUtils.showCustomToast(this, "Введите корректное число", "e");
                return;
            }

            String lastDateText = getModifiedDate(firstDate, days);
            editTextLastDate.setText(lastDateText);
        }
    }

    private String getModifiedDate(String date, long daysToAdd){
        LocalDate initialDate = LocalDate.parse(date, formatter);
        LocalDate modifiedDate = initialDate.plusDays(daysToAdd);
        return modifiedDate.format(formatter);
    }

    // Поисковик
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
                editTextSearch.setText(selectedValue);
                showSelectedProduct(selectedValue);
                searchHasFullName = true;
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

    private void showSelectedProduct(String fullName){
        // Обновляем ListView
        if(editTextFirstDate.getText().toString().isEmpty() || editTextLastDate.getText().toString().isEmpty()){
            Log.i("showSelectedProduct", "One of dates is null");
            categoriesList = dbHelper.getCategoryForAnalyticsByFullName(fullName);
        } else{
            Log.i("showSelectedProduct", "Both of dates are not null");
            String firstDate = LocalDate.parse(editTextFirstDate.getText().toString(), formatter).format(formatterDB);
            String lastDate = LocalDate.parse(editTextLastDate.getText().toString(), formatter).format(formatterDB);
            categoriesList = dbHelper.getCategoryForAnalyticsByFullNameWithDates(fullName, firstDate, lastDate);
        }
        categoryAdapter.updateCategories(categoriesList);
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

    public void btnShoppingList(View v){
        Intent intent = new Intent(this, ShoppingListActivity.class);
        startActivity(intent);
    }
}