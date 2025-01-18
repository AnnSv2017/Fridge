package com.example.fridge;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductDetailFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private DataProductInFridge dataPIF;

    private InfoProductFragment infoProductFragCont;
    private ImageView backButton;


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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        backButton = view.findViewById(R.id.back_image_view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(dataPIF != null){
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Вызываем метод активности для закрытия фрагмента и открытия активности
                    if (mListener != null) {
                        mListener.onFragmentClose();
                    }
                    // Закрываем фрагмент
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .remove(ProductDetailFragment.this)
                            .commit();
                }
            });

            infoProductFragCont = InfoProductFragment.newInstance(dataPIF);
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.info_product_fragment_container, infoProductFragCont);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    //Метод вызывается, когда фрагмент отключается от активности.
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}