package com.example.fridge;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateProductFragment extends Fragment {

    private SharedViewModel viewModel;
    private View view;
    private DBHelper dbHelper;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private ImageView backImageView;
    private TextInputEditText editTextName, editTextFirm, editTextType, editTextManufactureDate, editTextExpiryDate, editTextDays, editTextWeight, editTextQuantity;
    private EditText editTextProteins, editTextFats, editTextCarbohydrates, editTextCaloriesKcal, editTextCaloriesKJ;
    private TextView textViewAllergensList;
    private RadioGroup radioGroupWeight, radioGroupMeasurementType;
    private RadioButton radioBtnKg, radioBtnL, radioBtnWeight, radioBtnQuantity;
    private ImageView imageViewMinusWeight, imageViewPlusWeight, imageViewMinusQuantity, imageViewPlusQuantity;
    private Button deleteBtn, addBtn, changeAllergensBtn;


    private OnFragmentInteractionListener mListener;

    // Для подсказок (поиска)
    private PopupWindow popupWindow;
    private RecyclerView.Adapter adapter;

    //Этот метод вызывается, когда фрагмент подключается к активности.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_product, container, false);

        dbHelper = DBHelper.getInstance(requireContext());

        backImageView = view.findViewById(R.id.back_image_view);

        editTextName = view.findViewById(R.id.edit_text_name);
        editTextFirm = view.findViewById(R.id.edit_text_firm);
        editTextType = view.findViewById(R.id.edit_text_type);
        editTextManufactureDate = view.findViewById(R.id.edit_text_manufacture_date);
        editTextExpiryDate = view.findViewById(R.id.edit_text_expiry_date);
        editTextDays = view.findViewById(R.id.edit_text_days);
        editTextWeight = view.findViewById(R.id.edit_text_weight);
        editTextQuantity = view.findViewById(R.id.edit_text_quantity);

        editTextProteins = view.findViewById(R.id.edit_text_proteins);
        editTextFats = view.findViewById(R.id.edit_text_fats);
        editTextCarbohydrates = view.findViewById(R.id.edit_text_carbohydrates);
        editTextCaloriesKcal = view.findViewById(R.id.edit_text_calories_kcal);
        editTextCaloriesKJ = view.findViewById(R.id.edit_text_calories_kj);

        textViewAllergensList = view.findViewById(R.id.text_view_allergens_list);

        radioGroupWeight = view.findViewById(R.id.radio_group_weight);
        radioBtnKg = view.findViewById(R.id.radio_btn_kg);
        radioBtnL = view.findViewById(R.id.radio_btn_l);

        radioGroupMeasurementType = view.findViewById(R.id.radio_group_measurement_type);
        radioBtnWeight = view.findViewById(R.id.radio_btn_weight);
        radioBtnQuantity = view.findViewById(R.id.radio_btn_quantity);

        changeAllergensBtn = view.findViewById(R.id.change_allergens_btn);
        deleteBtn = view.findViewById(R.id.delete_btn);
        addBtn = view.findViewById(R.id.add_btn);

        imageViewMinusWeight = view.findViewById(R.id.image_view_minus_weight);
        imageViewPlusWeight = view.findViewById(R.id.image_view_plus_weight);
        imageViewMinusQuantity = view.findViewById(R.id.image_view_minus_quantity);
        imageViewPlusQuantity = view.findViewById(R.id.image_view_plus_quantity);

        imageViewMinusWeight.setOnClickListener(view1 -> {decreaseWeight(editTextWeight);});
        imageViewPlusWeight.setOnClickListener(view1 -> {increaseWeight(editTextWeight);});
        imageViewMinusQuantity.setOnClickListener(view1 -> {decreaseQuantity(editTextQuantity);});
        imageViewPlusQuantity.setOnClickListener(view1 -> {increaseQuantity(editTextQuantity);});

        editTextManufactureDate.setOnClickListener(view1 -> {showDatePicker(editTextManufactureDate);});
        editTextExpiryDate.setOnClickListener(view1 -> {showDatePicker(editTextExpiryDate);});

        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                ArrayList<String> filteredSuggestions = getSuggestionsFromDatabase(s.toString());
//                suggestionsAdapter.updateData(filteredSuggestions);
//
//                if (!popupWindow.isShowing() && !filteredSuggestions.isEmpty()) {
//                    popupWindow.showAsDropDown(editTextName);
//                }
                showSuggestionNames(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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


        // Получаем ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Наблюдаем за изменениями списка аллергенов
        viewModel.getSelectedAllergens().observe(getViewLifecycleOwner(), allergens -> {
            if (allergens.isEmpty()) {
                textViewAllergensList.setText("Аллергенов нет");
            } else {
                textViewAllergensList.setText(String.join(", ", allergens));
            }
        });

        changeAllergensBtn.setOnClickListener(view1 -> {switchToFragmentCheckAllergens();});

        backImageView.setOnClickListener(view1 -> {backToQrCodeScannerActivity();});

        addBtn.setOnClickListener(view1 -> {addProduct();});

        return view;
    }

    private void decreaseWeight(EditText editText){
        BigDecimal currentValue = new BigDecimal(editText.getText().toString());
        editText.setText(String.valueOf(currentValue.subtract(BigDecimal.ONE)));
    }

    private void decreaseQuantity(EditText editText){
        Integer currentValue = Integer.valueOf(editText.getText().toString());
        if (currentValue > 0)
            editText.setText(String.valueOf(currentValue - 1));
    }

    private void increaseWeight(EditText editText){
        BigDecimal currentValue = new BigDecimal(editText.getText().toString());
        editText.setText(String.valueOf(currentValue.add(BigDecimal.ONE)));
    }

    private void increaseQuantity(EditText editText){
        Integer currentValue = Integer.valueOf(editText.getText().toString());
        editText.setText(String.valueOf(currentValue + 1));
    }


    private void showDatePicker(EditText editText) {
        // Получаем текущую дату
        Calendar calendar = Calendar.getInstance();

        // Создаем DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
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
        if (editTextManufactureDate.getText().toString().trim().isEmpty() || editTextExpiryDate.getText().toString().trim().isEmpty()){
            return;
        }
        LocalDate manufactureDate = LocalDate.parse(editTextManufactureDate.getText().toString(), formatter);
        LocalDate expiryDate = LocalDate.parse(editTextExpiryDate.getText().toString(), formatter);

        long differenceInDays = ChronoUnit.DAYS.between(manufactureDate, expiryDate);
        editTextDays.setText(String.valueOf(differenceInDays));
    }

    private void updateDates(){
        if(!editTextManufactureDate.getText().toString().trim().isEmpty()){
            String manufactureDate = editTextManufactureDate.getText().toString();

            long days;
            try {
                days = Long.parseLong(editTextDays.getText().toString());
            } catch (NumberFormatException e) {
                // Обработка ошибки: если строка не число
                Toast.makeText(getContext(), "Введите корректное число!", Toast.LENGTH_SHORT).show();
                return;
            }

            String expiryDateText = getModifiedDate(manufactureDate, days);
            editTextExpiryDate.setText(expiryDateText);
        }
    }

    private String getModifiedDate(String date, long daysToAdd){
        LocalDate initialDate = LocalDate.parse(date, formatter);
        LocalDate modifiedDate = initialDate.plusDays(daysToAdd);
        return modifiedDate.format(formatter);
    }

    private void addProduct(){
        //Если какое-то поле было не заполнены, то продукт не добавляется
        if (!areAllFieldsFilled()){
            return;
        }
        String type = editTextType.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String firm = editTextFirm.getText().toString().trim();
        Double mass_value = Double.parseDouble(editTextWeight.getText().toString().trim());

        RadioButton selectedRadioButton1 = view.findViewById(radioGroupWeight.getCheckedRadioButtonId());
        String mass_unit = selectedRadioButton1.getText().toString();

        Double proteins = Double.parseDouble(editTextProteins.getText().toString().trim());
        Double fats = Double.parseDouble(editTextFats.getText().toString().trim());
        Double carbohydrates = Double.parseDouble(editTextCarbohydrates.getText().toString().trim());
        int caloriesKcal = Integer.parseInt(editTextCaloriesKcal.getText().toString().trim());
        int caloriesKJ = Integer.parseInt(editTextCaloriesKJ.getText().toString().trim());

        RadioButton radioButton2 = view.findViewById(radioGroupMeasurementType.getCheckedRadioButtonId());
        String measurement_type = radioButton2.getText().toString();

        // Добавление продукта как разновидности в БД если ещё не добавлен
        DataProduct dataProduct = new DataProduct(0, type, name, firm, mass_value, mass_unit, proteins, fats, carbohydrates, caloriesKcal, caloriesKJ, measurement_type);

        // Если продукта ещё нет в БД
        if(!dbHelper.productAlreadyExist(dataProduct)){
            // Добавляем продукт
            dbHelper.addProductIfNotExist(dataProduct);

            // Получение id продукта и Связывание продукта со списком аллергенов
            int productId = dbHelper.getProductIdByFullName(type, name, firm, mass_value, mass_unit);
            viewModel.getSelectedAllergens().observe(getViewLifecycleOwner(), allergensList ->{
                for(String allergen : allergensList){
                    DataProductAllergens dataProductAllergens = new DataProductAllergens(0, productId, allergen);
                    dbHelper.addProductAllergens(dataProductAllergens);
                }
            });
        }

        // Добавление продукта в холодильник
        String manufacture_date = editTextManufactureDate.getText().toString();
        String expiry_date = editTextExpiryDate.getText().toString();
        int productId = dbHelper.getProductIdByFullName(type, name, firm, mass_value, mass_unit);
        int quantity = Integer.parseInt(editTextQuantity.getText().toString());
        DataProductInFridge dataProductInFridge = new DataProductInFridge(0, manufacture_date, expiry_date, productId, quantity);
        dbHelper.addProductInFridge(dataProductInFridge);

        // Добавление продукта в логи с датой
        DataProductLogs dataProductLogs = new DataProductLogs(0, manufacture_date, expiry_date, productId, "add", quantity);
        dbHelper.addProductLogs(dataProductLogs);

        Toast.makeText(getContext(), "Продукт был успешно добавлен!", Toast.LENGTH_SHORT).show();

        // Выходим из фрагмента
        backToQrCodeScannerActivity();
    }

    private boolean areAllFieldsFilled(){
        // Список всех полей для проверки
        EditText[] requiredFields = {
                editTextName, editTextFirm, editTextType,
                editTextManufactureDate, editTextExpiryDate,
                editTextWeight, editTextQuantity,
                editTextProteins, editTextFats,
                editTextCarbohydrates, editTextCaloriesKcal,
                editTextCaloriesKJ
        };

        // Проверка заполненности текстовых полей
        for (EditText field : requiredFields) {
            if (field.getText().toString().trim().isEmpty()) {
                Toast.makeText(getContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // Проверка корректности введённых данных в поля editTextWeight, editTextQuantity и пищевой ценности
        try{
            Double.parseDouble(editTextWeight.getText().toString());
        } catch (Exception e){
            Toast.makeText(getContext(), "Введите корректный вес товара!", Toast.LENGTH_SHORT).show();
            return false;
        }

        try{
            Integer.parseInt(editTextQuantity.getText().toString());
        } catch (Exception e){
            Toast.makeText(getContext(), "Введите корректное количество товара!", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Double.parseDouble(editTextProteins.getText().toString());
            Double.parseDouble(editTextFats.getText().toString());
            Double.parseDouble(editTextCarbohydrates.getText().toString());
            Integer.parseInt(editTextCaloriesKcal.getText().toString());
            Integer.parseInt(editTextCaloriesKJ.getText().toString());
        } catch (Exception e){
            Toast.makeText(getContext(), "Введите корректную пищевую/энергетическую ценность!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Проверка выбора в RadioGroupWeight и RadioGroupMeasurementType
        if (radioGroupWeight.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Выберите единицу измерения количества товара (л/кг)", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(radioGroupMeasurementType.getCheckedRadioButtonId() == -1){
            Toast.makeText(getContext(), "Выберите тип измерения!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Все поля заполнены
        return true;
    }

    // Метод для перехода в фрагмент, где можно выбрать аллергены
    private void switchToFragmentCheckAllergens(){
        // Получаем экземпляр FragmentManager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Начинаем транзакцию фрагмента
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Заменяем текущий фрагмент на новый
        transaction.replace(R.id.fragment_container, new CheckAllergensFragment());

        // Добавляем транзакцию в стек, чтобы можно было вернуться назад
        transaction.addToBackStack(null);

        // Выполняем транзакцию
        transaction.commit();

    }

    // TODO: Всплывающие подсказки
    // Поиск по полному имени
    private void showSuggestionNames(String query) {
        ArrayList<String> suggestionNames = dbHelper.searchProducts(query);

        if (suggestionNames.isEmpty()) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            return;
        }

        if (popupWindow == null || !popupWindow.isShowing()) {
            RecyclerView recyclerView = new RecyclerView(requireContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

            adapter = new SuggestionNamesAdapter(suggestionNames, selectedValue -> {
                fillFields(selectedValue);
                //editTextName.setText(selectedValue);
                popupWindow.dismiss();
            });

            recyclerView.setAdapter(adapter);

            popupWindow = new PopupWindow(recyclerView, editTextName.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            popupWindow.setElevation(10); // Задаем стиль
            popupWindow.setFocusable(false); // Окно не перехватывает фоку
            popupWindow.setOutsideTouchable(true); // Позволяет закрывать окно при нажатии вне его области

            popupWindow.showAsDropDown(editTextName);
        } else {
            ((SuggestionNamesAdapter) adapter).updateData(suggestionNames);
        }
    }

    private void fillFields(String fullName){
        String[] parts = fullName.split(", ");
        String type = parts[0];
        String name = parts[1];
        String firm = parts[2];
        String mass_value = parts[3].split(" ")[0];
        String mass_unit = parts[3].split(" ")[1];

        int productId = dbHelper.getProductIdByFullName(type, name, firm, Double.parseDouble(mass_value), mass_unit);
        DataProduct dataProduct = dbHelper.getProductById(productId);

        editTextName.setText(dataProduct.getName());
        editTextType.setText(dataProduct.getType());
        editTextFirm.setText(dataProduct.getFirm());
        editTextWeight.setText(mass_value);
        switch (mass_unit){
            case "кг":
                radioGroupWeight.check(R.id.radio_btn_kg);
                break;
            case "л":
                radioGroupWeight.check(R.id.radio_btn_l);
                break;
        }
        switch (dataProduct.getMeasurement_type()){
            case "вес":
                radioGroupMeasurementType.check(R.id.radio_btn_weight);
                break;
            case "штуки":
                radioGroupMeasurementType.check(R.id.radio_btn_quantity);
                break;
        }
        editTextProteins.setText(String.valueOf(dataProduct.getProteins()));
        editTextFats.setText(String.valueOf(dataProduct.getFats()));
        editTextCarbohydrates.setText(String.valueOf(dataProduct.getCarbohydrates()));
        editTextCaloriesKcal.setText(String.valueOf(dataProduct.getCalories_kcal()));
        editTextCaloriesKJ.setText(String.valueOf(dataProduct.getCalories_KJ()));

        ArrayList<String> allergens = dbHelper.getAllAllergensByProductId(productId);
        viewModel.setSelectedAllergens(allergens);
    }

    // Метод для выхода из фрагмента
    private void backToQrCodeScannerActivity(){
        // Вызываем метод активности для возобновления сканера
        if (mListener != null) {
            mListener.onFragmentClose(); // Возвращаем управление активности
        }
        // Закрываем фрагмент
        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(CreateProductFragment.this)
                .commit();
    }

    //Метод вызывается, когда фрагмент отключается от активности.
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}