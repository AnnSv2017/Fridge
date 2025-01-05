package com.example.fridge;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CheckAllergensFragment extends Fragment {

    private SharedViewModel viewModel;
    private DBHelper dbHelper;

    private ArrayList<String> allergensArr;
    private List<String> selectedAllergens;
    private AllergensAdapter adapterAllergens;

    private ListView listViewAllergens;
    private ImageView backImageView, addAllergenImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_allergens, container, false);

        // Настройка ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        dbHelper = DBHelper.getInstance(requireContext());

        allergensArr = dbHelper.getAllAllergensNames();

        adapterAllergens = new AllergensAdapter(getContext(), allergensArr);

        listViewAllergens = view.findViewById(R.id.list_view_allergens);

        viewModel.getSelectedAllergens().observe(getViewLifecycleOwner(), allergens -> {
            selectedAllergens = allergens;
            adapterAllergens.setPreselectedItems(selectedAllergens);
        });

        listViewAllergens.setAdapter(adapterAllergens);

        listViewAllergens.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Переключаем состояние элемента
                adapterAllergens.toggleSelection(i);
            }
        });

        listViewAllergens.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view_item, int position, long id) {
                String allergenName = (String) adapterView.getItemAtPosition(position);

                // Показываем меню с действиями
                PopupMenu popupMenu = new PopupMenu(getContext(), view_item);
                popupMenu.inflate(R.menu.context_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override

                    public boolean onMenuItemClick(MenuItem menuItem) {
                            if(menuItem.getItemId() == R.id.action_edit){
                                Toast.makeText(getContext(), "Изменение", Toast.LENGTH_SHORT).show();
                                showEditAllergenAlertDialog(allergenName);
                            }
                            else if(menuItem.getItemId() == R.id.action_delete){
                                Toast.makeText(getContext(), "Удаление", Toast.LENGTH_SHORT).show();
                                dbHelper.deleteAllergenByName(allergenName);
                                updateAllergensArr();
                            }
                            else{
                                Toast.makeText(getContext(), "ДРУГОЕ", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                    }
                });

                popupMenu.show();
                return true;
            }
        });

        backImageView = view.findViewById(R.id.back_image_view);

        backImageView.setOnClickListener(view1 -> {
            // При выходе из второго фрагмента
            selectedAllergens = adapterAllergens.getSelectedAllergens();
            viewModel.setSelectedAllergens(selectedAllergens); // Обновляем данные в ViewModel

            // Возвращаемся к предыдущему фрагменту
            getActivity().getSupportFragmentManager().popBackStack();
        });

        addAllergenImageView = view.findViewById(R.id.image_view_add_allergen);

        addAllergenImageView.setOnClickListener(view1 -> {showAddAllergenAlertDialog();});

        return view;
    }

    private void showEditAllergenAlertDialog(String customAllergenName){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.dialog);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_allergen, null);

        ImageView closeImageView = dialogView.findViewById(R.id.close_image_view);
        EditText editTextAllergen = dialogView.findViewById(R.id.edit_text_allergen);
        Button addBtn = dialogView.findViewById(R.id.edit_btn);

        editTextAllergen.setText(customAllergenName);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        addBtn.setOnClickListener(view -> {
            String allergen = editTextAllergen.getText().toString();
            if (!allergen.trim().isEmpty()){
                // Изменение аллергена
                String newAllergenName = editTextAllergen.getText().toString();
                int allergenId = dbHelper.getAllergenIdByName(customAllergenName);

                dbHelper.editAllergenNameById(allergenId, newAllergenName);

                updateAllergensArr();
            }
            dialog.dismiss();
        });

        closeImageView.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showAddAllergenAlertDialog(){
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
                DataAllergen data = new DataAllergen(0, allergen);
                dbHelper.addAllergen(data);
                updateAllergensArr();
                //allergensArr.add(allergen);
            }
            dialog.dismiss();
        });

        closeImageView.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateAllergensArr(){
        ArrayList<String> list = dbHelper.getAllAllergensNames();
        allergensArr.clear();
        for(String allergen : list){
            allergensArr.add(allergen);
        }
        adapterAllergens.notifyDataSetChanged();
    }
}