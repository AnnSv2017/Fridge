package com.example.fridge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.Size;

import java.util.List;


public class QrCodeScannerActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ScannerFrameView scannerFrameView;
    private JsonObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);

        // Создание qr сканера
        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        scannerFrameView = findViewById(R.id.scanner_frame);

        // Проверка разрешений
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            initializeScanner();
        }

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), null);

        // Устанавливаем размер области сканирования
        barcodeScannerView.getBarcodeView().setFramingRectSize(new Size(700, 700));

        barcodeScannerView.getBarcodeView().resume();

        // Передаем область сканирования в ScannerFrameView
        //Handler для задержки чтобы barcodeScannerView успело изменить размер сканируемой области до того как мы отдадим ему запрос на создание framingRect
        new Handler().postDelayed(() -> {
            Rect framingRect = barcodeScannerView.getBarcodeView().getFramingRect();
            if (framingRect != null) {
                Log.d("ScannerFrame", "FramingRect: " + framingRect);
                scannerFrameView.setFramingRect(framingRect);
            } else {
                Log.e("ScannerFrame", "FramingRect is still null after delay!");
            }
        }, 1000); // Задержка в 1000 мс

        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null) {

                    // Парсинг JSON
                    Gson gson = new Gson();
                    jsonObject = gson.fromJson(result.getText(), JsonObject.class);

                    // Переход на новый фрагмент
                    QrCodeInfoFragment fragment = new QrCodeInfoFragment(jsonObject);
                    switchToFragment(fragment);
                }
            }
            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }
        });
    }

    private void initializeScanner() {
        // Управление сканером
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), null);
        barcodeScannerView.getViewFinder().setLaserVisibility(false);
        barcodeScannerView.getViewFinder().setMaskColor(0x60000000); // Полупрозрачный черный фон
        barcodeScannerView.getBarcodeView().setFramingRectSize(new Size(700, 700)); // Размер области сканирования
        barcodeScannerView.getBarcodeView().resume();
    }

    //Вызывается, когда пользователь отвечает на запрос разрешений.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeScanner();
            } else {
                ToastUtils.showCustomToast(this, "Камера не разрешена", "e");
            }
        }
    }

    public void switchToFragment(Fragment fragment) {
        try {
            if (fragment == null) return;

            // Остановка сканера
            barcodeScannerView.pause();

            // Скрываем активность
            findViewById(R.id.activity_container).setVisibility(View.GONE);
            // Отображаем фрагмент
            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

            // Вставка фрагмента в пустой fragment_container
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e){
            Log.e("switchToFragment", "Ошибка при переключении фрагмента", e);
        }
    }


    @Override
    public void onFragmentClose() {
        // Восстанавливаем активность
        findViewById(R.id.activity_container).setVisibility(View.VISIBLE);
        // Скрываем контейнер фрагмента
        findViewById(R.id.fragment_container).setVisibility(View.GONE);
        // Возобновляем работу сканера
        barcodeScannerView.resume();
    }

    public void btnCreateProduct(View v){
        CreateProductFragment fragment = CreateProductFragment.newInstance("QrCodeScannerActivity");
        switchToFragment(fragment);
    }

    //Переопределяем метод для возврата назад (встроенная кнопка на телефоне) из фрагмента к сканеру
    @Override
    public void onBackPressed() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            // Получаем текущий фрагмент
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

            if (currentFragment instanceof OnBackPressInQrCodeInfoFragmentListener) {
                // Вызываем метод фрагмента
                int productId = ((OnBackPressInQrCodeInfoFragmentListener) currentFragment).getProductIdIfProductWasNOTCreatedEarlier();

                if (productId != -1) {
                    // Удаляем продукт из базы данных, если id не равно -1
                    DBHelper dbHelper = DBHelper.getInstance(this);
                    dbHelper.deleteProduct(productId);
                }
            }

            // Возвращаемся к предыдущему фрагменту
            fragmentManager.popBackStack();
            onFragmentClose();
        } else {
            // Закрываем активность, если мы на корневом фрагменте
            super.onBackPressed();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (barcodeScannerView != null) {
            barcodeScannerView.getBarcodeView().resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (barcodeScannerView != null) {
            barcodeScannerView.getBarcodeView().pause();
        }
    }

    public void btnHome(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void btnAnalytics(View v){
        Intent intent = new Intent(this, AnalyticsActivity.class);
        startActivity(intent);
    }

    public void btnShoppingList(View v){
        Intent intent = new Intent(this, ShoppingListActivity.class);
        startActivity(intent);
    }
}