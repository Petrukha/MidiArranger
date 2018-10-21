package io.github.petrukha.arranger.service;

import io.github.petrukha.arranger.domain.PatternType;

import java.util.List;

public interface PatternChecker {
    List<PatternType> checkAvailablePatternTypes(String arrangementType, String chordType);
}
