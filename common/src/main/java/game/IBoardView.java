package game;

/**
 * Interface representing a view of the game board.
 * Allows the board to be repainted when its state changes.
 */
public interface IBoardView {

    /**
     * Requests the board view to repaint itself.
     */
    void repaint();
}
