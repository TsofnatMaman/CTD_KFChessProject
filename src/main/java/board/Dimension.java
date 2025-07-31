package board;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Dimension implements Serializable {
    private final int x;
    private final int y;

    @JsonCreator
    public Dimension(
            @JsonProperty("x") int x,
            @JsonProperty("y") int y
    ) {
        this.x = x;
        this.y = y;
    }

    public Dimension(int x) {
        this(x, x);
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
