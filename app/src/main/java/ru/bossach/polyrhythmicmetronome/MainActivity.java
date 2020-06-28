package ru.bossach.polyrhythmicmetronome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int DEFAULT_SPEED = 120;

    private boolean isPlay;
    private int current_speed;

    private TextView rhythmField;
    private SeekBar rhythmSeekBar;
    final private int SEEKBAR_BIAS = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageButton playPauseButton = findViewById(R.id.playPauseButton);
        final ImageButton plus = findViewById(R.id.plus);
        final ImageButton minus = findViewById(R.id.minus);
        rhythmField = findViewById(R.id.rhythmField);
        rhythmSeekBar = findViewById(R.id.rhythmSeekBar);


        updateSpeed(DEFAULT_SPEED);


        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay) {
                    isPlay = false;
                    playPauseButton.setImageResource(R.drawable.ic_play);
                } else {
                    isPlay = true;
                    playPauseButton.setImageResource(R.drawable.ic_pause);
                }
            }
        });


        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSpeed(current_speed + 1);
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSpeed(current_speed - 1);
            }
        });


        rhythmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSpeed(progress + SEEKBAR_BIAS);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }


    private void updateSpeed(int speed) {
        if (speed > 240) speed = 240;
        if (speed < 20) speed = 20;

        current_speed = speed;
        rhythmField.setText(String.valueOf(current_speed));
        rhythmSeekBar.setProgress(current_speed - SEEKBAR_BIAS);
    }

}
