package com.example.fridge;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

public class QrCodeInfoFragment extends Fragment {

    private JsonObject jsonData;
    private String name, firm, type, manufacture_date, expiry_date, mass_unit, measurement_type;
    private Double mass_value, proteins, fats, carbohydrates;
    private Integer calories_kcal, calories_KJ;
    private List<String> allergensList;

    private ImageView backImageView, listSwitchImageView, minusQuantityImageView, plusQuantityImageView;

    private TextView textViewFullName, textViewWeightOne, textViewTotalWeight;
    private TextInputEditText editTextQuantity;
    //private TextView textViewName, textViewType, textViewManufactureDate, textViewExpiryDate, textViewMass, textViewProteins, textViewFats, textViewCarbohydrates, textViewCalories, textViewAllergens, textViewMeasurementType;
    private RelativeLayout rlShowInfoProduct;
    private Button addButton, deleteButton;

    private Fragment infoProductFragCont;

    private boolean infoProductIsExpended = false;

    private OnFragmentInteractionListener mListener;

    public QrCodeInfoFragment(JsonObject jsonData) {
        this.jsonData = jsonData;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Загружаем готовый макет фрагмента
        View view = inflater.inflate(R.layout.fragment_qr_code_info, container, false);

        // Инициализация данных
        if (jsonData != null) {
            name = jsonData.get("name").getAsString();
            firm = jsonData.get("firm").getAsString();
            type = jsonData.get("type").getAsString();
            manufacture_date = jsonData.get("manufacture_date").getAsString();
            expiry_date = jsonData.get("expiry_date").getAsString();

            // Извлечение значений из объекта mass
            JsonObject massObject = jsonData.getAsJsonObject("mass");
            mass_value = massObject.get("value").getAsDouble();
            mass_unit = massObject.get("unit").getAsString();

            // Извлечение значений из объекта food_value
            JsonObject foodValueObject = jsonData.getAsJsonObject("food_value");
            proteins = foodValueObject.get("proteins").getAsDouble();
            fats = foodValueObject.get("fats").getAsDouble();
            carbohydrates = foodValueObject.get("carbohydrates").getAsDouble();
            calories_kcal = foodValueObject.get("calories_kcal").getAsInt();
            calories_KJ = foodValueObject.get("calories_KJ").getAsInt();

            // Извлекаем данные из объекта allergens в список аллергенов
            allergensList = new ArrayList<>();

            if (jsonData.has("allergens")) {
                JsonArray allergensArray = jsonData.getAsJsonArray("allergens");
                for (int i = 0; i < allergensArray.size(); i++) {
                    String allergen = allergensArray.get(i).getAsString();
                    allergensList.add(allergen);
                }
            }

            measurement_type = jsonData.get("measurement_type").getAsString();
        } else {
            // Обработка ошибки, если jsonData null
            Toast.makeText(getContext(), "Ошибка: jsonData отсутствует", Toast.LENGTH_SHORT).show();
        }

        backImageView = view.findViewById(R.id.back_image_view);
        minusQuantityImageView = view.findViewById(R.id.image_view_minus_quantity);
        plusQuantityImageView = view.findViewById(R.id.image_view_plus_quantity);

        textViewFullName = view.findViewById(R.id.text_view_full_name);
        textViewWeightOne = view.findViewById(R.id.text_view_weight_one);
        textViewTotalWeight = view.findViewById(R.id.text_view_total_weight);

        editTextQuantity = view.findViewById(R.id.edit_text_quantity);
        rlShowInfoProduct = view.findViewById(R.id.rl_show_info_product);
        listSwitchImageView = view.findViewById(R.id.image_view_list_switch);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_product_fragment_container, new InfoProductFragment());
        transaction.commit(); // Асинхронная транзакция

        // Ожидаем завершения транзакции
        new Handler(Looper.getMainLooper()).post(() -> {
            infoProductFragCont = getChildFragmentManager().findFragmentById(R.id.info_product_fragment_container);
            if (infoProductFragCont != null && infoProductFragCont.getView() != null) {
                fillAllFieldsInfoProduct(infoProductFragCont.getView());
                infoProductFragCont.getView().setVisibility(View.GONE);
                infoProductFragCont.getView().requestLayout();
                Log.d("FragmentDebug", "Fragment hidden successfully");
            } else {
                Log.e("FragmentError", "Fragment not found!");
            }
        });

