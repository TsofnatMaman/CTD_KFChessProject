package interfaces;

import pieces.Position;

/**
 * Interface for game logic and state management.
 */
public interface IGame extends Runnable{

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

    void handleSelection(int playerId, Position selected);

    /**
     * Returns the winner: 0 for player 1, 1 for player 2, -1 if no winner yet.
     * @return The winner's player index, or -1 if no winner
     */
    IPlayer win();

    @Override
    void run();

    long getStartTimeNano();

    long getElapsedMillis();

    IPlayer[] getPlayers();

    boolean isRunning();

    void setPlayerName(int playerId, String name);
}
