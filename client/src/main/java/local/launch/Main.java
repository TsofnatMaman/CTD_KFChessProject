package local.launch;

import board.BoardConfig;
import board.Dimension;
import game.Game;
import interfaces.IGame;
import interfaces.IPlayer;
import local.view.GamePanel;
import player.PlayerFactory;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("KFChess");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            BoardConfig boardConfig = new BoardConfig(new Dimension(8),new Dimension(64*8));

            IPlayer[] players = PlayerFactory.createPlayers(new String[]{"player 1", "player 2"}, boardConfig);

            IGame game = new Game(boardConfig , players);
            GamePanel gameView = new GamePanel(game);

            // Add debug prints
            System.out.println("Debug: Initial game state setup");

            game.run();

            frame.setContentPane(gameView);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
