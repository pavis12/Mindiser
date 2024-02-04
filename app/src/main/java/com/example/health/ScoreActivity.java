package com.example.health;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ScoreActivity extends AppCompatActivity {

    private TextView scoreTextView;
    private ScoreProgressBar scoreProgressBar;
    private RectangularProgressBar depscoreProgressbar,anscoreProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        scoreTextView = findViewById(R.id.scoreTextView);
        scoreProgressBar = findViewById(R.id.scoreProgressBar);
        depscoreProgressbar=findViewById(R.id.deprectangularProgressBar);
        anscoreProgressbar=findViewById(R.id.anrectangularProgressBar);
        /*Scaling:-
        * For Depression (PHQ-9):

            Not at all: 0
            Several days: 1
            More than half the days: 2
            Nearly every day: 3
            For Anxiety (GAD-7):

            Not at all: 0
            Several days: 1
            More than half the days: 2
            Nearly every day: 3
            *
            * for max.30 days:0-4: Minimal
            5-9: Mild
            10-14: Moderate
            15-19: Moderately severe
            20-30: Severe*/


        int score = getIntent().getIntExtra("score", 0);
        int depScore = getIntent().getIntExtra("depScore", 0);
        int anxScore = getIntent().getIntExtra("anxScore", 0);
        int maxPossibleScore=30;
        int maxdepScore=15;
        int maxanxScore=15;
        depScore=(depScore*100)/maxdepScore;
        anxScore=(anxScore*100)/maxanxScore;


        scoreTextView.setText("Your score: " + score);

        // Calculate score percentage
        int totalQuestions = 10; // Assuming total questions
        int scorePercentage = (score * 100) /maxPossibleScore;

        // Set score percentage to progress bar
        scoreProgressBar.setScorePercentage(scorePercentage);
        depscoreProgressbar.setScorePercentage(depScore);
        anscoreProgressbar.setScorePercentage(anxScore);
        setColorDescription(scorePercentage);

    }
    private void setColorDescription(int scorePercentage) {
        TextView colorDescriptionTextView = findViewById(R.id.colorDescriptionTextView);
        if (scorePercentage <10) {
            colorDescriptionTextView.setText("All Good"); // Green color description
            colorDescriptionTextView.setTextColor(Color.GREEN);
        } else if(scorePercentage>=10&&scorePercentage<=14) {
            colorDescriptionTextView.setText("Moderate"); // Red color description
            colorDescriptionTextView.setTextColor(Color.YELLOW);
        }
        else if(scorePercentage>=15&&scorePercentage<=19) {
            colorDescriptionTextView.setText("Moderately Severe"); // Red color description
            colorDescriptionTextView.setTextColor(Color.DKGRAY);
        }
        else if(scorePercentage>=20) {
            colorDescriptionTextView.setText("Severe"); // Red color description
            colorDescriptionTextView.setTextColor(Color.RED);
        }
    }
}
