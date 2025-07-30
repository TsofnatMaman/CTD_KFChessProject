package webSocket.client;

import interfaces.IBoard;
import interfaces.IPlayerCursor;
import pieces.Position;
import player.PlayerCursor;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class MainClient2 {
    public static void main(String[] args) {
        try {
            URI uri = new URI("ws://localhost:8025/ws/game");
            ChessClientEndpoint clientEndpoint = new ChessClientEndpoint(uri);

            // הרץ את ההמתנה על thread נפרד כדי לא לחסום את ה-main thread
            new Thread(() -> {
                try {
                    int playerId = clientEndpoint.waitForPlayerId();
                    IBoard board = clientEndpoint.waitForBoard();

                    IPlayerCursor cursor = new PlayerCursor(new Position(0, 0), Color.BLUE);
                    SinglePlayerBoardPanel panel = new SinglePlayerBoardPanel(board, cursor, playerId);

                    panel.setOnAction(pos -> clientEndpoint.sendPosition(pos));

                    // יצירת GUI ב־EDT (Event Dispatch Thread)
                    SwingUtilities.invokeLater(() -> {
                        JFrame frame = new JFrame("שחקן #" + playerId);
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        frame.getContentPane().add(panel);
                        frame.pack();
                        frame.setVisible(true);
                        panel.requestFocusInWindow();
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
