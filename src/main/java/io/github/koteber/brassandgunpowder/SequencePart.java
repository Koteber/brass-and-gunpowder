package io.github.koteber.brassandgunpowder;

public class SequencePart {
    public SequencePart(SequenceDirections sequenceDirections, int length) {
        this.direction = sequenceDirections;
        this.length = length;
    }

    public enum SequenceDirections { LEFT, RIGHT, UP, DOWN; }
    public SequenceDirections direction;
    public int length = 25;
}
