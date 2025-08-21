package endpoint.controller;

import interfaces.IPlayer;
import viewUtils.board.BaseBoardPanel;

/**
 * Interface representing the game UI components and updates.
 * <p>
 * Provides methods for updating the UI in response to game events such as moves, timer updates, and game end.
 * </p>
 */
public interface IGameUI {

    /**
     * Called when the game state is updated.
     * <p>
     * The UI should refresh the board and any relevant components.
     * </p>
     */
    void onGameUpdate();

    /**
     * Called when the game ends to display the winner.
     *
     * @param winner the player who won the game
     */
    void onWin(IPlayer winner);

    /**
     * Updates the timer label with the provided text.
     *
     * @param text the text to display in the timer label
     */
    void updateTimerLabel(String text);

    /**
     * Returns the board panel component for the game.
     *
     * @return the BaseBoardPanel instance
     */
    BaseBoardPanel getBoardPanel();
}
