package com.example.fridge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AllergensAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<String> allergensList;
    private final HashSet<String> selectedItems; // Для отслеживания выбранных элементов

    public AllergensAdapter(Context context, ArrayList<String> allergensList) {
        this.context = context;
        this.allergensList = allergensList;
        this.selectedItems = new HashSet<>();
    }

    @Override
    public int getCount() {
        return allergensList.size();
    }

    @Override
    public Object getItem(int position) {
        return allergensList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.allergen_item, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.text_view_name);
        ImageView checkMarkImageView = convertView.findViewById(R.id.check_mark_image_view);

        String allergen = allergensList.get(position);
        textViewName.setText(allergen);

        // Устанавливаем видимость галочки на основе состояния
        if (selectedItems.contains(allergen)) {
            checkMarkImageView.setVisibility(View.VISIBLE);
        } else {
            checkMarkImageView.setVisibility(View.GONE);
        }

        // Обработчик нажатия для переключения состояния
        //convertView.setOnClickListener(v -> toggleSelection(allergen));

        return convertView;
    }

    // Обновление состояния выбранности элемента
    public void toggleSelection(String allergen) {
        if (selectedItems.contains(allergen)) {
            selectedItems.remove(allergen); // Убираем из выбранных
        } else {
            selectedItems.add(allergen); // Добавляем в выбранные
        }
        notifyDataSetChanged(); // Обновляем ListView
    }

    public void recheckAllergenInSelected(String currentName, String newName){
        selectedItems.remove(currentName);
        selectedItems.add(newName);
    }

    public void deleteAllergenFromSelected(String deletedName){
        selectedItems.remove(deletedName);
    }

    // Метод для предустановки галочек на те что были выбраны раннее
    public void setPreselectedItems(List<String> preselectedAllergens) {
        selectedItems.clear(); // Сбрасываем предыдущий выбор
        selectedItems.addAll(preselectedAllergens);
        notifyDataSetChanged(); // Обновляем ListView
    }

    // Возвращает список выбранных элементов
    public List<String> getSelectedAllergens() {
        return new ArrayList<>(selectedItems);
    }

}
