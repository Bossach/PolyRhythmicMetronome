package ru.bossach.polyrhythmicmetronome.metronome;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.ArrayList;
import java.util.List;

import ru.bossach.polyrhythmicmetronome.R;


public class Metronome {

    private static Metronome instance;

    public static Metronome getInstance(Context context) {
        if (instance == null) {
            instance = new Metronome(context);
        }
        return instance;
    }

    private static final int MAX_METRONOMES = 2;

    private static final float DEFAULT_BASE_TONE = 1.4f;
    private static final float[] DEFAULT_ACCENT_TONES = new float[]{1.85f, 1.65f, 1.1f, 1.2f};
    private static final float DEFAULT_EXTRA_TONE = 1f;

    public int getBpm() {
        return bpm;
    }

    private volatile int bpm;

    private Thread metronomeThread;
    private boolean isActive;

    private SoundPool soundPool;

    private int soundId;

    private ArrayList<ClickPattern> patterns;
    private int preCLicks = 3;


    private Metronome(Context context) {
        soundPool = new SoundPool(MAX_METRONOMES, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(context, R.raw.click, 1);

        //TODO
        hardcodePatterns();

    }

    private void hardcodePatterns() {
        patterns = new ArrayList<>();
        patterns.add(new ClickPattern("2/8"));
        patterns.add(new ClickPattern("4/8"));
    }

    private void tick() {

        if(preCLicks-- > 0) {

            return;
        }


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
        for (Float value : toneValuesAtThisTick) {
            click(value);
        }
    }

    private void click(float toneValue) {
        soundPool.play(soundId, 1, 1, 0, 0, toneValue);
    }

    private void click() {
        click(DEFAULT_EXTRA_TONE);
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

        if (metronomeThread != null) {
            stop();
        }
        isActive = true;

        metronomeThread = new Thread(new Ticker());
        metronomeThread.setDaemon(true);
        metronomeThread.start();
    }

    private int getDelay() {
        return (240000 / bpm) / Constants.GLOBAL_DENSITY;
    }

    public void stop() {
        pause();
        reset();
    }

    public void pause() {
        isActive = false;
        if (metronomeThread != null) {
            metronomeThread.interrupt();
            metronomeThread = null;
        }
    }

    public void reset() {
        for (ClickPattern pattern : patterns) {
            pattern.reset();
        }
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    private class Ticker implements Runnable {

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
                } else {
                    try {
                        Thread.sleep(timeToNext);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }
    }
}
