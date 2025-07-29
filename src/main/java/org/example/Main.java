package org.example;

import board.BoardConfig;
import board.Dimension;
import game.Game;
import interfaces.IGame;
import interfaces.IPlayer;
import pieces.Position;
import player.Player;
import player.PlayerCursor;
import view.GamePanel;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("KFChess");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            BoardConfig boardConfig = new BoardConfig(new Dimension(8),new Dimension(64*8));

            IPlayer p1 = new Player("player 1",new PlayerCursor(new Position(0,0), Color.RED), boardConfig);
            IPlayer p2 = new Player("player 2",new PlayerCursor(new Position(7,7),Color.BLUE), boardConfig);

            IGame game = new Game(boardConfig ,new IPlayer[]{p1, p2});
            GamePanel gameView = new GamePanel(game);

            // Add debug prints
            System.out.println("Debug: Initial game state setup");

            gameView.run();

            frame.setContentPane(gameView);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
