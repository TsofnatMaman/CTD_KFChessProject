package pieces;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void dxAndDyReturnRelativeDifferences() {
        Position a = new Position(4, 6);
        Position b = new Position(1, 2);

        assertEquals(4, a.dx(b));
        assertEquals(3, a.dy(b));
        assertEquals(-4, b.dx(a));
        assertEquals(-3, b.dy(a));
    }

    @Test
    void addReturnsNewOffsetPosition() {
        Position base = new Position(2, 3);
        Position result = base.add(1, -1);

        assertEquals(3, result.getRow());
        assertEquals(2, result.getCol());
        assertEquals(2, base.getRow());
        assertEquals(3, base.getCol());
        assertNotSame(base, result);
    }

    @Test
    void incrementAndDecrementHelpersAdjustCoordinates() {
        Position p = new Position(0, 0);

        p.addOneRow();
        p.addOneCol();
        assertEquals(1, p.getRow());
        assertEquals(1, p.getCol());

        p.reduceOneRow();
        p.reduceOneCol();
        assertEquals(0, p.getRow());
        assertEquals(0, p.getCol());
    }

    @Test
    void equalsAndHashCodeDependOnRowAndCol() {
        Position a1 = new Position(5, 7);
        Position a2 = new Position(5, 7);
        Position b = new Position(7, 5);

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
        assertNotEquals(a1, b);
        assertNotEquals(a1.hashCode(), b.hashCode());
    }

    @Test
    void toStringUsesSeparator() {
        Position p = new Position(3, 4);
        String expected = "3" + constants.PieceConstants.POSITION_SEPARATOR + "4";
        assertEquals(expected, p.toString());
    }
}
