package ru.bossach.polyrhythmicmetronome;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


class Metronome {

    private final int MEDIAPLAYERS_COUNT = 15;

    private Context context;

    private volatile int bpm;

    private Thread metronome;
    private boolean isActive;

    List<MediaPlayer> players;

    Iterator<MediaPlayer> mediaPlayerIterator;


    Metronome(Context context) {
        this.context = context;
        players = new ArrayList<>(MEDIAPLAYERS_COUNT);
        for (int i = 0; i < MEDIAPLAYERS_COUNT; i++) {
            players.add(MediaPlayer.create(context, R.raw.click));
        }

        mediaPlayerIterator = new Iterator<MediaPlayer>() {
            int index = -1;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public MediaPlayer next() {
                index++;
                index = index % players.size();
                return players.get(index);
            }
        };
    }

    private void click() {
        long before = System.currentTimeMillis() % (60 * 1000);
        mediaPlayerIterator.next().start();
        //System.out.println("Before sound start " + before + "\tDelta " + ((System.currentTimeMillis() % (60 * 1000)) - before));
    }

    public void start() {
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
                    delay = 60000 / bpm;
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
