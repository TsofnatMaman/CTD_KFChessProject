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
 * Main game class. Handles game loop, player turns, command execution, and win detection.
 */
public class Game implements IGame {

    private final IPlayer[] players;
    private final Queue<ICommand> commandQueue;
    private final IBoard board;

    private Timer timer;
    private long startTimeNano;
    private volatile boolean running;

    /**
     * Constructs a new game with the given board configuration and players.
     *
     * @param bc      Board configuration
     * @param players Array of players
     */
    public Game(BoardConfig bc, IPlayer[] players) {
        this.board = new Board(bc, players);
        this.players = players;
        this.commandQueue = new LinkedList<>();
        this.running = false;
        this.startTimeNano = 0;

        // Initialize loggers for game events
        new MovesLogger();
        new JumpsLogger();
        new CapturedLogger();
        new GameEndLogger();
    }

    /**
     * Adds a command to the queue for execution.
     *
     * @param cmd Command to execute
     */
    private void addCommand(ICommand cmd) {
        commandQueue.add(cmd);
    }

    /**
     * Executes all commands currently in the queue.
     */
    @Override
    public void update() {
        ICommand cmd;
        while ((cmd = commandQueue.poll()) != null) {
            cmd.execute();
        }
    }

    /**
     * Retrieves a player by their ID.
     *
     * @param id Player ID
     * @return IPlayer instance
     */
    @Override
    public IPlayer getPlayerById(int id) {
        if (id < 0 || id >= players.length) {
            throw new IllegalArgumentException("Invalid player id: " + id);
        }
        return players[id];
    }

    @Override
    public IBoard getBoard() {
        return board;
    }

    /**
     * Handles a selection made by a player at a given position.
     * If a valid command is returned, it is added to the command queue.
     *
     * @param player   The player making the selection
     * @param selected Selected position
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
     * Checks for a winner. Returns the winning player or null if no winner yet.
     *
     * @return Winning IPlayer or null
     */
    @Override
    public IPlayer win() {
        if (board.getPlayers()[0].isFailed()) return players[1];
        if (board.getPlayers()[1].isFailed()) return players[0];
        return null;
    }

    /**
     * Starts the game loop using a Swing Timer.
     */
    @Override
    public void run() {
        if (timer == null) {
            timer = new Timer(GameConstants.GAME_LOOP_MS, e -> tick());
        }
        if (!running) {
            running = true;
            startTimeNano = System.nanoTime();
        }
        timer.start();
    }

    /**
     * Single tick of the game loop.
     * Updates commands, the board, and publishes game events.
     */
    private void tick() {
        IPlayer winner = win();
        if (winner == null) {
            update();
            board.updateAll();
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
