import org.junit.jupiter.api.Test;
import pieces.Position;

import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {

    @Test
    void testDx_and_Dy_computation() {
        Position p1 = new Position(1, 1);
        Position p2 = new Position(3, 4);

        assertEquals(2, p2.dx(p1));
        assertEquals(3, p2.dy(p1));
    }

    @Test
    void testToString_and_Equals() {
        Position pos = new Position(2, 5);
        assertEquals("2,5", pos.toString());
        assertEquals(new Position(2, 5), pos);
    }
}
