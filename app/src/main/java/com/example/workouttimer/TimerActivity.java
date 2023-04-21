package com.example.workouttimer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Vibrator;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TimerActivity extends AppCompatActivity {

    //initialise variables for app elements
    TextView textWorkoutProgress, textSetProgress, textSetType;
    TextView textTimerWorkout, textTimerSet;
    Button buttonStartStop, buttonFinishWorkout;
    ProgressBar progressBarWorkout, progressBarSet;

    long timeWorkoutRemaining = 0, timeSetRemaining = 0, timeRestRemaining = 0;
    long timeWorkoutTotal = 0, timeSetTotal = 0, timeRestTotal = 0;

    //initialise variables for use in app
    //intWorkoutStatus states are:
    // 0 is initial setup,
    //1 is start set, 2 is pause set, 3 is continue set
    //4 is start break, 5 is pause break, 6 is continue break
    //7 is to end the app
    int intWorkoutStatus = 0;
    MediaPlayer mp = null, endMp = null;

    CountDownTimer workoutTimer, setTimer, restTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        //bind variables to elements in the view
        textWorkoutProgress = findViewById(R.id.textViewTitleWorkoutProgress);
        textSetProgress = findViewById(R.id.textViewTitleSetProgress);
        textSetType = findViewById(R.id.textViewTitleCurrentSetType);

        textTimerWorkout = findViewById(R.id.textViewTimerWorkout);
        textTimerSet = findViewById(R.id.textViewTimerSet);

        buttonStartStop = findViewById(R.id.buttonStartStop);
        buttonFinishWorkout = findViewById(R.id.buttonFinishWorkout);

        progressBarWorkout = findViewById(R.id.progressBarWorkoutProgress);
        progressBarSet = findViewById(R.id.progressBarSetProgress);

        //get elements input in previous activity
        Bundle extras = getIntent().getExtras();
        long timeWorkout = extras.getInt("WorkoutDuration");
        long timeSet = extras.getInt("SetDuration");
        long timeRest = extras.getInt("RestDuration");

        //setup media player for sounds on set completion and workout completion
        mp = MediaPlayer.create(this, R.raw.sample);
        endMp = MediaPlayer.create(this, R.raw.end);

        buttonStartStop.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (intWorkoutStatus == 0){ //initialise workout timers

                    textSetType.setText("Working Hard");

                    timeWorkoutTotal = timeWorkout * 10000;
                    timeSetTotal = timeSet * 10000;
                    timeRestTotal = timeRest * 10000;

                    progressBarWorkout.setMax(Math.toIntExact(timeWorkoutTotal));
                    progressBarSet.setMax(Math.toIntExact(timeSetTotal));

                    //create new timers based on previous inputs
                    workoutTimer = newWorkoutTimer(timeWorkoutTotal).start();

                    setTimer = newSetTimer(timeSetTotal).start();

                    //change button to pause, and set status to 1: set
                    buttonStartStop.setText("Pause");
                    intWorkoutStatus = 2;

                }else if (intWorkoutStatus == 1){ //start set

                    textSetType.setText("Working Hard");
                    timeWorkoutRemaining -= 1000;

                    workoutTimer = newWorkoutTimer(timeWorkoutRemaining).start();

                    //start set timer with new workout time left
                    setTimer = newSetTimer(timeSetTotal).start();

                    buttonStartStop.setText("Pause");
                    intWorkoutStatus = 2;

                }else if (intWorkoutStatus == 2) { //pause set

                    textSetType.setText("Paused");

                    //cancel both timers
                    workoutTimer.cancel();
                    setTimer.cancel();

                    //change button to continue, and status to 2: paused
                    buttonStartStop.setText("Continue");
                    intWorkoutStatus = 3;

                }else if (intWorkoutStatus == 3) { //continue set

                    textSetType.setText("Working Hard");

                    //start workout timer with remaining time left
                    workoutTimer = newWorkoutTimer(timeWorkoutRemaining).start();

                    //start set timer with new workout time left
                    setTimer = newSetTimer(timeSetRemaining).start();

                    buttonStartStop.setText("Pause");
                    intWorkoutStatus = 2;

                }else if (intWorkoutStatus == 4){ //start break

                    textSetType.setText("Rest Time");
                    timeWorkoutRemaining -= 1000;
                    //start workout timer with remaining time left
                    workoutTimer = newWorkoutTimer(timeWorkoutRemaining).start();

                    //start set timer with new workout time left
                    restTimer = newRestTimer(timeRestTotal).start();

                    buttonStartStop.setText("Pause");
                    intWorkoutStatus = 5;

                } else if (intWorkoutStatus == 5) { //pause break

                    textSetType.setText("Paused");

                    //cancel both timers
                    workoutTimer.cancel();
                    restTimer.cancel();

                    //change button to continue, and status to 2: paused
                    buttonStartStop.setText("Continue");
                    intWorkoutStatus = 6;

                }else if (intWorkoutStatus == 6) { //continue break

                    textSetType.setText("Rest Time");

                    //start workout timer with remaining time left
                    workoutTimer = newWorkoutTimer(timeWorkoutRemaining).start();

                    //start set timer with new workout time left
                    restTimer = newRestTimer(timeRestRemaining).start();

                    buttonStartStop.setText("Pause");
                    intWorkoutStatus = 5;
                }else if (intWorkoutStatus == 7){
                    //everything is done
                    finish();
                }
            }
        });

        buttonFinishWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //just kick back to main activity to end the app
                finish();
            }
        });
    }

    public CountDownTimer newWorkoutTimer(long time) {
        return new CountDownTimer(time, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                timeWorkoutRemaining = millisUntilFinished;
                progressBarWorkout.setProgress(Math.toIntExact(timeWorkoutTotal-timeWorkoutRemaining));
                textTimerWorkout.setText(f.format(min) + ":" + f.format(sec));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                progressBarWorkout.setProgress(Math.toIntExact(timeWorkoutTotal));
                progressBarSet.setProgress(Math.toIntExact(timeSetTotal));
                textTimerSet.setText("");
                textTimerWorkout.setText("");
                textSetType.setTextSize(30);
                textSetType.setText("Congratulations You Are Done");
                setTimer.cancel();
                restTimer.cancel();
                buttonStartStop.setText("Complete");
                intWorkoutStatus = 7;
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(800);
                endMp.start();
            }
        };
    }
    public CountDownTimer newSetTimer(long time) {
        return new CountDownTimer(time, 1000){
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished){
                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                timeSetRemaining = millisUntilFinished;
                progressBarSet.setProgress(Math.toIntExact(timeSetTotal-timeSetRemaining));
                textTimerSet.setText(f.format(min) + ":" + f.format(sec));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                progressBarSet.setProgress(Math.toIntExact(timeSetTotal));
                workoutTimer.cancel();
                buttonStartStop.setText("Next stage: rest");
                intWorkoutStatus = 4;
                timeRestRemaining = timeRestTotal;
                textTimerSet.setText("Complete");
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(400);
                mp.start();
            }
        };

    }
    public CountDownTimer newRestTimer(long time) {
        return new CountDownTimer(time, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                timeRestRemaining = millisUntilFinished;
                textTimerSet.setText(f.format(min) + ":" + f.format(sec));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                workoutTimer.cancel();
                buttonStartStop.setText("Next stage: workout");
                intWorkoutStatus = 1;
                timeSetRemaining = timeSetTotal;
                textTimerSet.setText("Complete");
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(400);
                mp.start();
            }
        };
    }
}