package endpoint.controller;

import interfaces.IPlayer;
import viewUtils.board.BaseBoardPanel;

/**
 * Interface representing the game UI components and updates.
 */
public interface IGameUI {
    /** Called when the game state is updated. */
    void onGameUpdate();

    /** Called when the game ends to display the winner. */
    void onWin(IPlayer winner);

    /** Updates the timer label with the provided text. */
    void updateTimerLabel(String text);

    /** Returns the board panel component. */
    BaseBoardPanel getBoardPanel();
}
