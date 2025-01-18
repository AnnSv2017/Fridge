package com.example.fridge;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddToFridgeFromShoppingListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private DataProductInShoppingList dataPISL;
    private DataProduct dataProduct;
    private DBHelper dbHelper;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private TextView textViewFullName, textViewWeightOne, textViewTotalWeight;
    private TextInputEditText editTextManufactureDate, editTextExpiryDate, editTextDays, editTextQuantity;
    private ImageView backImageView, listSwitchImageView, minusQuantityImageView, plusQuantityImageView;
    private RelativeLayout rlShowInfoProduct;
    private Fragment infoProductFragment;
    private Button addButton, deleteButton;

    private boolean infoProductIsExpended = false;


    public static AddToFridgeFromShoppingListFragment newInstance(DataProductInShoppingList product) {
        AddToFridgeFromShoppingListFragment fragment = new AddToFridgeFromShoppingListFragment();
        Bundle args = new Bundle();
        args.putParcelable("product", product); // Parcelable
        fragment.setArguments(args);
        return fragment;
    }

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            dataPISL = getArguments().getParcelable("product");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_to_fridge_from_shopping_list, container, false);

        dbHelper = DBHelper.getInstance(requireContext());

        textViewFullName = view.findViewById(R.id.text_view_full_name);
        textViewWeightOne = view.findViewById(R.id.text_view_weight_one);
        textViewTotalWeight = view.findViewById(R.id.text_view_total_weight);
        editTextManufactureDate = view.findViewById(R.id.edit_text_manufacture_date);
        editTextExpiryDate = view.findViewById(R.id.edit_text_expiry_date);
        editTextDays = view.findViewById(R.id.edit_text_days);
        editTextQuantity = view.findViewById(R.id.edit_text_quantity);
        backImageView = view.findViewById(R.id.back_image_view);
        listSwitchImageView = view.findViewById(R.id.image_view_list_switch);
        minusQuantityImageView = view.findViewById(R.id.image_view_minus_quantity);
        plusQuantityImageView = view.findViewById(R.id.image_view_plus_quantity);
        rlShowInfoProduct = view.findViewById(R.id.rl_show_info_product);
        addButton = view.findViewById(R.id.add_button);
        deleteButton = view.findViewById(R.id.delete_button);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(dataPISL != null){
            int productId = dataPISL.getProduct_id();
            dataProduct = dbHelper.getProductById(productId);

            // Транзакция
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            infoProductFragment = InfoProductWithoutDatesFragment.newInstance(dataProduct);
            transaction.replace(R.id.info_product_fragment_container, infoProductFragment);
            transaction.commit(); // Асинхронная транзакция

            // Ожидаем завершения транзакции
            new Handler(Looper.getMainLooper()).post(() -> {
                if (infoProductFragment != null && infoProductFragment.getView() != null) {
                    infoProductFragment.getView().setVisibility(View.GONE);
                    infoProductFragment.getView().requestLayout();
                    Log.d("FragmentDebug", "Fragment hidden successfully");
                } else {
                    Log.e("FragmentError", "Fragment not found!");
                }
            });

            // Внесение изменений в поля
            String fullName = String.format("%s, %s, %s, %s %s", dataProduct.getType(), dataProduct.getName(), dataProduct.getFirm(), dataProduct.getMass_value(), dataProduct.getMass_unit());
            textViewFullName.setText(fullName);

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

            textViewWeightOne.setText(String.valueOf(dataProduct.getMass_value()) + " " + dataProduct.getMass_unit());

            editTextQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String quantityText = charSequence.toString();
                    if(quantityText != null && !quantityText.isEmpty()){
                        updateTotalWeight(charSequence.toString());
                    } else{
                        Toast.makeText(requireContext(), "Введите корректное число!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            minusQuantityImageView.setOnClickListener(v -> {
                decreaseQuantity(editTextQuantity);
                updateTotalWeight(editTextQuantity.getText().toString());
            });
            plusQuantityImageView.setOnClickListener(v -> {
                increaseQuantity(editTextQuantity);
                updateTotalWeight(editTextQuantity.getText().toString());
            });

            textViewTotalWeight.setText(String.valueOf(dataProduct.getMass_value()) + " " + dataProduct.getMass_unit());

            rlShowInfoProduct.setOnClickListener(v -> {infoProductOnClick();});

            backImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Вызываем метод активности для закрытия фрагмента и открытия активности
                    if (mListener != null) {
                        mListener.onFragmentClose();
                    }
                    // Закрываем фрагмент
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .remove(AddToFridgeFromShoppingListFragment.this)
                            .commit();
                }
            });


        }
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

    // TODO: Всё связанное с датами
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


    // TODO: Всё связанное с расчётом веса и количеством
    private void decreaseQuantity(EditText editText){
        Integer currentValue = Integer.valueOf(editText.getText().toString());
        if (currentValue > 0)
            editText.setText(String.valueOf(currentValue - 1));
    }

    private void increaseQuantity(EditText editText){
        Integer currentValue = Integer.valueOf(editText.getText().toString());
        editText.setText(String.valueOf(currentValue + 1));
    }

    private void updateTotalWeight(String quantityText) {
        int quantity = Integer.parseInt(quantityText);
        double totalWeight = quantity * dataProduct.getMass_value();

        // Преобразуем mass_value в строку, чтобы определить количество знаков после запятой
        String massValueStr = String.valueOf(dataProduct.getMass_value());
        int decimalPlaces = 0;

        if (massValueStr.contains(".")) {
            decimalPlaces = massValueStr.length() - massValueStr.indexOf('.') - 1;
        }

        // Форматируем результат с тем же количеством знаков после запятой
        String formatString = "%." + decimalPlaces + "f";
        String formattedWeight = String.format(Locale.US, formatString, totalWeight);

        textViewTotalWeight.setText(String.format("%s %s", formattedWeight, dataProduct.getMass_unit()));
    }

    // TODO: Показ информации о продукте
    private void infoProductOnClick(){
        infoProductIsExpended = !infoProductIsExpended;
        listSwitchImageView.setImageResource(infoProductIsExpended ? R.drawable.ic_open_list : R.drawable.ic_close_list);

        if(infoProductIsExpended){
            if (infoProductFragment != null && infoProductFragment.getView() != null) {
                infoProductFragment.getView().setVisibility(View.VISIBLE);
                infoProductFragment.getView().requestLayout();
            }
        } else{
            if (infoProductFragment != null && infoProductFragment.getView() != null) {
                infoProductFragment.getView().setVisibility(View.GONE);
                infoProductFragment.getView().requestLayout();
            }
        }
    }

    //Метод вызывается, когда фрагмент отключается от активности.
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}