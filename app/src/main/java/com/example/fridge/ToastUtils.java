package com.example.fridge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtils {
    // Приватный конструктор, чтобы предотвратить создание экземпляра этого класса
    private ToastUtils() {}

    // Метод для отображения кастомного Toast
    public static void showCustomToast(Context context, String message, String type) {
        // Загружаем кастомный макет
        LayoutInflater inflater = LayoutInflater.from(context);

        View layout = inflater.inflate(R.layout.custom_toast, null);
        // Настраиваем текст и изображение
        TextView textView = layout.findViewById(R.id.toast_text);
        textView.setText(message);

        ImageView imageView = layout.findViewById(R.id.toast_icon);

        if(type.equals("e")){
            // error
            imageView.setImageResource(R.drawable.ic_priority_high);
        } else if(type.equals("s")){
            // success
            imageView.setImageResource(R.drawable.ic_success);
        }

        // Создаем Toast
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}

