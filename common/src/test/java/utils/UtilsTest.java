package utils;

import org.junit.jupiter.api.Test;
import pieces.Position;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void testGetNameStandardCase() {
        Position pos = new Position(0, 0); // A1
        String result = Utils.getName(pos);
        assertEquals("A1", result);
    }

    @Test
    void testGetNameMiddleBoard() {
        Position pos = new Position(3, 4); // D5
        String result = Utils.getName(pos);
        assertEquals("D5", result);
    }

    @Test
    void testGetNameUpperBound() {
        Position pos = new Position(7, 7); // H8
        String result = Utils.getName(pos);
        assertEquals("H8", result);
    }

    @Test
    void testFormatElapsedTimeZero() {
        assertEquals("00:00", Utils.formatElapsedTime(0));
    }

    @Test
    void testFormatElapsedTimeExactSeconds() {
        assertEquals("00:45", Utils.formatElapsedTime(45_000));
    }

    @Test
    void testFormatElapsedTimeExactMinutes() {
        assertEquals("02:00", Utils.formatElapsedTime(120_000));
    }

    @Test
    void testFormatElapsedTimeMinutesAndSeconds() {
        assertEquals("01:30", Utils.formatElapsedTime(90_000));
    }

    @Test
    void testFormatElapsedTimeLargeValue() {
        // 1 שעה -> 60 דקות
        long millis = 60L * 60 * 1000;
        assertEquals("60:00", Utils.formatElapsedTime(millis));
    }
}
