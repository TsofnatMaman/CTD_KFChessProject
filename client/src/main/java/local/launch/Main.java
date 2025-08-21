package local.launch;

import board.BoardConfig;
import constants.BoardConstants;
import constants.GameConstants;
import game.GameFactory;
import game.GameLoop;
import interfaces.IGame;
import interfaces.IGameLoop;
import interfaces.IPlayer;
import local.controller.Controller;
import local.view.BoardPanel;
import pieces.Position;
import player.PlayerCursor;
import player.PlayerFactory;
import viewUtils.game.GamePanel;
import viewUtils.game.PlayerInfoPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Entry point for launching a local KFChess game without a server.
 * <p>
 * This class sets up the Swing UI, initializes the board, players, controller,
 * and starts the game loop in local mode.
 * </p>
 */
public class Main {

    /**
     * Application entry point.
     * <p>
     * This method initializes the Swing UI, creates the game model, sets up
     * players, controllers, panels, and starts the local game loop.
     * </p>
     *
     * @param args program arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // ------------------ Frame ------------------
            JFrame frame = new JFrame("KFChess - Local Mode");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // ------------------ Board ------------------
            BoardConfig boardConfig = new BoardConfig(
                    new Dimension(BoardConstants.BOARD_ROWS, BoardConstants.BOARD_COLS),     // logical grid (rows × cols)
                    new Dimension(BoardConstants.BOARD_PANEL_WIDTH, BoardConstants.BOARD_PANEL_HEIGHT), // display size in pixels
                    new Dimension(BoardConstants.BOARD_WIDTH_M, BoardConstants.BOARD_HEIGHT_M)  // logical board size
            );

            // ------------------ Players ------------------
            IPlayer[] players = PlayerFactory.createPlayers(
                    new String[]{"Player 1", "Player 2"},
                    boardConfig
            );

            // Each player gets a cursor for selection/navigation
            PlayerCursor pc1 = new PlayerCursor(new Position(0, 0), players[0].getColor());
            PlayerCursor pc2 = new PlayerCursor(new Position(0, 0), players[1].getColor());

            // ------------------ Game ------------------
            IGame game = GameFactory.createNewGame(boardConfig, players);

            // ------------------ Board Panel ------------------
            BoardPanel boardPanel = new BoardPanel(game.getBoard(), pc1, pc2);

            // ------------------ Game View ------------------
            GamePanel gameView = new GamePanel(
                    boardPanel,
                    Arrays.stream(players)
                            .map(PlayerInfoPanel::new) // one info panel per player
                            .toList()
            );
            frame.setContentPane(gameView);

            // ------------------ Controller ------------------
            Controller controller = new Controller(game, gameView);
            boardPanel.setOnPlayerAction(controller::handlePlayerMove);

            // ------------------ Frame Settings ------------------
            frame.pack();
            frame.setLocationRelativeTo(null); // center window
            frame.setVisible(true);

            // ------------------ Start Game ------------------
            System.out.println("Debug: Starting local KFChess game");

            IGameLoop gameLoop = new GameLoop(game);
            gameLoop.run(); // start game loop
        });
    }
}
