package game;

import interfaces.ICommand;
import interfaces.IBoard;
import interfaces.IGame;
import interfaces.IPlayer;
import pieces.Position;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

/**
 * Main game class. Handles game loop, player turns, command execution, and win detection.
 * The board and players are injected into this class.
 */
public class Game implements IGame {

    private final IPlayer[] players;
    private final Queue<ICommand> commandQueue;
    private final IBoard board;

    private long startTimeNano;
    private volatile boolean running;

    /**
     * Constructs a new game instance.
     * The board and players are provided by a factory.
     *
     * @param board   The game board instance.
     * @param players The array of players.
     */
    public Game(IBoard board, IPlayer[] players) {
        this.board = board;
        this.players = players;
        this.commandQueue = new LinkedList<>();
        this.running = false;
        this.startTimeNano = 0;
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

        board.updateAll();

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
    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void setPlayerName(int playerId, String name) {
        getPlayerById(playerId).setName(name);
    }

    @Override
    public void setStartTimeNano(long startTimeNano) {
        this.startTimeNano = startTimeNano;
    }
}