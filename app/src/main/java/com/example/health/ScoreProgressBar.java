package com.example.health;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ScoreProgressBar extends View {

    private int scorePercentage;
    private Paint progressPaint;
    private Paint textPaint;
    private RectF rectF;
    private int strokeWidth = 30; // Adjust this value for thicker or thinner arc

    public ScoreProgressBar(Context context) {
        super(context);
        init();
    }

    public ScoreProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScoreProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        progressPaint = new Paint();
        progressPaint.setColor(Color.GREEN);
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);

        textPaint = new Paint();
        textPaint.setColor(Color.GREEN);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(60); // Adjust text size as needed

        rectF = new RectF();
    }

    public void setScorePercentage(int scorePercentage) {
        this.scorePercentage = scorePercentage;
        if (scorePercentage > 50) {
            progressPaint.setColor(Color.RED);
        } else {
            progressPaint.setColor(Color.GREEN);
        }
        invalidate();
    }


    /* protected void onDraw(Canvas canvas) {
         super.onDraw(canvas);

         float centerX = getWidth() / 2f;
         float centerY = getHeight() / 2f;
         float radius = Math.min(centerX, centerY) - strokeWidth / 2;
         float startAngle = 180;
         float sweepAngle = (scorePercentage * 1.8f); // 1.8 degrees per percentage point

         rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

         // Draw progress (thin colored arc)
         canvas.drawArc(rectF, startAngle, sweepAngle, false, progressPaint);

         // Draw percentage text
         String text = scorePercentage + "%";
         float textX = centerX;
         float textY = centerY + (textPaint.getTextSize() / 3); // Adjust vertical position
         canvas.drawText(text, textX, textY, textPaint);
     }*/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(centerX, centerY) - strokeWidth / 2;
        float startAngle = 180;

        // Ensure scorePercentage is within [0, 100] range
        scorePercentage = Math.max(0, Math.min(100, scorePercentage));

        // Calculate sweep angle based on the score percentage (maximum of 180 degrees)
        float sweepAngle = scorePercentage * 1.8f; // 1.8 degrees per percentage point

        // Ensure sweep angle doesn't exceed 180 degrees
        sweepAngle = Math.min(180, sweepAngle);

        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // Draw progress (thin colored arc)
        canvas.drawArc(rectF, startAngle, sweepAngle, false, progressPaint);

        // Draw percentage text
        String text = (int) scorePercentage + "%"; // Convert to integer for cleaner display
        float textX = centerX;
        float textY = centerY + (textPaint.getTextSize() / 3); // Adjust vertical position
        canvas.drawText(text, textX, textY, textPaint);
    }


}
