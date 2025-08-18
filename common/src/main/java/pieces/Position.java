package pieces;

import java.io.Serializable;
import java.util.Objects;

public class Position implements Serializable {
    private int r;
    private int c;

    // Empty constructor required for Jackson
    public Position() {}

    // Standard constructor
    public Position(int r, int c){
        this.r = r;
        this.c = c;
    }

    // Public getters and setters

    public int getRow() {
        return r;
    }

    public int getCol() {
        return c;
    }

    public int dx(Position other){
        return c - other.c;
    }

    public int dy(Position other){
        return r - other.r;
    }

    @Override
    public boolean equals(Object obj){
        return obj instanceof Position && ((Position)obj).r == r && ((Position)obj).c == c;
    }

    public Position add(int x, int y){
        return new Position(r + x, c + y);
    }

    @Override
    public String toString() {
    return r + constants.PieceConstants.POSITION_SEPARATOR + c; // Use separator from PieceConstants
    }

    public void reduceOneRow(){
        r--;
    }

    public void reduceOneCol(){
        c--;
    }

    public void addOneRow(){
        r++;
    }

    public void addOneCol(){
        c++;
    }

    public Position copy(){
        return new Position(getRow(), getCol());
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, c);
    }
}
