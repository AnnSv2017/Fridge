package com.example.fridge;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class CheckAllergensFragment extends Fragment {

    private DBHelper dbHelper;

    private ArrayList<String> allergensArr;
    private AllergensAdapter adapterAllergens;

    private ListView listViewAllergens;
    private ImageView backImageView, addAllergenImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_allergens, container, false);

        dbHelper = new DBHelper(getContext());

        //allergensArr = dbHelper.getAllAllergensNames();
        allergensArr = new ArrayList<>();

        adapterAllergens = new AllergensAdapter(getContext(), allergensArr);

        listViewAllergens = view.findViewById(R.id.list_view_allergens);

        listViewAllergens.setAdapter(adapterAllergens);

        listViewAllergens.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Переключаем состояние элемента
                adapterAllergens.toggleSelection(i);
            }
        });

        backImageView = view.findViewById(R.id.back_image_view);

        backImageView.setOnClickListener(view1 -> {
            // Возвращаемся к предыдущему фрагменту
            getActivity().getSupportFragmentManager().popBackStack();
        });

        addAllergenImageView = view.findViewById(R.id.image_view_add_allergen);

        addAllergenImageView.setOnClickListener(view1 -> {showAllergenAlert();});

        return view;
    }

    private void showAllergenAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.dialog);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_allergen, null);

        ImageView closeImageView = dialogView.findViewById(R.id.close_image_view);
        EditText editTextAllergen = dialogView.findViewById(R.id.edit_text_allergen);
        Button addBtn = dialogView.findViewById(R.id.add_btn);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        addBtn.setOnClickListener(view -> {
            String allergen = editTextAllergen.getText().toString();
            if (!allergen.trim().isEmpty()){
                //DataAllergen data = new DataAllergen(0, allergen);
                //dbHelper.addAllergen(data);
                allergensArr.add(allergen);
            }
            dialog.dismiss();
        });

        closeImageView.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }
}