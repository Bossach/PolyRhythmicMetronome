package ru.bossach.polyrhythmicmetronome;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


class Metronome {

    private volatile int bpm;

    private Thread metronome;
    private boolean isActive;

    private SoundPool soundPool;

    private int soundId;


    Metronome(Context context) {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(context, R.raw.click, 1);
    }

    private void click() {
        soundPool.play(soundId, 1, 1, 0, 0, 1);
    }

    void start() {

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
                        click();
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
        return 60000 / bpm;
    }

    void stop() {
        isActive = false;
        if (metronome != null) {
            metronome.interrupt();
            metronome = null;
        }
    }

    void setBpm(int bpm) {
        this.bpm = bpm;
    }
}
