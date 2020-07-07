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

    private static final int DEFAULT_BPM = 120;

    private boolean isPlay;

    private int currentBpm;

    ActivityMainBinding binding;

    Metronome metronome;

    final private int SEEKBAR_BIAS = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        metronome = new Metronome(getApplicationContext());

        setBpm(DEFAULT_BPM);

        setListeners();

    }

    private void setListeners() {

        binding.playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPlay();
            }
        });

        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBpm(currentBpm + 1);
            }
        });

        binding.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBpm(currentBpm - 1);
            }
        });


        binding.rhythmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setBpm(progress + SEEKBAR_BIAS);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            final float BUFFER_LIMIT = 25f;
            float buffer = 0f;
            float prevX = 0f;

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float diatanceX, float distanceY) {
                float delta = e2.getX() - prevX;
                prevX = e2.getX();
                Log.d("onScroll", "delta: " + delta);
                if (Math.signum(buffer + delta) != Math.signum(buffer) && buffer != 0) {
                    buffer = 0;
                } else {
                    buffer += delta;
                }
                Log.d("onScroll", "buffer: " + buffer);
                if (Math.abs(buffer) > BUFFER_LIMIT) {
                    setBpm(currentBpm + (int) Math.signum(buffer));
                    buffer = 0f;
                }
                return true;
            }
        });

        binding.rhythmField.setClickable(true);

        binding.rhythmField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
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


    private void setBpm(int bpm) {
        if (bpm == currentBpm) return;
        if (bpm > 240) bpm = 240;
        if (bpm < 20) bpm = 20;

        currentBpm = bpm;
        metronome.setBpm(bpm);
        binding.rhythmField.setText(String.valueOf(bpm));
        binding.rhythmSeekBar.setProgress(bpm - SEEKBAR_BIAS);
    }
}
