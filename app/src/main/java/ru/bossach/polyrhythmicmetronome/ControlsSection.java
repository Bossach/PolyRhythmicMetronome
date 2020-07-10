package ru.bossach.polyrhythmicmetronome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.bossach.polyrhythmicmetronome.databinding.ControlsSectionBinding;
import ru.bossach.polyrhythmicmetronome.metronome.Metronome;

public class ControlsSection extends Fragment {

    private ControlsSectionBinding binding;
    private Metronome metronome;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ControlsSectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        metronome = Metronome.getInstance(getContext());

        setListeners();
    }

    private void setListeners() {

        binding.playStopButton.setOnClickListener(v -> switchPlay());

        binding.pauseButton.setOnClickListener(v -> {

            metronome.pause();
            updatePlayButton();
        });

        binding.resetButton.setOnClickListener(v -> metronome.reset());
    }


    @Override
    public void onPause() {
        super.onPause();
        if (metronome.isActive()) {
            metronome.stop();
            updatePlayButton();
        }
    }


    private void switchPlay() {
        if (metronome.isActive()) {
            metronome.stop();
        } else {
            metronome.start();
        }
        updatePlayButton();
    }

    private void updatePlayButton() {
        if (metronome.isActive()) {
            binding.playStopButton.setImageResource(R.drawable.ic_stop);
        } else {
            binding.playStopButton.setImageResource(R.drawable.ic_play);
        }
    }
}
