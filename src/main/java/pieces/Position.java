package pieces;

import java.io.Serializable;

public class Position implements Serializable {
    private int r;
    private int c;

    // קונסטרקטור ריק דרוש ל-Jackson
    public Position() {}

    // קונסטרקטור רגיל
    public Position(int r, int c){
        this.r = r;
        this.c = c;
    }

    // getters ו-setters ציבוריים

    public int getRow() {
        return r;
    }

    public void setRow(int r) {
        this.r = r;
    }

    public int getCol() {
        return c;
    }

    public void setCol(int c) {
        this.c = c;
    }

    // שאר הקוד שלך נשאר זהה

    public int dx(Position other){
        return r - other.r;
    }

    public int dy(Position other){
        return c - other.c;
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
        return r + "," + c;
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

    public static Position fromString(String s){
        String[] rowCol = s.split(",");
        return new Position(Integer.parseInt(rowCol[0].trim()), Integer.parseInt(rowCol[1].trim()));
    }
}
