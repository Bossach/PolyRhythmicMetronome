package ru.bossach.polyrhythmicmetronome.metronome;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.bossach.polyrhythmicmetronome.R;


public class Metronome {

    private static final int MAX_METRONOMES = 2;

    private static final float DEFAULT_BASE_TONE = 1.5f;
    private static final float[] DEFAULT_ACCENT_TONES = new float[]{1.85f, 1.35f, 1.1f, 1.2f};

    private volatile int bpm;

    private Thread metronome;
    private boolean isActive;

    private SoundPool soundPool;

    private int soundId;

    private ArrayList<ClickPattern> patterns;


    public Metronome(Context context) {
        soundPool = new SoundPool(MAX_METRONOMES, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(context, R.raw.click, 1);

        hardcodePatterns();

    }

    private void hardcodePatterns() {
        patterns = new ArrayList<>();
        patterns.add(new ClickPattern("4/4"));
        patterns.add(new ClickPattern("3/4"));
    }

    private void tick() {

        List<Float> toneValues = new ArrayList<>();

        Tone tier = Tone.EMPTY;

        for (ClickPattern pattern : patterns) {
            Tone tone = pattern.next();
            if (tone == Tone.ACCENT) {
                tier = Tone.ACCENT;
                toneValues.add(getToneValue(pattern, tone));
            } else if (tone == Tone.BASE && tier == Tone.EMPTY) {
                tier = Tone.BASE;
            }
        }

        if (tier == Tone.BASE) toneValues.add(DEFAULT_BASE_TONE);

        if (tier != Tone.EMPTY) {
            click(toneValues);
        }

    }


    private void click(List<Float> toneValuesAtThisTick) {
        Log.d("In click", "time: " + System.currentTimeMillis() % getDelay());
        for (Float value : toneValuesAtThisTick) {
            Log.d("\tIn for", "\ttime: " + System.currentTimeMillis() % getDelay());
            soundPool.play(soundId, 1, 1, 0, 0, value);
        }



    }

    private float getToneValue(ClickPattern pattern, Tone tone) {
        if (patterns.contains(pattern)) {
            if (tone == Tone.ACCENT) {
                return DEFAULT_ACCENT_TONES[patterns.indexOf(pattern)];
            } else return DEFAULT_BASE_TONE;
        } else {
            throw new IllegalArgumentException("Unknown pattern");
        }
    }

    public void start() {

        soundPool.play(soundId, 0, 0, 0, 0, 1);

        if (metronome != null) {
            stop();
        }
        isActive = true;

        metronome = new Thread(new Runnable() {
            @Override
            public void run() {
                long lastTick = System.currentTimeMillis();
                long cur;
                long delay;
                long timeToNext;
                while (isActive) {
                    delay = getDelay();
                    cur = System.currentTimeMillis();
                    timeToNext = lastTick + delay - cur;
                    if (timeToNext <= 0) {
                        while (timeToNext <= 0) {
                            lastTick += delay;
                            timeToNext = lastTick + delay - cur;
                        }
                        tick();
                        cur = System.currentTimeMillis();
                        timeToNext = lastTick + delay - cur;
                    }


                    if (timeToNext > 0) {
                        try {
                            if (timeToNext > 50) {
                                Thread.sleep(timeToNext - 50);
                            } else {
                                Thread.sleep(0);
                            }
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            }
        });
        metronome.start();
    }

    private int getDelay() {
        return (240000 / bpm) / Constants.GLOBAL_DENSITY;
    }

    public void stop() {
        isActive = false;
        if (metronome != null) {
            metronome.interrupt();
            metronome = null;
        }
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }
}
