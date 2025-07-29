package pieces;

/**
 * Represents a position on the board.
 */
public class Position {
    int r;
    int c;

    /**
     * Constructs a position with the given row and column.
     */
    public Position(int r, int c){
        this.r = r;
        this.c = c;
    }

    /**
     * Gets the row index.
     */
    public int getRow() {
        return r;
    }

    /**
     * Gets the column index.
     */
    public int getCol() {
        return c;
    }

    /**
     * Returns the difference in rows between this and another position.
     */
    public int dx(Position other){
        return r-other.r;
    }

    /**
     * Returns the difference in columns between this and another position.
     */
    public int dy(Position other){
        return c-other.c;
    }

    @Override
    public boolean equals(Object obj){
        return obj instanceof Position && ((Position)obj).r == r && ((Position)obj).c == c;
    }

    /**
     * Returns a new position offset by x rows and y columns.
     */
    public Position add(int x, int y){
        return new Position(r+x, c+y);
    }

    @Override
    public String toString() {
        return r+","+c;
    }

    /**
     * Reduces the row index by one.
     */
    public void reduceOneRow(){
        r--;
    }

    /**
     * Reduces the column index by one.
     */
    public void reduceOneCol(){
        c--;
    }

    /**
     * Increases the row index by one.
     */
    public void addOneRow(){
        r++;
    }

    /**
     * Increases the column index by one.
     */
    public void addOneCol(){
        c++;
    }

    /**
     * Returns a copy of this position.
     */
    public Position copy(){
        return new Position(getRow(), getCol());
    }
}

