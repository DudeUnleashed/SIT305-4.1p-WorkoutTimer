package com.example.workouttimer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //element variable declaration
    TextView textTitle, textSetNumber, textSetDuration, textRestDuration;
    EditText editSetNumber, editSetDuration, editRestDuration;
    Button buttonStartWorkout;

    //variables used for timer
    public int setNumber, setDuration, restDuration;
    public int workoutDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //assign values to id
        textTitle = findViewById(R.id.textViewTitle);
        textSetNumber = findViewById(R.id.textViewTitleWorkoutNumber);
        textSetDuration = findViewById(R.id.textViewTitleSetDuration);
        textRestDuration = findViewById(R.id.textViewTitleRestTime);

        editSetNumber = findViewById(R.id.editTextSetAmount);
        editSetDuration = findViewById(R.id.editTextSetDuration);
        editRestDuration = findViewById(R.id.editTextRestDuration);

        buttonStartWorkout = findViewById(R.id.buttonStartWorkout);

        buttonStartWorkout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                setNumber = Integer.parseInt(String.valueOf(editSetNumber.getText()));
                setDuration = Integer.parseInt(String.valueOf(editSetDuration.getText()));
                restDuration = Integer.parseInt(String.valueOf(editRestDuration.getText()));
                workoutDuration = (setNumber*setDuration) + restDuration*(setNumber-1);
                Intent intentToTimer = new Intent(MainActivity.this, TimerActivity.class);
                intentToTimer.putExtra("WorkoutDuration", workoutDuration);
                intentToTimer.putExtra("SetDuration", setDuration);
                intentToTimer.putExtra("RestDuration", restDuration);
                startActivity(intentToTimer);
                finish();
            }
        });

    }
}