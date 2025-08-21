package game;

/**
 * Represents a view of the game board.
 * Provides a method to request repainting when the board state changes.
 */
public interface IBoardView {

    /**
     * Requests the board view to refresh its display.
     */
    void repaint();
}
