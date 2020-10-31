package com.example.timer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SeekBar seekBar;
    private TextView textView;
    Button btn;
    private boolean isTimerOn;
    private CountDownTimer timer;
    private int default_interval;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        btn = findViewById(R.id.button);

        seekBar = findViewById(R.id.seekBar);
        textView = findViewById(R.id.textView);

        //Устанавливаем макс занчение бара
        seekBar.setMax(600);
//        seekBar.setProgress(30);
        setIntervalFromSharedPreference(sharedPreferences);
        isTimerOn = false;

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long progressInMills = progress * 1000;
                updateTimer(progressInMills);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//        CountDownTimer timer = new CountDownTimer(10000, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                Log.d("timer", String.valueOf(millisUntilFinished / 1000) + " seconds left");
//            }
//
//            @Override
//            public void onFinish() {
//                Log.d("timer", "Finish");
//            }
//        };
//        timer.start();

//        final Handler hund = new Handler();
//        Runnable run = new Runnable() {
//            @Override
//            public void run() {
//                Log.d("Runnable: ", "2 sec are passed!");
//                hund.postDelayed(this, 2000);
//            }
//        };
//        hund.post(run);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    public void start(View view) {

        if (!isTimerOn) {
            btn.setText("Stop");
            seekBar.setEnabled(false);
            isTimerOn = true;
            timer = new CountDownTimer(seekBar.getProgress() * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    updateTimer(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if (sharedPreferences.getBoolean("enable_sound", true)) {

                        String melodyName = sharedPreferences.getString("timer_melody", "bell");
                        if (melodyName.equals("bell")) {
//                            Log.d("finish", "Finished!");
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bell_sound);
                            mediaPlayer.start();

                        } else if (melodyName.equals("alarm_siren")) {
                            Log.d("finish", "Finished!");
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm_siren_sound);
                            mediaPlayer.start();

                        } else if (melodyName.equals("bip")) {
                            Log.d("finish", "Finished!");
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bip_sound);
                            mediaPlayer.start();
                        }


//
                    }
                    reserTimer();
                }
            };
            timer.start();
        } else {
            reserTimer();
        }


    }

    private void updateTimer(long millisUntilFinished) {
        int minutes = (int) (millisUntilFinished / 1000 / 60);
        int seconds = (int) (millisUntilFinished / 1000 - (minutes * 60));

        String minutesString = "";
        String secondsString = "";

        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = String.valueOf(minutes);
        }

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = String.valueOf(seconds);
        }

        textView.setText(minutesString + " : " + secondsString);
    }

    private void reserTimer() {
        timer.cancel();
        btn.setText("Start");
        seekBar.setEnabled(true);
        isTimerOn = false;
        setIntervalFromSharedPreference(sharedPreferences);
    }


    //create menu to display
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.timer_menu, menu);
        return true;
    }

    //click action
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action1) {
            Intent openSettings = new Intent(this, SettingsActivity.class);
            startActivity(openSettings);
            return true;
        } else {
            Intent About = new Intent(this, AboutActivity.class);
            startActivity(About);
            return true;
        }
    }

    private void setIntervalFromSharedPreference(SharedPreferences sharedPreference) {
//        try {
//            default_interval = Integer.parseInt(sharedPreference.getString("default_interval", "30"));
////            int x = 8/0;
//        } catch (NumberFormatException e) {
//            Toast.makeText(this, "Use only numbers", Toast.LENGTH_SHORT).show();
//        } catch (Exception ex) {
//            Toast.makeText(this, "Another problems with App", Toast.LENGTH_SHORT).show();
//        }
        default_interval = Integer.parseInt(sharedPreference.getString("default_interval", "30"));
        long defaultintervalInMillis = default_interval * 1000;
        updateTimer(defaultintervalInMillis);
        seekBar.setProgress(default_interval);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("default_interval")) {
            setIntervalFromSharedPreference(sharedPreferences);
        }
    }
}