package interfaces;

import pieces.Position;

/**
 * Interface for game logic and state management.
 */
public interface IGame extends Runnable {

    /**
     * Executes all commands in the queue and updates game state.
     */
    void update();

    /**
     * Retrieves a player by their unique ID.
     * @param id The player's ID
     * @return The corresponding IPlayer instance
     */
    IPlayer getPlayerById(int id);

    /**
     * Gets the game board instance.
     * @return The board
     */
    IBoard getBoard();

    /**
     * Handles a selection action from a player.
     * @param player The player making the selection
     * @param selected The selected position on the board
     */
    void handleSelection(IPlayer player, Position selected);

    /**
     * Handles a selection action using player ID.
     * @param playerId The player's ID
     * @param selected The selected position
     */
    void handleSelection(int playerId, Position selected);

    /**
     * Returns the winning player.
     * @return IPlayer instance of the winner, or null if no winner
     */
    IPlayer win();

    /**
     * Runs the main game loop.
     */
    @Override
    void run();

    /**
     * Gets the start time of the game in nanoseconds.
     * @return Start time in nanoseconds
     */
    long getStartTimeNano();

    /**
     * Gets the elapsed time since the game started in milliseconds.
     * @return Elapsed milliseconds
     */
    long getElapsedMillis();

    /**
     * Retrieves all players in the game.
     * @return Array of players
     */
    IPlayer[] getPlayers();

    /**
     * Checks if the game is currently running.
     * @return True if running, false otherwise
     */
    boolean isRunning();

    /**
     * Sets the name of a player by ID.
     * @param playerId The player's ID
     * @param name The new name
     */
    void setPlayerName(int playerId, String name);
}