        // Внесение изменений в поля
        String fullName = String.format("%s, %s, %s, %s %s", type, name, firm, mass_value, mass_unit);
        textViewFullName.setText(fullName);

        textViewWeightOne.setText(String.valueOf(mass_value) + " " + mass_unit);

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

        textViewTotalWeight.setText(String.valueOf(mass_value) + " " + mass_unit);

        rlShowInfoProduct.setOnClickListener(v -> {infoProductOnClick();});

//        addButton = view.findViewById(R.id.add_button);
//        deleteButton = view.findViewById(R.id.delete_button);

        backImageView.setOnClickListener(view1 -> {
            // Вызываем метод активности для возобновления сканера
            if (mListener != null) {
                mListener.onFragmentClose(); // Возвращаем управление активности
            }
            // Закрываем фрагмент
            getActivity().getSupportFragmentManager().beginTransaction()
                    .remove(QrCodeInfoFragment.this)
                    .commit();
        });

        return view;
    }

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
        double totalWeight = quantity * mass_value;

        // Преобразуем mass_value в строку, чтобы определить количество знаков после запятой
        String massValueStr = String.valueOf(mass_value);
        int decimalPlaces = 0;

        if (massValueStr.contains(".")) {
            decimalPlaces = massValueStr.length() - massValueStr.indexOf('.') - 1;
        }

        // Форматируем результат с тем же количеством знаков после запятой
        String formatString = "%." + decimalPlaces + "f";
        String formattedWeight = String.format(Locale.US, formatString, totalWeight);

        textViewTotalWeight.setText(String.format("%s %s", formattedWeight, mass_unit));
    }

    private void infoProductOnClick(){
        infoProductIsExpended = !infoProductIsExpended;
        listSwitchImageView.setImageResource(infoProductIsExpended ? R.drawable.ic_open_list : R.drawable.ic_close_list);

        if(infoProductIsExpended){
            if (infoProductFragCont != null && infoProductFragCont.getView() != null) {
                infoProductFragCont.getView().setVisibility(View.VISIBLE);
                infoProductFragCont.getView().requestLayout();
            }
        } else{
            if (infoProductFragCont != null && infoProductFragCont.getView() != null) {
                infoProductFragCont.getView().setVisibility(View.GONE);
                infoProductFragCont.getView().requestLayout();
            }
        }
    }

    private void fillAllFieldsInfoProduct(View viewFragment){
        TextView textViewName = viewFragment.findViewById(R.id.text_view_name);
        TextView textViewType = viewFragment.findViewById(R.id.text_view_type);
        TextView textViewFirm = viewFragment.findViewById(R.id.text_view_firm);
        TextView textViewManufactureDate = viewFragment.findViewById(R.id.text_view_manufacture_date);
        TextView textViewExpiryDate = viewFragment.findViewById(R.id.text_view_expiry_date);
        TextView textViewMass = viewFragment.findViewById(R.id.text_view_mass);
        TextView textViewProteins = viewFragment.findViewById(R.id.text_view_proteins);
        TextView textViewFats = viewFragment.findViewById(R.id.text_view_fats);
        TextView textViewCarbohydrates = viewFragment.findViewById(R.id.text_view_carbohydrates);
        TextView textViewCalories = viewFragment.findViewById(R.id.text_view_calories);
        TextView textViewAllergens = viewFragment.findViewById(R.id.text_view_allergens);
        TextView textViewMeasurementType = viewFragment.findViewById(R.id.text_view_measurement_type);

        textViewName.setText(name);
        textViewType.setText(type);
        textViewFirm.setText(firm);
        textViewManufactureDate.setText(manufacture_date);
        textViewExpiryDate.setText(expiry_date);
        textViewMass.setText(String.valueOf(mass_value) + " " + mass_unit);
        textViewProteins.setText(String.valueOf(proteins) + " г");
        textViewFats.setText(String.valueOf(fats) + " г");
        textViewCarbohydrates.setText(String.valueOf(carbohydrates) + " г");
        String calories_text = String.format("%s ккал/\n%s кДж", calories_kcal, calories_KJ);
        textViewCalories.setText(calories_text);
        if (allergensList != null && !allergensList.isEmpty()) {
            textViewAllergens.setText(String.join(", ", allergensList));
        } else{
            textViewAllergens.setText("Нет аллергенов");
        }
        textViewMeasurementType.setText(measurement_type);
    }

    //Метод вызывается, когда фрагмент отключается от активности.
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}