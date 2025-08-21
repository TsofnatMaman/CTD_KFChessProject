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


public class GameLoop implements IGameLoop {
    private Timer timer;
    private final IGame game;

    public GameLoop(IGame game){
        this.game = game;
    }

    /**
     * Starts the game loop using a Swing Timer.
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
     * Single tick of the game loop.
     * Updates commands, the board, and publishes game events.
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
     * Stops the game loop.
     */
    private void stopGameLoop() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        game.setRunning(false);
    }
}
