package game;

import board.Board;
import board.BoardConfig;
import constants.GameConstants;
import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.listeners.CapturedLogger;
import events.listeners.GameEndLogger;
import events.listeners.JumpsLogger;
import events.listeners.MovesLogger;
import interfaces.ICommand;
import interfaces.IBoard;
import interfaces.IGame;
import interfaces.IPlayer;
import pieces.Position;
import utils.LogUtils;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

/**
 * Main game logic and state management.
 * Handles command execution, player turns, and win condition.
 */
public class Game implements IGame {
    private final IPlayer[] players;
    /** Queue of commands to be executed. */
    private final Queue<ICommand> commandQueue;
    /** The board instance for the game. */
    private final IBoard board;

    private Timer timer;

    private long startTimeNano;
    private volatile boolean running;

    public Game(BoardConfig bc, IPlayer[] players) {
        this.board = new Board(bc, players);
        this.players = players;
        this.commandQueue = new LinkedList<>();
        this.running = false;

        // startTimeNano will be set when run() is invoked, not here.
        this.startTimeNano = 0;

        new MovesLogger();
        new JumpsLogger();
        new CapturedLogger();
        new GameEndLogger();
    }

    /**
     * Adds a command to the queue.
     *
     * @param cmd The command to add
     */
    private void addCommand(ICommand cmd) {
        commandQueue.add(cmd);
    }

    /**
     * Executes all commands in the queue.
     */
    @Override
    public void update() {
        ICommand cmd;
        while ((cmd = commandQueue.poll()) != null) {
            cmd.execute();
        }
    }

    @Override
    public IPlayer getPlayerById(int id) {
        if (id < 0 || id >= players.length) {
            throw new IllegalArgumentException("Invalid player id: " + id);
        }
        return players[id];
    }

    /**
     * Gets the game board.
     *
     * @return The board instance
     */
    @Override
    public IBoard getBoard() {
        return board;
    }

    /**
     * Handles selection for the given player.
     * Adds the resulting command to the queue if not null.
     *
     * @param player   The player making a selection
     * @param selected The selected position
     */
    @Override
    public void handleSelection(IPlayer player, Position selected) {
        Optional<ICommand> cmd = player.handleSelection(getBoard(), selected);
        cmd.ifPresent(this::addCommand);
    }

    @Override
    public void handleSelection(int playerId, Position selected) {
        handleSelection(getPlayerById(playerId), selected);
    }

    /**
     * Returns the winning player, or null if no winner yet.
     */
    @Override
    public IPlayer win() {
        if (board.getPlayers()[0].isFailed())
            return players[1];
        if (board.getPlayers()[1].isFailed())
            return players[0];
        return null;
    }

    @Override
    public void run() {
        if (timer == null) {
            // set running before starting timer to avoid race in elapsed time queries
            timer = new Timer(GameConstants.GAME_LOOP_MS, e -> tick());
        }
        if (!running) {
            running = true;
            startTimeNano = System.nanoTime();
        }
        timer.start();
    }

    /**
     * Single tick of the game loop. Separated to allow deterministic unit testing.
     */
    private void tick() {
        IPlayer winner = win();
        if (winner == null) {
            update();
            board.updateAll();
            EventPublisher.getInstance().publish(EGameEvent.GAME_UPDATE, new GameEvent(EGameEvent.GAME_UPDATE, null));
        } else {
            EventPublisher.getInstance().publish(EGameEvent.GAME_ENDED, new GameEvent(EGameEvent.GAME_ENDED, null));
            stopGameLoop();
            LogUtils.logDebug("Game Over. Winner: Player " + winner.getName());
        }
    }

    private void stopGameLoop() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        running = false;
    }

    @Override
    public long getStartTimeNano() {
        return startTimeNano;
    }

    @Override
    public long getElapsedMillis() {
        if (startTimeNano == 0) return 0;
        return (System.nanoTime() - startTimeNano) / 1_000_000;
    }


    @Override
    public IPlayer[] getPlayers() {
        return players;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void setPlayerName(int playerId, String name) {
        getPlayerById(playerId).setName(name);
    }
}
