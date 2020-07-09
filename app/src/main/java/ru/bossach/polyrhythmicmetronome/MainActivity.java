package ru.bossach.polyrhythmicmetronome;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import ru.bossach.polyrhythmicmetronome.databinding.ActivityMainBinding;
import ru.bossach.polyrhythmicmetronome.metronome.Metronome;

public class MainActivity extends AppCompatActivity {


    private boolean isPlay;


    ActivityMainBinding binding;

    Metronome metronome;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        metronome = Metronome.getInstance(getApplicationContext());


        setListeners();

    }

    private void setListeners() {

        binding.playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPlay();
            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isPlay) switchPlay();
    }


    private void switchPlay() {
        if (isPlay) {
            metronome.stop();
            isPlay = false;
            binding.playPauseButton.setImageResource(R.drawable.ic_play);
        } else {
            metronome.start();
            isPlay = true;
            binding.playPauseButton.setImageResource(R.drawable.ic_pause);
        }
    }



}
