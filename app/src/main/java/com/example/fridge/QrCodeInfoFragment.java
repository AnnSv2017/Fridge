package com.example.fridge;

import android.content.Context;
import android.os.Bundle;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QrCodeInfoFragment extends Fragment implements OnBackPressInQrCodeInfoFragmentListener {

    private DBHelper dbHelper;
    private DateTimeFormatter formatterDB = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private JsonObject jsonData;
    private String name, firm, type, manufacture_date, expiry_date, mass_unit, measurement_type;
    private Double mass_value, proteins, fats, carbohydrates;
    private Integer calories_kcal, calories_KJ;
    private List<String> allergensList;

    private ImageView backImageView, listSwitchImageView, minusQuantityImageView, plusQuantityImageView;

    private TextView textViewFullName, textViewWeightOne, textViewTotalWeight;
    private TextInputEditText editTextQuantity;
    private RelativeLayout rlShowInfoProduct;
    private Button addButton, deleteButton;

    private Fragment infoProductFragment;

    private boolean infoProductIsExpended = false;

    private OnFragmentInteractionListener mListener;

    private boolean productWasCreatedEarlier = false;
    @Override
    public int getProductIdIfProductWasNOTCreatedEarlier(){
        if(productWasCreatedEarlier){
            return -1; // Если продукт был создан ранее, ничего не делаем
        } else{
            // Возвращаем id продукта, чтобы удалить его из базы данных
            return dbHelper.getProductIdByFullName(type, name, firm, mass_value, mass_unit);
        }
    }

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

        dbHelper = DBHelper.getInstance(requireContext());

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
            ToastUtils.showCustomToast(requireContext(), "Ошибка: jsonData отсутствует", "e");
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

        addButton = view.findViewById(R.id.add_button);
        deleteButton = view.findViewById(R.id.delete_button);


        // Проверка на существование продукта как объекта
        DataProductInFridge dataProductInFridge = null;
        int product_id = dbHelper.getProductIdByFullName(type, name, firm, mass_value, mass_unit);
        if(product_id == -1){
            productWasCreatedEarlier = false;
            // Создаём оюъект продукта и добавляем в БД, если мы этот продукт решим не добавлять в холодильник или удалим, тогда и сам продукт как объект должегн быть удалён из ProductTable
            DataProduct dataProduct = new DataProduct(0, type, name, firm, mass_value, mass_unit, proteins, fats, carbohydrates, calories_kcal, calories_KJ, measurement_type);
            dbHelper.addProductIfNotExist(dataProduct);
            int new_product_id = dbHelper.getProductIdByFullName(type, name, firm, mass_value, mass_unit);
            int quantity = Integer.parseInt(editTextQuantity.getText().toString());
            dataProductInFridge = new DataProductInFridge(0, manufacture_date, expiry_date, new_product_id, quantity);

            // Если продукта вообще нет, то мы не можем его удалить -> скрываем кнопку удаления
            deleteButton.setVisibility(View.GONE);
        } else{
            productWasCreatedEarlier = true;
            // Проверка на существование продукта в холодильнике
            int productInFridgeId = dbHelper.getProductInFridgeIdIfItInFridge(manufacture_date, expiry_date, product_id);
            if(productInFridgeId == -1){
                int quantity = Integer.parseInt(editTextQuantity.getText().toString());
                dataProductInFridge = new DataProductInFridge(0, manufacture_date, expiry_date, product_id, quantity);

                // Если продукта нет в холодильнике, то мы не можем его удалить -> скрываем кнопку удаления
                deleteButton.setVisibility(View.GONE);
            } else{
                dataProductInFridge = dbHelper.getProductInFridgeById(productInFridgeId);
            }
        }

        // Транзакция
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        infoProductFragment = InfoProductFragment.newInstance(dataProductInFridge);
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
                    ToastUtils.showCustomToast(requireContext(), "Введите корректное число", "e");
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

        backImageView.setOnClickListener(v -> {backOnClick("backImageView");});

        addButton.setOnClickListener(v -> {addProductOnClick();});
        deleteButton.setOnClickListener(v -> {deleteProductOnClick();});
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

    private void backOnClick(String who){
        // Если мы вышли из фрагмента и продукт не создавался раннее
        if (who.equals("backImageView") && !productWasCreatedEarlier){
            // Удаляем созданный нами продукт как объект
            int created_product_id = dbHelper.getProductIdByFullName(type, name, firm, mass_value, mass_unit);
            dbHelper.deleteProduct(created_product_id);
        }
        // Вызываем метод активности для возобновления сканера
        if (mListener != null) {
            mListener.onFragmentClose(); // Возвращаем управление активности
        }
        // Закрываем фрагмент
        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(QrCodeInfoFragment.this)
                .commit();
    }

    private void addProductOnClick(){
        // Добавление в холодильник
        int product_id = dbHelper.getProductIdByFullName(type, name, firm, mass_value, mass_unit);
        int quantity = Integer.parseInt(editTextQuantity.getText().toString());
        DataProductInFridge dataProductInFridge = new DataProductInFridge(0, manufacture_date, expiry_date, product_id, quantity);
        dbHelper.addProductInFridge(dataProductInFridge);

        // Добавление типа если его до этого не было
        int typeId = dbHelper.getTypeIdByName(type);
        if(typeId == -1){
            dbHelper.addType(new DataType(0, type));
        }

        // Добавление в логи
        String currentDate = LocalDate.now().format(formatterDB);
        DataProductLogs dataProductLogs = new DataProductLogs(0, currentDate, manufacture_date, expiry_date, product_id, "add", quantity);
        dbHelper.addProductLogs(dataProductLogs);

        ToastUtils.showCustomToast(requireContext(), "Продукт был успешно добавлен", "s");

        backOnClick("addProductOnClick");
    }

    private void deleteProductOnClick(){
        // Откравается только тогда когда мы можем удалить продукт
        int productId = dbHelper.getProductIdByFullName(type, name, firm, mass_value, mass_unit);
        if(dbHelper.getProductInFridgeIdIfItInFridge(manufacture_date, expiry_date, productId) == -1){
            // Продукта нет в холодильнике
            ToastUtils.showCustomToast(requireContext(), "Данного продукта нет в холодильнике", "e");
            return;
        }
        // Продукт в холодильнике есть
        int productInFridgeId = dbHelper.getProductInFridgeIdIfItInFridge(manufacture_date, expiry_date, productId);
        int quantityToDelete = Integer.parseInt(editTextQuantity.getText().toString());
        boolean isDeleted = dbHelper.deleteProductFromFridge(productInFridgeId, quantityToDelete);
        if(isDeleted){
            ToastUtils.showCustomToast(requireContext(), "Продукт был успешно удалён", "s");
        } else{
            ToastUtils.showCustomToast(requireContext(), "Продукта в холодильнике нет в таком количестве", "e");
            return;
        }

        // Добавление в логи
        // Находим тип удаления (истрачено или просрочено)
        String currentDate = LocalDate.now().format(formatterDB);
        String operationType;
        LocalDate currentLocalDate = LocalDate.now();
        LocalDate expiryLocalDate = LocalDate.parse(expiry_date, formatterDB);
        if(currentLocalDate.isBefore(expiryLocalDate) || currentLocalDate.isEqual(expiryLocalDate)){
            operationType = "used";
        } else {
            operationType = "overdue";
        }
        DataProductLogs dataProductLogs = new DataProductLogs(0, currentDate, manufacture_date, expiry_date, productId, operationType, quantityToDelete);
        dbHelper.addProductLogs(dataProductLogs);

        backOnClick("deleteProductOnClick");
    }

    //Метод вызывается, когда фрагмент отключается от активности.
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}