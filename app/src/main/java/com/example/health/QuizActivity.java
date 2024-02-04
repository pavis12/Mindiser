package com.example.health;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {

    private TextView questionNumberTextView;
    private TextView questionTextView;
    private RadioGroup optionsRadioGroup;
    private RadioButton option1RadioButton, option2RadioButton, option3RadioButton, option4RadioButton;
    private Button nextButton;
    private int dep=0,anx=0;

    private String[] questions = {
            "Question 0: How often have you been bothered by feeling down, depressed, or hopeless in the past two weeks?",
            "Question 1: How often have you been bothered by little interest or pleasure in doing things in the past two weeks?",
            "Question 2: Over the last 2 weeks, how often have you been bothered by feeling tired or having little energy?",
            "Question 3: Over the last 2 weeks, how often have you been bothered by poor appetite or overeating?",
            "Question 4: Over the last 2 weeks, how often have you been bothered by feeling bad about yourself - or that you are a failure or have let yourself or your family down",
            "Question 5: Over the last 2 weeks, how often have you been bothered by feeling nervous, anxious, or on edge",
            "Question 6: Over the last 2 weeks, how often have you been bothered by not being able to stop or control worrying",
            "Question 7: Over the last 2 weeks, how often have you been bothered by worrying too much about different things",
            "Question 8: Over the last 2 weeks, how often have you been bothered by trouble relaxing",
            "Question 9: Over the last 2 weeks, how often have you been bothered by becoming easily annoyed or irritable"




            // Add more questions here
    };

    private String[][] options = {
            {"Not at all", "Several days", "More than half the days", "Nearly every day"},

            // Add options for additional questions here
    };

    private int currentQuestionIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //questionNumberTextView = findViewById(R.id.questionNumberTextView);
        questionTextView = findViewById(R.id.questionTextView);
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
        option1RadioButton = findViewById(R.id.option1RadioButton);
        option2RadioButton = findViewById(R.id.option2RadioButton);
        option3RadioButton = findViewById(R.id.option3RadioButton);
        option4RadioButton = findViewById(R.id.option4RadioButton);
        nextButton = findViewById(R.id.nextButton);

        loadQuestion();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
                if (currentQuestionIndex < questions.length - 1) {
                    currentQuestionIndex++;
                    loadQuestion();
                } else {
                    showScore();
                }
            }
        });
    }

    private void loadQuestion() {
        //questionNumberTextView.setText(getString(R.string.question_number, currentQuestionIndex + 1, questions.length));
        questionTextView.setText(questions[currentQuestionIndex]);
        option1RadioButton.setText(options[0][0]);
        option2RadioButton.setText(options[0][1]);
        option3RadioButton.setText(options[0][2]);
        option4RadioButton.setText(options[0][3]);
        optionsRadioGroup.clearCheck();

        // Update progress indicator
        updateProgressIndicator();
    }

    private void checkAnswer() {
        int selectedId = optionsRadioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) { // At least one option is selected
            RadioButton selectedRadioButton = findViewById(selectedId);
            if (selectedRadioButton != null) {
                String selectedOption = selectedRadioButton.getText().toString();
                if (selectedOption.equals("Not at all")) {
                    score+=0;
                    if(currentQuestionIndex<=4){
                        dep+=0;
                    }
                    else{
                        anx+=0;
                    }
                }
                else if(selectedOption.equals("Several days")){
                    score+=1;
                    if(currentQuestionIndex<=4){
                        dep+=1;
                    }
                    else{
                        anx+=1;
                    }
                }
                else if(selectedOption.equals("More than half the days")){
                    score+=2;
                    if(currentQuestionIndex<=4){
                        dep+=2;
                    }
                    else{
                        anx+=2;
                    }
                }
                else if(selectedOption.equals("Nearly every day")){
                    score+=3;
                    if(currentQuestionIndex<=4){
                        dep+=3;
                    }
                    else{
                        anx+=3;
                    }
                }


            }
        } else {
            // Show a message to the user that they need to select an option
            Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
        }
    }

    private void showScore() {
        Intent intent = new Intent(QuizActivity.this, ScoreActivity.class);
        intent.putExtra("score", score);

        intent.putExtra("depScore", dep);
        intent.putExtra("anxScore", anx);
        startActivity(intent);
        finish();
    }

    private void updateProgressIndicator() {
        LinearLayout progressIndicatorLayout = findViewById(R.id.progressIndicatorLayout);
        int totalQuestions = questions.length;

        // Remove existing circles
        progressIndicatorLayout.removeAllViews();

        // Add circles for each question
        for (int i = 0; i < totalQuestions; i++) {
            View circle = new View(this);
            circle.setLayoutParams(new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.circle_size),
                    getResources().getDimensionPixelSize(R.dimen.circle_size)
            ));
            circle.setBackgroundResource(R.drawable.circle_background);

            // Highlight current question circle
            if (i == currentQuestionIndex) {
                circle.setBackgroundColor(Color.WHITE); // Change color to highlight current question
            }

            progressIndicatorLayout.addView(circle);
        }
    }
}
