package game;

import board.Board;
import board.BoardConfig;
import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.listeners.CapturedLogger;
import events.listeners.GameEndLogger;
import events.listeners.JumpsLogger;
import events.listeners.MovesLogger;
import interfaces.ICommand;
import interfaces.*;
import pieces.Position;
import utils.LogUtils;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Main game logic and state management.
 * Handles command execution, player turns, and win condition.
 */
public class Game implements IGame {
    private final IPlayer []players;
    /** Queue of commands to be executed. */
    private Queue<ICommand> commandQueue;
    /** The board instance for the game. */
    private final IBoard board;

    private Timer timer;

    private long startTimeNano;
    private boolean running;

    public Game(BoardConfig bc, IPlayer[]players) {
        this.board = new Board(bc,players);
        this.players = players;
        commandQueue = new LinkedList<>();
        this.startTimeNano = System.nanoTime();

        new MovesLogger();
        new JumpsLogger();
        new CapturedLogger();
        new GameEndLogger();
    }

    /**
     * Adds a command to the queue.
     * @param cmd The command to add
     */
    @Override
    public void addCommand(ICommand cmd){
        commandQueue.add(cmd);
    }

    /**
     * Executes all commands in the queue.
     */
    @Override
    public void update() {
        while (!commandQueue.isEmpty()) {
            commandQueue.poll().execute();
        }
    }

    @Override
    public IPlayer getPlayerById(int id){
        return players[id];
    }

    /**
     * Gets the game board.
     * @return The board instance
     */
    @Override
    public IBoard getBoard() {
        return board;
    }

    /**
     * Handles selection for the given player.
     * Adds the resulting command to the queue if not null.
     * @param player The player making a selection
     */
    @Override
    public void handleSelection(IPlayer player, Position selected){
        ICommand cmd = player.handleSelection(getBoard(), selected);
        if(cmd != null){
            addCommand(cmd);
        }
    }

    @Override
    public void handleSelection(int playerId, Position selected){
        handleSelection(getPlayerById(playerId), selected);
    }

    /**
     * Returns the winner: 0 for player 1, 1 for player 2, -1 if no winner yet.
     * @return The winner's player index, or -1 if no winner
     */
    @Override
    public IPlayer win(){
        if(board.getPlayers()[0].isFailed())
            return players[1];
        if(board.getPlayers()[1].isFailed())
            return players[0];
        return null;
    }

    @Override
    public void run(){
        if (timer == null) {
            timer = new Timer(16, e -> {
                if(!running){
                    running = true;
                }

                if (win() == null) {
                    update();
                    board.updateAll();
                    EventPublisher.getInstance().publish(EGameEvent.GAME_UPDATE, new GameEvent(EGameEvent.GAME_UPDATE, null));
                } else {
                    EventPublisher.getInstance().publish(EGameEvent.GAME_ENDED, new GameEvent(EGameEvent.GAME_ENDED, null));
                    stopGameLoop();
                    LogUtils.logDebug("Game Over. Winner: Player " + win().getName());
                }
            });
        }
        timer.start();
    }

    @Override
    public void stopGameLoop() {
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
    public long getElapsedTimeNano() {
        if (!running) return 0;
        long elapsedNano = System.nanoTime() - startTimeNano;
        return elapsedNano / 1_000_000; // convert from nano to milliseconds
    }

    @Override
    public IPlayer[] getPlayers() {
        return players;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
