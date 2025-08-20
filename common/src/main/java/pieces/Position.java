package pieces;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a position on the board.
 */
public class Position implements Serializable {

    private int row;
    private int col;

    /** Empty constructor required for Jackson */
    public Position() {}

    /**
     * Creates a position with the given row and column.
     *
     * @param row Row index
     * @param col Column index
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /** Returns the row index */
    public int getRow() {
        return row;
    }

    /** Returns the column index */
    public int getCol() {
        return col;
    }

    /**
     * Returns the horizontal difference between this position and another.
     */
    public int dx(Position other) {
        return col - other.col;
    }

    /**
     * Returns the vertical difference between this position and another.
     */
    public int dy(Position other) {
        return row - other.row;
    }

    /** Returns a new Position by adding x and y offsets */
    public Position add(int dRow, int dCol) {
        return new Position(row + dRow, col + dCol);
    }

    /** Increment or decrement helpers */
    public void addOneRow() { row++; }
    public void addOneCol() { col++; }
    public void reduceOneRow() { row--; }
    public void reduceOneCol() { col--; }

    /** Returns a copy of this position */
    public Position copy() {
        return new Position(row, col);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Position p && p.row == row && p.col == col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        // Use the separator from PieceConstants
        return row + constants.PieceConstants.POSITION_SEPARATOR + col;
    }
}
