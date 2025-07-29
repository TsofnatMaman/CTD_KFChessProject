package board;

public class Dimension {
    private int x;
    private int y;

    public Dimension(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Dimension(int x){
        this(x,x);
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
