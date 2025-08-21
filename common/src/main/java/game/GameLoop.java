package game;

import constants.GameConstants;
import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import interfaces.IGame;
import interfaces.IGameLoop;
import interfaces.IPlayer;
import utils.LogUtils;

import javax.swing.*;

/**
 * Implements the main game loop using a Swing Timer.
 * Responsible for updating game state, processing commands, and publishing events.
 */
public class GameLoop implements IGameLoop {

    private Timer timer;
    private final IGame game;

    /**
     * Constructs a GameLoop for the given game.
     *
     * @param game The game instance to run
     */
    public GameLoop(IGame game) {
        this.game = game;
    }

    /**
     * Starts the game loop using a Swing Timer.
     * Initializes the start time if the game is not already running.
     */
    @Override
    public void run() {
        if (timer == null) {
            timer = new Timer(GameConstants.GAME_LOOP_MS, e -> tick());
        }
        if (!game.isRunning()) {
            game.setRunning(true);
            game.setStartTimeNano(System.nanoTime());
        }
        timer.start();
    }

    /**
     * Executes a single tick of the game loop.
     * Updates the board, executes queued commands, and publishes relevant events.
     */
    private void tick() {
        IPlayer winner = game.win();
        if (winner == null) {
            game.update();
            EventPublisher.getInstance().publish(
                    EGameEvent.GAME_UPDATE,
                    new GameEvent(EGameEvent.GAME_UPDATE, null)
            );
        } else {
            EventPublisher.getInstance().publish(
                    EGameEvent.GAME_ENDED,
                    new GameEvent(EGameEvent.GAME_ENDED, null)
            );
            stopGameLoop();
            LogUtils.logDebug("Game Over. Winner: Player " + winner.getName());
        }
    }

    /**
     * Stops the game loop and sets the game as not running.
     */
    private void stopGameLoop() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        game.setRunning(false);
    }
}
