package utils;

import org.junit.jupiter.api.Test;
import pieces.Position;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Utils utility class.
 * Verifies correct behavior of getName(Position) and formatElapsedTime(long).
 */
class UtilsTest {

    /**
     * Test getName() for the standard lower-left corner of the board (A1).
     */
    @Test
    void testGetNameStandardCase() {
        Position pos = new Position(0, 0); // A1
        String result = Utils.getName(pos);
        assertEquals("A1", result, "getName should return 'A1' for position (0,0)");
    }

    /**
     * Test getName() for a middle-of-the-board position (D5).
     */
    @Test
    void testGetNameMiddleBoard() {
        Position pos = new Position(3, 4); // D5
        String result = Utils.getName(pos);
        assertEquals("D5", result, "getName should return 'D5' for position (3,4)");
    }

    /**
     * Test getName() for the upper-right corner of the board (H8).
     */
    @Test
    void testGetNameUpperBound() {
        Position pos = new Position(7, 7); // H8
        String result = Utils.getName(pos);
        assertEquals("H8", result, "getName should return 'H8' for position (7,7)");
    }

    /**
     * Test formatElapsedTime() for zero milliseconds.
     * Should return "00:00".
     */
    @Test
    void testFormatElapsedTimeZero() {
        assertEquals("00:00", Utils.formatElapsedTime(0), "formatElapsedTime(0) should return '00:00'");
    }

    /**
     * Test formatElapsedTime() for exact seconds (45 seconds).
     */
    @Test
    void testFormatElapsedTimeExactSeconds() {
        assertEquals("00:45", Utils.formatElapsedTime(45_000), "45 seconds should format as '00:45'");
    }

    /**
     * Test formatElapsedTime() for exact minutes (2 minutes).
     */
    @Test
    void testFormatElapsedTimeExactMinutes() {
        assertEquals("02:00", Utils.formatElapsedTime(120_000), "120,000 ms should format as '02:00'");
    }

    /**
     * Test formatElapsedTime() for minutes and seconds (1 min 30 sec).
     */
    @Test
    void testFormatElapsedTimeMinutesAndSeconds() {
        assertEquals("01:30", Utils.formatElapsedTime(90_000), "90,000 ms should format as '01:30'");
    }

    /**
     * Test formatElapsedTime() for large values (1 hour -> 60 minutes).
     */
    @Test
    void testFormatElapsedTimeLargeValue() {
        long millis = 60L * 60 * 1000; // 1 hour
        assertEquals("60:00", Utils.formatElapsedTime(millis), "1 hour should format as '60:00'");
    }
}
