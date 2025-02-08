package com.example.fridge;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExpiryCheckWorker extends Worker {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private DateTimeFormatter formatterDB = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ExpiryCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Получите список продуктов из базы данных, срок годности которых истекает через день
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
        List<DataProductInFridge> expiringProducts = dbHelper.getExpiredProductsInFridge();

        if (!expiringProducts.isEmpty()) {
            // Формирование сообщения
            StringBuilder message = new StringBuilder("Срок годности истекает/истёк:\n");
            for (DataProductInFridge dataPIF : expiringProducts) {
                DataProduct dataProduct = dbHelper.getProductById(dataPIF.getProduct_id());
                String expiryDate = LocalDate.parse(dataPIF.getExpiry_date(), formatterDB).format(formatter);
                message.append(dataProduct.getName()).append(" (").append(expiryDate).append(")\n");
            }

            // Отправка уведомления
            sendNotification(getApplicationContext(), "Уведомление от холодильника", message.toString());
            Log.i("doWork", "Message was send");
        }

        return Result.success();
    }

    private void sendNotification(Context context, String title, String message) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "FRIDGE_CHANNEL",
                    "Fridge Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Уведомления о продуктах в холодильнике");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "FRIDGE_CHANNEL")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message)); // Для длинных сообщений

        notificationManager.notify(1, builder.build());
    }
}

