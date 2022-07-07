package dev.morazzer.smarterinventories.inventories;

public record Position(int row, int column) {
    public Position(int index) {
        this(index / 9, index % 9);
    }

    public Position(int index, double column) {
        this(index / (int) column, index % (int) column);
    }

    public Position add(Position other) {
        return new Position(row + other.row, column + other.column);
    }
}
