package interfaces;

public interface IGameLoop  extends Runnable{

    /**
     * Runs the main game loop.
     */
    @Override
    void run();
}
