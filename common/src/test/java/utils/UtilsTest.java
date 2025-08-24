package utils;

import org.junit.jupiter.api.Test;
import pieces.Position;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Utils}.
 */
class UtilsTest {

    @Test
    void getNameReturnsStandardNotation() {
        assertEquals("A1", Utils.getName(new Position(0, 0)));
        assertEquals("D5", Utils.getName(new Position(3, 4)));
    }

    @Test
    void getNameNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> Utils.getName(null));
    }

    @Test
    void formatElapsedTimeZero() {
        assertEquals("00:00", Utils.formatElapsedTime(0));
    }

    @Test
    void formatElapsedTimePositive() {
        assertEquals("00:01", Utils.formatElapsedTime(1_000));
        assertEquals("01:01", Utils.formatElapsedTime(61_000));
    }

    @Test
    void formatElapsedTimeNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> Utils.formatElapsedTime(-1));
    }
}
