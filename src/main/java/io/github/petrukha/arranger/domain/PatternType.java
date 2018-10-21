package io.github.petrukha.arranger.domain;

import java.util.Objects;

/**
 * Type of pattern, represented by its <i>duration</i>
 * and the flag of <i>cadence</i>
 */
public class PatternType {

    private long duration;
    private boolean isCadence;

    public PatternType(long duration, boolean isCadence) {
        this.duration = duration;
        this.isCadence = isCadence;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isCadence() {
        return isCadence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatternType that = (PatternType) o;
        return duration == that.duration &&
                isCadence == that.isCadence;
    }

    @Override
    public int hashCode() {

        return Objects.hash(duration, isCadence);
    }

    @Override
    public String toString() {
        return isCadence ? "[Cadence]" : String.format("[duration: %d]", getDuration());
    }
}
