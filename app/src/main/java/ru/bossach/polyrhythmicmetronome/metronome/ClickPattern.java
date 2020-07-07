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
    private static final int MAX_NUMERATOR = 256;
    private List<Tone> pattern;

    private String patternString;

    private int curIndex = 0;

    public String getPatternString() {
        return patternString;
    }

    public ClickPattern() {
        this("4/4");
    }

    public ClickPattern(String patternString) {
        parsePattern(patternString);
    }

    private void parsePattern(String patternStr) {
        List<Tone> pattern = new ArrayList<>();
        StringBuilder patternLine = new StringBuilder();

        //Так надо, чтобы split не отбраVсывал пустые такты в конце
        String[] tacts = (patternStr + "*").split(TACT_SEPARATOR);
        String[] tactDivided;
        int numerator = DEFAULT_NUMERATOR;
        int denominator = DEFAULT_DENOMINATOR;

        for (String tact : tacts) {

            if (patternLine.length() != 0) {
                patternLine.append(':');
            }

            tactDivided = tact.split(SIZE_SEPARATOR);

            if (tactDivided.length > 0) {
                String tmp = tactDivided[0].replaceAll("\\D", "");
                if (tmp.length() > 0) {
                    numerator = Integer.parseInt(tmp);
                }
                if (tactDivided.length > 1) {
                    tmp = tactDivided[1].replaceAll("\\D", "");
                    if (tmp.length() > 0) {
                        denominator = Integer.parseInt(tmp);
                    }
                }
            }

            if (numerator == 0) {
                Log.w("Parsing pattern", "Zero numerator, using default");
                numerator = DEFAULT_NUMERATOR;
            }

            if (numerator > 256) {
                numerator = MAX_NUMERATOR;
            }

            if (denominator > MAX_DENOMINATOR || !isTwoDegree(denominator)) {
                Log.w("Parsing pattern", "Invalid denominator, using default");
                denominator = DEFAULT_DENOMINATOR;
            }

            patternLine.append(numerator);
            patternLine.append('/');
            patternLine.append(denominator);


            int stepsInTick = MAX_DENOMINATOR / denominator;
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

        this.patternString = patternLine.toString();
        Log.d("Parsing pattern", "Parsed pattern line - \"" + this.patternString + "\"");

        this.pattern = pattern;
//        return pattern;


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
