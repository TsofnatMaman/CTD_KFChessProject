package pieces;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void testConstructorAndGetters() {
        Position pos = new Position(3, 5);
        assertEquals(3, pos.getRow());
        assertEquals(5, pos.getCol());
    }

    @Test
    void testDxDy() {
        Position a = new Position(3, 5);
        Position b = new Position(1, 2);

        assertEquals(3, a.dx(b)); // col difference
        assertEquals(2, a.dy(b)); // row difference
    }

    @Test
    void testEqualsAndHashCode() {
        Position a = new Position(2, 4);
        Position b = new Position(2, 4);
        Position c = new Position(3, 4);

        assertEquals(a, b);
        assertNotEquals(a, c);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a.hashCode(), c.hashCode());
    }

    @Test
    void testAddMethod() {
        Position a = new Position(1, 1);
        Position b = a.add(2, 3);
        assertEquals(3, b.getRow());
        assertEquals(4, b.getCol());
    }

    @Test
    void testCopy() {
        Position a = new Position(4, 7);
        Position b = a.copy();
        assertEquals(a, b);
        assertNotSame(a, b); // חייב להיות אובייקט חדש
    }

    @Test
    void testRowColumnManipulations() {
        Position pos = new Position(5, 5);

        pos.addOneRow();
        assertEquals(6, pos.getRow());
        pos.addOneCol();
        assertEquals(6, pos.getCol());

        pos.reduceOneRow();
        assertEquals(5, pos.getRow());
        pos.reduceOneCol();
        assertEquals(5, pos.getCol());
    }

    @Test
    void testToStringFormat() {
        Position pos = new Position(2, 3);
        String str = pos.toString();
        // בדיקה בסיסית שהפורמט נכון עם הספייס/סימן מ-constants
        assertTrue(str.contains(Integer.toString(pos.getRow())));
        assertTrue(str.contains(Integer.toString(pos.getCol())));
    }
}
