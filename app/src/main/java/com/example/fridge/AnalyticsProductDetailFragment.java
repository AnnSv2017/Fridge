package com.example.fridge;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AnalyticsProductDetailFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private AnalyticsProduct analyticsProduct;
    private DBHelper dbHelper;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private DateTimeFormatter formatterDB = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private InfoProductFragment infoProductFragment;
    private TextView textViewFullName, textViewAdd, textViewUsed, textViewOverdue;
    private RelativeLayout rlShowInfoProduct;
    private ImageView backButton, listSwitchImageView;
    private boolean infoProductIsExpended = false;


    public static AnalyticsProductDetailFragment newInstance(AnalyticsProduct analyticsProduct) {
        AnalyticsProductDetailFragment fragment = new AnalyticsProductDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("product", analyticsProduct); // Parcelable
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            analyticsProduct = getArguments().getParcelable("product");
        }
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
        View view = inflater.inflate(R.layout.fragment_analytics_product_detail, container, false);

        dbHelper = DBHelper.getInstance(requireContext());

        textViewFullName = view.findViewById(R.id.text_view_full_name);
        textViewAdd = view.findViewById(R.id.text_view_add);
        textViewUsed = view.findViewById(R.id.text_view_used);
        textViewOverdue = view.findViewById(R.id.text_view_overdue);
        rlShowInfoProduct = view.findViewById(R.id.rl_show_info_product);
        listSwitchImageView = view.findViewById(R.id.image_view_list_switch);
        backButton = view.findViewById(R.id.back_image_view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(analyticsProduct != null){
            int productId = analyticsProduct.getProductId();
            DataProduct dataProduct = dbHelper.getProductById(productId);

            // Транзакция
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            infoProductFragment = InfoProductFragment.newInstance(dataProduct, analyticsProduct.getManufactureDate(), analyticsProduct.getExpiryDate());
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

            textViewFullName.setText(dataProduct.getFull_name());
            textViewAdd.setText(String.valueOf(analyticsProduct.getAddLogsCount()));
            textViewUsed.setText(String.valueOf(analyticsProduct.getUsedLogsCount()));
            textViewOverdue.setText(String.valueOf(analyticsProduct.getOverdueLogsCount()));
            rlShowInfoProduct.setOnClickListener(v -> {infoProductOnClick();});
            backButton.setOnClickListener(v -> {backToActivity();});

        }
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

    private void backToActivity(){
        // Вызываем метод активности для закрытия фрагмента и открытия активности
        if (mListener != null) {
            mListener.onFragmentClose();
        }
        // Закрываем фрагмент
        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(AnalyticsProductDetailFragment.this)
                .commit();
    }

    //Метод вызывается, когда фрагмент отключается от активности.
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}