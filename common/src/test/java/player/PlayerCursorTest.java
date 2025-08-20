package player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pieces.Position;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class PlayerCursorTest {

    private PlayerCursor cursor;
    private Position pos;

    @BeforeEach
    void setUp() {
        // Initialize cursor at a specific position with a color
        pos = new Position(3, 3);
        cursor = new PlayerCursor(pos, Color.RED);
    }

    @Test
    void testInitialPositionAndColor() {
        // Check initial position and color
        assertEquals(3, cursor.getPosition().getRow());
        assertEquals(3, cursor.getPosition().getCol());
        assertEquals(Color.RED, cursor.getColor());
    }

    @Test
    void testMoveUp() {
        // Moving up decreases row, but not below 0
        cursor.moveUp();
        assertEquals(2, pos.getRow());
        cursor.moveUp();
        cursor.moveUp();
        cursor.moveUp();
        assertEquals(0, pos.getRow());
    }

    @Test
    void testMoveDown() {
        // Moving down increases row, but not beyond ROWS-1
        cursor.moveDown();
        assertEquals(4, pos.getRow());
        for (int i = 0; i < 10; i++) cursor.moveDown();
        assertEquals(cursor.ROWS - 1, pos.getRow());
    }

    @Test
    void testMoveLeft() {
        // Moving left decreases column, but not below 0
        cursor.moveLeft();
        assertEquals(2, pos.getCol());
        for (int i = 0; i < 10; i++) cursor.moveLeft();
        assertEquals(0, pos.getCol());
    }

    @Test
    void testMoveRight() {
        // Moving right increases column, but not beyond COLS-1
        cursor.moveRight();
        assertEquals(4, pos.getCol());
        for (int i = 0; i < 10; i++) cursor.moveRight();
        assertEquals(cursor.COLS - 1, pos.getCol());
    }

    @Test
    void testDrawDoesNotThrow() {
        // Ensure draw method runs without throwing exceptions
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        assertDoesNotThrow(() -> cursor.draw(g2d, 800, 800));

        g2d.dispose();
    }
}
