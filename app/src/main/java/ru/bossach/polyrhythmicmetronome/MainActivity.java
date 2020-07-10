package ru.bossach.polyrhythmicmetronome;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;

import ru.bossach.polyrhythmicmetronome.databinding.ActivityMainBinding;
import ru.bossach.polyrhythmicmetronome.metronome.Metronome;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    Metronome metronome;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        metronome = Metronome.getInstance(getApplicationContext());

    }

}
