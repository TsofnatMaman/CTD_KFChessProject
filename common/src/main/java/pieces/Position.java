package pieces;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a position on the board with row and column indices.
 */
public class Position implements Serializable {

    private int row;
    private int col;

    /** Default constructor required for serialization/deserialization. */
    public Position() {}

    /**
     * Creates a position with the specified row and column.
     *
     * @param row Row index
     * @param col Column index
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /** Returns the row index. */
    public int getRow() {
        return row;
    }

    /** Returns the column index. */
    public int getCol() {
        return col;
    }

    /**
     * Returns the horizontal difference between this position and another.
     *
     * @param other Another position
     * @return Difference in columns
     */
    public int dx(Position other) {
        return col - other.col;
    }

    /**
     * Returns the vertical difference between this position and another.
     *
     * @param other Another position
     * @return Difference in rows
     */
    public int dy(Position other) {
        return row - other.row;
    }

    /**
     * Returns a new Position offset by the given row and column differences.
     *
     * @param dRow Row offset
     * @param dCol Column offset
     * @return New Position instance
     */
    public Position add(int dRow, int dCol) {
        return new Position(row + dRow, col + dCol);
    }

    /** Increment or decrement helpers */
    public void addOneRow() { row++; }
    public void addOneCol() { col++; }
    public void reduceOneRow() { row--; }
    public void reduceOneCol() { col--; }

    /** Returns a copy of this position. */
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
        return row + constants.PieceConstants.POSITION_SEPARATOR + col;
    }
}
