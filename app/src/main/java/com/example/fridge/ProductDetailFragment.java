package com.example.fridge;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProductDetailFragment extends Fragment {

    private OnFragmentInteractionExpendedListener mListener;
    private DataProductInFridge dataPIF;
    private DBHelper dbHelper;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private DateTimeFormatter formatterDB = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private InfoProductFragment infoProductFragment;
    private ImageView backButton, deleteButton;


    public static ProductDetailFragment newInstance(DataProductInFridge product) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("product", product); // Parcelable
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            dataPIF = getArguments().getParcelable("product");
        }
    }


    //Этот метод вызывается, когда фрагмент подключается к активности.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionExpendedListener) {
            mListener = (OnFragmentInteractionExpendedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionExpendedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        dbHelper = DBHelper.getInstance(requireContext());

        backButton = view.findViewById(R.id.back_image_view);
        deleteButton = view.findViewById(R.id.delete_image_view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(dataPIF != null){
            infoProductFragment = InfoProductFragment.newInstance(dataPIF);
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.info_product_fragment_container, infoProductFragment);
            ft.addToBackStack(null);
            ft.commit();

            backButton.setOnClickListener(v -> {backToActivity();});

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Удаляем продукт из холодильника
                    boolean isDeleted = dbHelper.deleteProductFromFridge(dataPIF.getId(), dataPIF.getQuantity());
                    if(isDeleted){
                        ToastUtils.showCustomToast(requireContext(), "Продукт был успешно удалён", "s");
                        // Находим тип удаления (истрачено или просрочено)
                        String currentDate = LocalDate.now().format(formatterDB);
                        String operationType;
                        LocalDate currentLocalDate = LocalDate.now();
                        LocalDate expiryLocalDate = LocalDate.parse(dataPIF.getExpiry_date(), formatterDB);
                        if(currentLocalDate.isBefore(expiryLocalDate) || currentLocalDate.isEqual(expiryLocalDate)){
                            operationType = "used";
                        } else {
                            operationType = "overdue";
                        }
                        // Фиксируем удаление в логах
                        DataProductLogs dataProductLogs = new DataProductLogs(0, currentDate, dataPIF.getManufacture_date(), dataPIF.getExpiry_date(), dataPIF.getProduct_id(), operationType, dataPIF.getQuantity());
                        dbHelper.addProductLogs(dataProductLogs);
                    } else {
                        ToastUtils.showCustomToast(requireContext(), "ERROR. Продукт НЕ был удалён", "e");
                    }
                    if(mListener != null){
                        mListener.onActivityUpdate();
                    }
                    backToActivity();
                }
            });
        }
    }

    private void backToActivity(){
        // Вызываем метод активности для закрытия фрагмента и открытия активности
        if (mListener != null) {
            mListener.onFragmentClose();
        }
        // Закрываем фрагмент
        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(ProductDetailFragment.this)
                .commit();
    }

    //Метод вызывается, когда фрагмент отключается от активности.
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}