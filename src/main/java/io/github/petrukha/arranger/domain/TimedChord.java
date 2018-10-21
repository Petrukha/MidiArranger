package io.github.petrukha.arranger.domain;

public class TimedChord {
    private Chord chord;
    /**
     * Time is measured from the beginning of the composition
     */
    private long time;

    public TimedChord() {
    }

    public TimedChord(Chord chord, long time) {
        this.chord = chord;
        this.time = time;
    }

    public Chord getChord() {
        return chord;
    }

    public void setChord(Chord chord) {
        this.chord = chord;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
