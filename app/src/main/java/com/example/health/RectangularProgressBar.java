package com.example.health;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class RectangularProgressBar extends View {

    private int scorePercentage;
    private Paint progressPaint;
    private Paint backgroundPaint;
    private Paint textPaint;
    private int barHeight = 40; // Adjust the height of the progress bar

    public RectangularProgressBar(Context context) {
        super(context);
        init();
    }

    public RectangularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RectangularProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        progressPaint = new Paint();
        progressPaint.setColor(Color.BLUE);
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.FILL);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.LTGRAY);
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(40); // Adjust text size as needed
    }

    public void setScorePercentage(int scorePercentage) {
        this.scorePercentage = scorePercentage;
        if (scorePercentage > 50) {
            progressPaint.setColor(Color.RED);
        } else {
            progressPaint.setColor(Color.DKGRAY);
        }
        invalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float endX = getWidth() * scorePercentage / 100f;
        float endY = getHeight();
        float textSize = getHeight() / 2f;

        // Draw background bar
        canvas.drawRect(0, 0, getWidth(), endY, backgroundPaint);

        // Draw colored portion
        if (endX > 0) {
            canvas.drawRect(0, 0, endX, endY, progressPaint);
        }

        // Draw percentage text
        String text = scorePercentage + "%";
        float textX = getWidth() / 2f;
        float textY = endY / 2 + textSize / 3; // Adjust vertical position
        canvas.drawText(text, textX, textY, textPaint);
    }
}

