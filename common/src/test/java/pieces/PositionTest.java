package pieces;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void testConstructorAndGetters() {
        // Constructor should set row and column correctly
        Position pos = new Position(3, 5);
        assertEquals(3, pos.getRow());
        assertEquals(5, pos.getCol());
    }

    @Test
    void testDxDy() {
        // dx and dy should compute differences correctly
        Position a = new Position(3, 5);
        Position b = new Position(1, 2);

        assertEquals(3, a.dx(b)); // col difference
        assertEquals(2, a.dy(b)); // row difference
    }

    @Test
    void testEqualsAndHashCode() {
        // equals and hashCode should behave consistently
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
        // add method should create new Position with summed values
        Position a = new Position(1, 1);
        Position b = a.add(2, 3);
        assertEquals(3, b.getRow());
        assertEquals(4, b.getCol());
    }

    @Test
    void testCopy() {
        // copy should create a new equal object
        Position a = new Position(4, 7);
        Position b = a.copy();
        assertEquals(a, b);
        assertNotSame(a, b);
    }

    @Test
    void testRowColumnManipulations() {
        // increment and decrement row/col methods
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
        // toString should include row and column numbers
        Position pos = new Position(2, 3);
        String str = pos.toString();
        assertTrue(str.contains(Integer.toString(pos.getRow())));
        assertTrue(str.contains(Integer.toString(pos.getCol())));
    }
}
