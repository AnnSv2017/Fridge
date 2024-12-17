package com.example.fridge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class ScannerFrameView extends View {
    private Paint cornerPaint;
    private int cornerLength = 50; // Длина уголка
    private int cornerWidth = 10; // Толщина уголка
    private Rect framingRect;

    public ScannerFrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        cornerPaint = new Paint();
        cornerPaint.setColor(Color.GREEN); // Цвет уголков
        cornerPaint.setStyle(Paint.Style.FILL);
    }

    public void setFramingRect(Rect framingRect) {
        this.framingRect = framingRect;
        invalidate(); // Перерисовать при обновлении рамки
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (framingRect == null) return;

        // Верхний левый угол
        canvas.drawRect(framingRect.left, framingRect.top,
                framingRect.left + cornerLength, framingRect.top + cornerWidth, cornerPaint);
        canvas.drawRect(framingRect.left, framingRect.top,
                framingRect.left + cornerWidth, framingRect.top + cornerLength, cornerPaint);

        // Верхний правый угол
        canvas.drawRect(framingRect.right - cornerLength, framingRect.top,
                framingRect.right, framingRect.top + cornerWidth, cornerPaint);
        canvas.drawRect(framingRect.right - cornerWidth, framingRect.top,
                framingRect.right, framingRect.top + cornerLength, cornerPaint);

        // Нижний левый угол
        canvas.drawRect(framingRect.left, framingRect.bottom - cornerWidth,
                framingRect.left + cornerLength, framingRect.bottom, cornerPaint);
        canvas.drawRect(framingRect.left, framingRect.bottom - cornerLength,
                framingRect.left + cornerWidth, framingRect.bottom, cornerPaint);

        // Нижний правый угол
        canvas.drawRect(framingRect.right - cornerLength, framingRect.bottom - cornerWidth,
                framingRect.right, framingRect.bottom, cornerPaint);
        canvas.drawRect(framingRect.right - cornerWidth, framingRect.bottom - cornerLength,
                framingRect.right, framingRect.bottom, cornerPaint);
    }
}
