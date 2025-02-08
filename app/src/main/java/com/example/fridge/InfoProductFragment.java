package com.example.fridge;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InfoProductFragment extends Fragment {

    private DBHelper dbHelper;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private DateTimeFormatter formatterDB = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DataProductInFridge dataPIF;
    private DataProduct dataP;
    private String manufacture_date, expiry_date;

    private TextView textViewName, textViewType, textViewFirm, textViewManufactureDate, textViewExpiryDate, textViewMass, textViewProteins, textViewFats, textViewCarbohydrates, textViewCalories, textViewAllergens, textViewMeasurementType;
    private String name, type, firm, mass_unit, measurement_type;
    private Double mass_value, proteins, fats, carbohydrates;
    private Integer calories_kcal, calories_KJ, quantity;
    private List<String> allergensList;

    // Метод для передачи DataProductInFridge
    public static InfoProductFragment newInstance(DataProductInFridge product) {
        InfoProductFragment fragment = new InfoProductFragment();
        Bundle args = new Bundle();
        args.putParcelable("product_in_fridge", product); // Parcelable
        fragment.setArguments(args);
        return fragment;
    }

    // Метод для передачи DataProduct и строк manufacture_date и expiry_date
    public static InfoProductFragment newInstance(DataProduct product, String manufactureDate, String expiryDate) {
        InfoProductFragment fragment = new InfoProductFragment();
        Bundle args = new Bundle();
        args.putParcelable("product", product); // Parcelable
        args.putString("manufacture_date", manufactureDate);
        args.putString("expiry_date", expiryDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            if (getArguments().containsKey("product_in_fridge")) {
                dataPIF = getArguments().getParcelable("product_in_fridge");
            } else if (getArguments().containsKey("product")) {
                dataP = getArguments().getParcelable("product");
                manufacture_date = getArguments().getString("manufacture_date");
                expiry_date = getArguments().getString("expiry_date");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_product, container, false);

        dbHelper = DBHelper.getInstance(requireContext());

        textViewName = view.findViewById(R.id.text_view_name);
        textViewType = view.findViewById(R.id.text_view_type);
        textViewFirm = view.findViewById(R.id.text_view_firm);
        textViewManufactureDate = view.findViewById(R.id.text_view_manufacture_date);
        textViewExpiryDate = view.findViewById(R.id.text_view_expiry_date);
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

        if (dataPIF != null) {
            displayDataFromFridge(dataPIF);
        } else if (dataP != null) {
            displayDataFromProduct(dataP, manufacture_date, expiry_date);
        }
    }

    private void displayDataFromFridge(DataProductInFridge product) {
        DataProduct dataProduct = dbHelper.getProductById(product.getProduct_id());
        populateFields(dataProduct);

        manufacture_date = product.getManufacture_date();
        expiry_date = product.getExpiry_date();
        quantity = product.getQuantity();

        String manufactureDate = LocalDate.parse(manufacture_date, formatterDB).format(formatter);
        String expiryDate = LocalDate.parse(expiry_date, formatterDB).format(formatter);
        textViewManufactureDate.setText(manufactureDate);
        textViewExpiryDate.setText(expiryDate);
    }

    private void displayDataFromProduct(DataProduct product, String manufactureDate, String expiryDate) {
        populateFields(product);

        String formattedManufactureDate = LocalDate.parse(manufactureDate, formatterDB).format(formatter);
        String formattedExpiryDate = LocalDate.parse(expiryDate, formatterDB).format(formatter);
        textViewManufactureDate.setText(formattedManufactureDate);
        textViewExpiryDate.setText(formattedExpiryDate);
    }

    private void populateFields(DataProduct product) {
        name = product.getName();
        type = product.getType();
        firm = product.getFirm();
        mass_value = product.getMass_value();
        mass_unit = product.getMass_unit();
        proteins = product.getProteins();
        fats = product.getFats();
        carbohydrates = product.getCarbohydrates();
        calories_kcal = product.getCalories_kcal();
        calories_KJ = product.getCalories_KJ();
        measurement_type = product.getMeasurement_type();

        allergensList = dbHelper.getAllAllergensByProductId(product.getId());

        textViewName.setText(name);
        textViewType.setText(type);
        textViewFirm.setText(firm);
        textViewMass.setText(String.format("%s %s", mass_value, mass_unit));
        textViewProteins.setText(String.format("%s г", proteins));
        textViewFats.setText(String.format("%s г", fats));
        textViewCarbohydrates.setText(String.format("%s г", carbohydrates));
        textViewCalories.setText(String.format("%s ккал/\n%s кДж", calories_kcal, calories_KJ));
        textViewAllergens.setText(allergensList != null && !allergensList.isEmpty() ? String.join(", ", allergensList) : "Нет аллергенов");
        textViewMeasurementType.setText(measurement_type);
    }
}