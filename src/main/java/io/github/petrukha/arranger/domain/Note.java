package io.github.petrukha.arranger.domain;

/**
 * Represents a note
 */
public enum Note {
    A(0),
    A_SHARP(1),
    B(2),
    C(3),
    C_SHARP(4),
    D(5),
    D_SHARP(6),
    E(7),
    F(8),
    F_SHARP(9),
    G(10),
    G_SHARP(11);

    Note(int number) {
        this.number = number;
    }

    private int number;

    public int getNumber() {
        return number;
    }

}
