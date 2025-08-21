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
 * A factory class for creating and assembling game components.
 * This class is responsible for dependency injection.
 */
public class GameFactory {

    /**
     * Creates and initializes a complete new game instance.
     *
     * @param bc      Board configuration.
     * @param players Array of players.
     * @return A fully initialized IGame instance.
     */
    public static IGame createNewGame(BoardConfig bc, IPlayer[] players) {
        // Step 1: Create the rules engine.
        KFCEngine rulesEngine = new KFCEngine();

        // Step 2: Create the board, injecting the rules engine.
        IBoard board = new Board(bc, rulesEngine, players);

        // Step 3: Initialize event loggers. This is a side concern,
        // so it's best handled during assembly.
        new MovesLogger();
        new JumpsLogger();
        new CapturedLogger();
        new GameEndLogger();

        // Step 4: Create and return the Game instance, injecting the board and players.
        return new Game(board, players);
    }
}