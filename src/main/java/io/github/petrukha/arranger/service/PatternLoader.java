package io.github.petrukha.arranger.service;

import io.github.petrukha.arranger.domain.Chord;
import io.github.petrukha.arranger.domain.PatternType;

import javax.sound.midi.MidiEvent;
import java.util.List;

public interface PatternLoader {
    List<MidiEvent> load(String arrangementType, Chord chord, PatternType patternType);
}
