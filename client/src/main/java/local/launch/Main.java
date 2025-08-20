package local.launch;

import board.BoardConfig;
import game.Game;
import interfaces.IGame;
import interfaces.IPlayer;
import local.controller.Controller;
import local.view.BoardPanel;
import pieces.Position;
import player.PlayerCursor;
import player.PlayerFactory;
import viewUtils.GamePanel;
import viewUtils.PlayerInfoPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Entry point for launching a local KFChess game without server.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // ------------------ Frame ------------------
            JFrame frame = new JFrame("KFChess - Local Mode");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // ------------------ Board ------------------
            BoardConfig boardConfig = new BoardConfig(
                    new Dimension(8, 8),     // logical grid
                    new Dimension(700, 700), // display size
                    new Dimension(500, 500)  // logical board size
            );

            // ------------------ Players ------------------
            IPlayer[] players = PlayerFactory.createPlayers(
                    new String[]{"Player 1", "Player 2"},
                    boardConfig
            );

            PlayerCursor pc1 = new PlayerCursor(new Position(0,0), players[0].getColor());
            PlayerCursor pc2 = new PlayerCursor(new Position(0,0), players[1].getColor());

            // ------------------ Game ------------------
            IGame game = new Game(boardConfig, players);

            // ------------------ Board Panel ------------------
            BoardPanel bp = new BoardPanel(game.getBoard(), pc1, pc2);


            // ------------------ Game View ------------------
            GamePanel gameView = new GamePanel(bp, Arrays.stream(players).map(PlayerInfoPanel::new).toList());
            frame.setContentPane(gameView);

            // ------------------ Controller ------------------
            Controller controller = new Controller(game, gameView);
            bp.setController(controller);

            // ------------------ Frame Settings ------------------
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // ------------------ Start Game ------------------
            System.out.println("Debug: Starting local KFChess game");
            game.run(); // optional: may run in a separate thread if needed
        });
    }
}
