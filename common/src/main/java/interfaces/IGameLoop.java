package interfaces;

/**
 * Represents the main game loop.
 * Typically runs in its own thread or via a scheduler.
 */
public interface IGameLoop extends Runnable {

    /**
     * Executes the main game loop.
     * Should repeatedly update the game state and render until the game ends.
     */
    @Override
    void run();
}
