package ru.bossach.polyrhythmicmetronome;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.bossach.polyrhythmicmetronome.databinding.BpmSelectorBinding;
import ru.bossach.polyrhythmicmetronome.metronome.Metronome;

public class BpmSelector extends Fragment {

    private BpmSelectorBinding binding;

    private static final int DEFAULT_BPM = 120;

    final private int SEEKBAR_BIAS = 20;
    private int currentBpm;

    private Metronome metronome;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BpmSelectorBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        metronome = Metronome.getInstance(getContext());

        setBpm(DEFAULT_BPM);

        setListeners();



    }

    private void setListeners() {
        binding.plus.setOnClickListener(v -> setBpm(currentBpm + 1));

        binding.minus.setOnClickListener(v -> setBpm(currentBpm - 1));


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

        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
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

        binding.rhythmField.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
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
