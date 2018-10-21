package io.github.petrukha.arranger.domain;

import java.util.Objects;

public class Chord {
    private Note bass;
    private Note key;
    private String type;

    public Chord() {
    }

    public Chord(Note bass, Note key, String type) {
        this.bass = bass;
        this.key = key;
        this.type = type;
    }

    public Note getBass() {
        return bass;
    }

    public void setBass(Note bass) {
        this.bass = bass;
    }

    public Note getKey() {
        return key;
    }

    public void setKey(Note key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chord chord = (Chord) o;
        return bass == chord.bass &&
                key == chord.key &&
                Objects.equals(type, chord.type);
    }

    @Override
    public int hashCode() {

        return Objects.hash(bass, key, type);
    }

    @Override
    public String toString() {
        return String.format("[key: %s; bass: %s; type: %s]", getKey().name(), getBass().name(), getType());
    }
}
