package com.example.fridge;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class QrCodeInfoFragment extends Fragment {

    private JsonObject jsonData;
    private String name, type, manufacture_date, expiry_date, mass_unit, measurement_type;
    private Double mass_value, proteins, fats, carbohydrates;
    private Integer calories_kcal, calories_KJ;

    private ImageView backImageView;
    private TextView textViewName, textViewType, textViewManufactureDate, textViewExpiryDate, textViewMass, textViewFoodValue, textViewAllergens, textViewMeasurementType;
    private Button closeButton;

    // Интерфейс для взаимодействия с активностью
    public interface OnFragmentInteractionListener {
        void onFragmentClose();
    }

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
            List<String> allergensList = new ArrayList<>();

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

        textViewName = view.findViewById(R.id.text_view_name);
        textViewType = view.findViewById(R.id.text_view_type);
        textViewManufactureDate = view.findViewById(R.id.text_view_manufacture_date);
        textViewExpiryDate = view.findViewById(R.id.text_view_expiry_date);
        textViewMass = view.findViewById(R.id.text_view_mass);
        textViewFoodValue = view.findViewById(R.id.text_view_food_value);
        textViewAllergens = view.findViewById(R.id.text_view_allergens);
        textViewMeasurementType = view.findViewById(R.id.text_view_measurement_type);

        textViewName.setText("Название:\n" + name);
        textViewType.setText("Тип:\n" + type);
        textViewManufactureDate.setText("Дата изготовления:\n" + manufacture_date);
        textViewExpiryDate.setText("Дата истечения срока:\n" + expiry_date);
        textViewMass.setText("масса/объём:\n" + mass_value + " " + mass_unit);
        textViewFoodValue.setText("Пищевая ценность, г:\n"
                + "Белки: " + proteins + "\nЖиры: " + fats + "\nУглеводы: " + carbohydrates
                + "\nКалории на 100г:\n " + calories_kcal + "ккал/" + calories_KJ + "кДж");

        closeButton = view.findViewById(R.id.close_button);

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Вызываем метод активности для возобновления сканера
                if (mListener != null) {
                    mListener.onFragmentClose(); // Возвращаем управление активности
                }
                // Закрываем фрагмент
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(QrCodeInfoFragment.this)
                        .commit();
            }
        });

        closeButton.setOnClickListener(v -> {
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

    //Метод вызывается, когда фрагмент отключается от активности.
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}