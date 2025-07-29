package interfaces;

import game.IBoardView;
import pieces.Position;

/**
 * Interface for game logic and state management.
 */
public interface IGame {

    /**
     * Adds a command to the queue.
     * @param cmd The command to add
     */
    void addCommand(ICommand cmd);

    /**
     * Executes all commands in the queue.
     */
    void update();

    IPlayer getPlayerById(int id);

    /**
     * Gets the game board.
     * @return The board instance
     */
    IBoard getBoard();

    /**
     * Handles selection for the given player.
     * @param player The player making a selection
     */
    void handleSelection(IPlayer player, Position selected);

    /**
     * Returns the winner: 0 for player 1, 1 for player 2, -1 if no winner yet.
     * @return The winner's player index, or -1 if no winner
     */
    IPlayer win();

    void run(IBoardView bv);

    long getElapsedTimeMillis();

    String toJson();
}
