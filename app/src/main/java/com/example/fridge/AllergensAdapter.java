package com.example.fridge;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AllergensAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<String> allergensList;
    private final SparseBooleanArray selectedItems; // Для отслеживания выбранных элементов

    public AllergensAdapter(Context context, ArrayList<String> allergensList) {
        this.context = context;
        this.allergensList = allergensList;
        this.selectedItems = new SparseBooleanArray();
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

        textViewName.setText(allergensList.get(position));

        // Устанавливаем видимость галочки на основе состояния
        if (selectedItems.get(position, false)) {
            checkMarkImageView.setVisibility(View.VISIBLE);
        } else {
            checkMarkImageView.setVisibility(View.GONE);
        }

        return convertView;
    }

    // Метод для предустановки галочек на те что были выбраны раннее
    public void setPreselectedItems(List<String> preselectedAllergens) {
        selectedItems.clear(); // Сбрасываем предыдущий выбор
        for (int i = 0; i < allergensList.size(); i++) {
            if (preselectedAllergens.contains(allergensList.get(i))) {
                selectedItems.put(i, true); // Отмечаем элемент как выбранный
            }
        }
        notifyDataSetChanged(); // Обновляем ListView
    }


    // Обновление состояния видимости галочки
    public void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position); // Убираем выбор
        } else {
            selectedItems.put(position, true); // Добавляем выбор
        }
        notifyDataSetChanged(); // Обновляем ListView
    }

    // Возвращает список выбранных элементов
    public List<String> getSelectedAllergens() {
        List<String> selectedList = new ArrayList<>();
        for (int i = 0; i < selectedItems.size(); i++) {
            int position = selectedItems.keyAt(i);
            if (selectedItems.valueAt(i)) {
                selectedList.add(allergensList.get(position));
            }
        }
        return selectedList;
    }

}
