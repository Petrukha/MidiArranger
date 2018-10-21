package io.github.petrukha.arranger.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates multiple {@link TimedChord}s
 */
public class Harmony {
    private List<TimedChord> chords = new ArrayList<>();

    public Harmony(TimedChord... chords) {
        this.chords.addAll(Arrays.asList(chords));
        sortChords();
    }

    public void addChord(TimedChord chord) {
        chords.add(chord);
        sortChords();
    }

    public void removeChord(TimedChord chord) {
        chords.remove(chord);
    }

    public List<TimedChord> getChords() {
        return Collections.unmodifiableList(chords);
    }

    private void sortChords() {
        Collections.sort(this.chords, (o1, o2) -> {
            long time1 = o1.getTime();
            long time2 = o2.getTime();
            if (time1 > time2) {
                return 1;
            }
            if (time2 > time1) {
                return -1;
            }
            return 0;
        });
    }
}
