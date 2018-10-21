package io.github.petrukha.arranger.service;

import io.github.petrukha.arranger.domain.Chord;
import io.github.petrukha.arranger.domain.Harmony;
import io.github.petrukha.arranger.domain.PatternType;
import io.github.petrukha.arranger.domain.TimedChord;

import javax.sound.midi.MidiEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates arrangements out of {@link Harmony}
 */
public class ArrangementService {
    private PatternChecker patternChecker;
    private PatternLoader patternLoader;

    public ArrangementService(PatternChecker patternChecker, PatternLoader patternLoader) {
        this.patternChecker = patternChecker;
        this.patternLoader = patternLoader;
    }

    /**
     * Compose an arrangement
     *
     * @return arrangement as a list of {@link MidiEvent}
     */
    public List<MidiEvent> arrange(String arrangementType, Harmony harmony) {
        List<PatternTypedChord> initialTypedChords = detectInitialPatternTypes(harmony);
        List<PatternTypedChord> resultingTypedChords = calculateResultingTypedChords(arrangementType, initialTypedChords);

        List<MidiEvent> result = new ArrayList<>();
        long shift = 0;
        for (PatternTypedChord patternTypedChord : resultingTypedChords) {
            for (MidiEvent event :patternLoader.load(
                    arrangementType,
                    patternTypedChord.getChord(),
                    patternTypedChord.getPatternType())) {
                event.setTick(event.getTick() + shift);
                result.add(event);
            }
            shift += patternTypedChord.getPatternType().getDuration();
        }
        return result;
    }

    private List<PatternTypedChord> calculateResultingTypedChords(String arrangementType, List<PatternTypedChord> initialTypedChords) {
        List<PatternTypedChord> result = new ArrayList<>();
        initialTypedChords.forEach(typedChord -> {
            calculateResultingTypedChords(arrangementType, typedChord).forEach(result::add);
        });
        return result;
    }

    private List<PatternTypedChord> calculateResultingTypedChords(String arrangementType, PatternTypedChord typedChord) {
        List<PatternType> availablePatternTypes =
                new ArrayList<>(patternChecker.checkAvailablePatternTypes(arrangementType, typedChord.getChord().getType()));

        boolean cadencePresent = availablePatternTypes.stream().anyMatch(t -> t.isCadence());
        availablePatternTypes.removeIf(t -> t.isCadence());

        availablePatternTypes.sort((t1, t2) -> Math.round(Math.signum(t2.getDuration() - t1.getDuration())));

        if (typedChord.getPatternType().isCadence()) {
            return Collections.singletonList(new PatternTypedChord(
                    typedChord.getChord(),
                    cadencePresent ? new PatternType(0, true) : availablePatternTypes.get(0)));
        }

        List<PatternTypedChord> result = calculateChordsRecursively(typedChord, availablePatternTypes);
        if (result == null) {
            throw new IllegalStateException(String.format("It was impossible to find patterns for chord %s of type %s",
                    typedChord.getChord(), typedChord.getPatternType()));
        }
        return result;
    }

    /**
     * @return list of {@link PatternTypedChord} or null if chords cannot be calculated
     */
    private List<PatternTypedChord> calculateChordsRecursively(PatternTypedChord typedChord, List<PatternType> availablePatternTypes) {
        List<PatternType> fittingPatternTypes = availablePatternTypes.stream()
                .filter(e -> e.getDuration() <= typedChord.getPatternType().getDuration())
                .collect(Collectors.toList());
        if (fittingPatternTypes.isEmpty()) {
            return null;
        }

        List<PatternTypedChord> result = new ArrayList<>();
        result.add(new PatternTypedChord(typedChord.getChord(), fittingPatternTypes.get(0)));
        if (typedChord.getPatternType().getDuration() == fittingPatternTypes.get(0).getDuration()) {
            return result;
        }
        List<PatternTypedChord> innerCallResult = calculateChordsRecursively(new PatternTypedChord(typedChord.getChord(),
                        new PatternType(typedChord.getPatternType().getDuration() - fittingPatternTypes.get(0).getDuration(), false)),
                        fittingPatternTypes);
        if (innerCallResult != null) {
            result.addAll(innerCallResult);
            return result;
        }
        return calculateChordsRecursively(typedChord, fittingPatternTypes.stream().skip(1).collect(Collectors.toList()));
    }

    private List<PatternTypedChord> detectInitialPatternTypes(Harmony harmony) {
        List<PatternTypedChord> result = new ArrayList<>();

        List<TimedChord> chords = harmony.getChords();
        for (int i = 1; i < chords.size(); i++) {
            result.add(new PatternTypedChord(
                       harmony.getChords().get(i - 1).getChord(),
                       new PatternType(
                               chords.get(i).getTime() - chords.get(i - 1).getTime(),
                               false)));
        }
        result.add(new PatternTypedChord(harmony.getChords().get(harmony.getChords().size() - 1).getChord(),
                new PatternType(0, true)));
        return result;
    }

    /**
     * A convinience class, represents a tuple of a {@link Chord} and a {@link PatternType}
     */
    private class PatternTypedChord {
        private Chord chord;
        private PatternType patternType;

        public PatternTypedChord(Chord chord, PatternType patternType) {
            this.chord = chord;
            this.patternType = patternType;
        }

        public Chord getChord() {
            return chord;
        }

        public PatternType getPatternType() {
            return patternType;
        }
    }
}
