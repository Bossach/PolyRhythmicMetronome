package ru.bossach.polyrhythmicmetronome.metronome;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ClickPattern implements Iterator<Tone> {

    private static final String TACT_SEPARATOR = ":";
    private static final String SIZE_SEPARATOR = "/";
    private static final int DEFAULT_NUMERATOR = 4;
    private static final int DEFAULT_DENOMINATOR = 4;
    private static final int MAX_DENOMINATOR = Constants.GLOBAL_DENSITY;
    private List<Tone> pattern;
    private int curIndex = 0;

    public ClickPattern() {
        this("4/4");
    }

    public ClickPattern(String patternStr) {
        this.pattern = parsePattern(patternStr);
    }

    private List<Tone> parsePattern(String patternStr) {
        List<Tone> pattern = new ArrayList<>();


        String[] tacts = patternStr.split(TACT_SEPARATOR);
        String[] tactDivided;
        int numerator = DEFAULT_NUMERATOR;
        int denominator = DEFAULT_DENOMINATOR;

        for (String tact : tacts) {
            tactDivided = tact.split(SIZE_SEPARATOR);

            if (tactDivided.length > 0) {
                numerator = Integer.parseInt(tactDivided[0].replaceAll("\\D", ""));
                if (tactDivided.length > 1) {
                    denominator = Integer.parseInt(tactDivided[1].replaceAll("\\D", ""));
                }
            }

            if (denominator > MAX_DENOMINATOR || !isTwoDegree(denominator)) {
                Log.d("Parsing pattern", "Invalid denominator, using default");
                denominator = DEFAULT_DENOMINATOR;
            }

            int stepsInTick = MAX_DENOMINATOR/denominator;
            int stepsCount = numerator * stepsInTick;
            boolean isFirst = true;

            for (int step = 0; step < stepsCount; step++) {
                if (isFirst) {
                    isFirst = false;
                    pattern.add(Tone.ACCENT);
                } else if (step % stepsInTick == 0) {
                    pattern.add(Tone.BASE);
                } else {
                    pattern.add(Tone.EMPTY);
                }
            }


        }


        return pattern;


    }

    private boolean isTwoDegree(int value) {
        return Integer.toBinaryString(value).replaceAll("0", "").length() == 1;
    }


    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Tone next() {
        Tone res = pattern.get(curIndex);
        curIndex = (curIndex + 1) % pattern.size();
        return res;
    }

    public void reset() {
        curIndex = 0;
    }
}
