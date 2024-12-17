package com.example.fridge;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CreateProductFragment extends Fragment {

    private ImageView backImageView;

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

        return view;
    }

    //Метод вызывается, когда фрагмент отключается от активности.
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}