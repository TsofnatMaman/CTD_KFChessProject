package game;

import board.Board;
import board.BoardConfig;
import board.KFCEngine;
import interfaces.IBoard;
import interfaces.IGame;
import interfaces.IPlayer;

import events.listeners.CapturedLogger;
import events.listeners.GameEndLogger;
import events.listeners.JumpsLogger;
import events.listeners.MovesLogger;

/**
 * Factory class for assembling and creating a complete game instance.
 * Handles dependency injection for board, rules engine, and players.
 */
public class GameFactory {

    /**
     * Creates a fully initialized game with the specified board configuration and players.
     *
     * @param bc      Board configuration
     * @param players Array of players
     * @return Fully initialized IGame instance
     */
    public static IGame createNewGame(BoardConfig bc, IPlayer[] players) {
        // Create the rules engine.
        KFCEngine rulesEngine = new KFCEngine();

        // Create the board with injected rules engine and players.
        IBoard board = new Board(bc, rulesEngine, players);

        // Initialize event loggers.
        new MovesLogger();
        new JumpsLogger();
        new CapturedLogger();
        new GameEndLogger();

        // Return the assembled Game instance.
        return new Game(board, players);
    }
}
