package com.example.fridge;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class InfoProductWithoutDatesFragment extends Fragment {
    private DBHelper dbHelper;
    private DataProduct dataProduct;

    private TextView textViewName, textViewType, textViewFirm, textViewMass, textViewProteins, textViewFats, textViewCarbohydrates, textViewCalories, textViewAllergens, textViewMeasurementType;
    private String name, type, firm, mass_unit, measurement_type;
    private Double mass_value, proteins, fats, carbohydrates;
    private Integer calories_kcal, calories_KJ, quantity;
    private List<String> allergensList;

    public static InfoProductWithoutDatesFragment newInstance(DataProduct product) {
        InfoProductWithoutDatesFragment fragment = new InfoProductWithoutDatesFragment();
        Bundle args = new Bundle();
        args.putParcelable("product", product); // Parcelable
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            dataProduct = getArguments().getParcelable("product");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_product_without_dates, container, false);

        dbHelper = DBHelper.getInstance(requireContext());

        textViewName = view.findViewById(R.id.text_view_name);
        textViewType = view.findViewById(R.id.text_view_type);
        textViewFirm = view.findViewById(R.id.text_view_firm);
        textViewMass = view.findViewById(R.id.text_view_mass);
        textViewProteins = view.findViewById(R.id.text_view_proteins);
        textViewFats = view.findViewById(R.id.text_view_fats);
        textViewCarbohydrates = view.findViewById(R.id.text_view_carbohydrates);
        textViewCalories = view.findViewById(R.id.text_view_calories);
        textViewAllergens = view.findViewById(R.id.text_view_allergens);
        textViewMeasurementType = view.findViewById(R.id.text_view_measurement_type);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(dataProduct != null){
            name = dataProduct.getName();
            type = dataProduct.getType();
            firm = dataProduct.getFirm();
            mass_value = dataProduct.getMass_value();
            mass_unit = dataProduct.getMass_unit();
            proteins = dataProduct.getProteins();
            fats = dataProduct.getFats();
            carbohydrates = dataProduct.getCarbohydrates();
            calories_kcal = dataProduct.getCalories_kcal();
            calories_KJ = dataProduct.getCalories_KJ();
            measurement_type = dataProduct.getMeasurement_type();

            allergensList = dbHelper.getAllAllergensByProductId(dataProduct.getId());

            textViewName.setText(name);
            textViewType.setText(type);
            textViewFirm.setText(firm);
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
    }
}