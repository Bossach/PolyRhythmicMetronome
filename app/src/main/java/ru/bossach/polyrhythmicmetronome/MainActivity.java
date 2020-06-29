package ru.bossach.polyrhythmicmetronome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import ru.bossach.polyrhythmicmetronome.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final int DEFAULT_SPEED = 120;

    private boolean isPlay;
    private int current_speed;

    ActivityMainBinding binding;


    final private int SEEKBAR_BIAS = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        updateSpeed(DEFAULT_SPEED);

        binding.playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPlay();
            }
        });




        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSpeed(current_speed + 1);
            }
        });

        binding.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSpeed(current_speed - 1);
            }
        });


        binding.rhythmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

    private void switchPlay() {
        if (isPlay) {
            isPlay = false;
            binding.playPauseButton.setImageResource(R.drawable.ic_play);
        } else {
            isPlay = true;
            binding.playPauseButton.setImageResource(R.drawable.ic_pause);
        }
    }


    private void updateSpeed(int speed) {
        if (speed > 240) speed = 240;
        if (speed < 20) speed = 20;

        current_speed = speed;
        binding.rhythmField.setText(String.valueOf(current_speed));
        binding.rhythmSeekBar.setProgress(current_speed - SEEKBAR_BIAS);
    }

}
