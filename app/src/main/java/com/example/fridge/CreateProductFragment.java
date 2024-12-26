package com.example.fridge;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import java.util.Calendar;
import java.util.Locale;

public class CreateProductFragment extends Fragment {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private ImageView backImageView;
    private TextInputEditText editTextName, editTextFirm, editTextType, editTextManufactureDate, editTextExpiryDate, editTextDays, editTextWeight, editTextQuantity;
    private EditText editTextProteins, editTextFats, editTextCarbohydrates, editTextCaloriesKcal, editTextCaloriesKJ;
    private TextView textViewAllergens;
    private RadioGroup radioGroup;
    private RadioButton radioBtnKg, radioBtnL;
    private ImageView imageViewMinusWeight, imageViewPlusWeight, imageViewMinusQuantity, imageViewPlusQuantity;
    private Button deleteBtn, addBtn;


    private OnFragmentInteractionListener mListener;

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
        View view = inflater.inflate(R.layout.fragment_create_product, container, false);

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

        textViewAllergens = view.findViewById(R.id.text_view_allergens);

        radioGroup = view.findViewById(R.id.radio_group);
        radioBtnKg = view.findViewById(R.id.radio_btn_kg);
        radioBtnL = view.findViewById(R.id.radio_btn_l);

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

        textViewAllergens.setOnClickListener(view1 -> {switchToFragmentCheckAllergens();});

        backImageView.setOnClickListener(view1 -> {
            // Вызываем метод активности для возобновления сканера
            if (mListener != null) {
                mListener.onFragmentClose(); // Возвращаем управление активности
            }
            // Закрываем фрагмент
            getActivity().getSupportFragmentManager().beginTransaction()
                    .remove(CreateProductFragment.this)
                    .commit();
        });

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
            Toast.makeText(getContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getContext(), "Продукт был успешно добавлен!", Toast.LENGTH_SHORT).show();
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
                return false;
            }
        }

        // Проверка выбора в RadioGroup
        if (radioGroup.getCheckedRadioButtonId() == -1) {
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

    //Метод вызывается, когда фрагмент отключается от активности.
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}