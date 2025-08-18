package pieces;

import java.io.Serializable;
import java.util.Objects;

public class Position implements Serializable {
    private int row;
    private int col;

    // Empty constructor required for Jackson
    public Position() {}

    // Standard constructor
    public Position(int r, int c){
        this.row = r;
        this.col = c;
    }

    // Public getters and setters

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int dx(Position other){
        return col - other.col;
    }

    public int dy(Position other){
        return row - other.row;
    }

    @Override
    public boolean equals(Object obj){
        return obj instanceof Position && ((Position)obj).row == row && ((Position)obj).col == col;
    }

    public Position add(int x, int y){
        return new Position(row + x, col + y);
    }

    @Override
    public String toString() {
    return row + constants.PieceConstants.POSITION_SEPARATOR + col; // Use separator from PieceConstants
    }

    public void reduceOneRow(){
        row--;
    }

    public void reduceOneCol(){
        col--;
    }

    public void addOneRow(){
        row++;
    }

    public void addOneCol(){
        col++;
    }

    public Position copy(){
        return new Position(getRow(), getCol());
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
