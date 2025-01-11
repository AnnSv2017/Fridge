package com.example.fridge;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

public class QrCodeCreateProductFragment extends Fragment {

    private GridLayout mainGridLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_code_create_product, container, false);

        mainGridLayout = view.findViewById(R.id.main_grid_layout);

        // Получаем Drawable для границ ячеек
        Drawable cellBorder = ContextCompat.getDrawable(requireContext(), R.drawable.cell_border);

        // Перебираем все дочерние View в GridLayout и устанавливаем им background
        if (mainGridLayout != null && cellBorder != null){
            for (int i = 0; i < mainGridLayout.getChildCount(); i++) {
                View child = mainGridLayout.getChildAt(i);
                if (child instanceof TextView){
                    child.setPadding(16, 16, 16, 16);
                    child.setBackground(cellBorder);

                } else if (child instanceof GridLayout) {
                    child.setPadding(16,16,16,16);
                    child.setBackground(cellBorder);

//                    GridLayout gridChild = (GridLayout) child;
//
//                    for (int j = 0; j < gridChild.getChildCount() ; j++) {
//                        View grandChild = gridChild.getChildAt(j);
//
//                        if(grandChild instanceof TextView){
//                            grandChild.setPadding(16, 16, 16, 16);
//                            grandChild.setBackground(cellBorder);
//                        }
//                    }
                }
            }
        }

        return view;
    }
}