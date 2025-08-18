package board;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dimension dimension = (Dimension) o;
        return getX() == dimension.getX() && getY() == dimension.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    @Override
    public String toString() {
        return "Dimension{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
