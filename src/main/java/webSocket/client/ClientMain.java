package webSocket.client;

import view.BoardPanel;
import view.GamePanel;
import player.PlayerCursor;
import pieces.Position;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class ClientMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // כתובת שרת ה-WebSocket שלך (עדכן לפי הצורך)
                URI serverUri = new URI("ws://localhost:8080/chess");

                // יצירת מודל לוח ו-cursors (אם יש צורך, אפשר לשלב מודל פשוט)
                // כאן תוכל להעביר מודל ריק או ממשק Board ריק, כי המצב יתעדכן מהשרת
                BoardPanel boardPanel = new BoardPanel(null,
                        new PlayerCursor(new Position(0, 0), Color.RED),
                        new PlayerCursor(new Position(7, 7), Color.BLUE));

                // GamePanel - אפשר להעביר null או ליצור מודל מתאים (אם יש)
                GamePanel gamePanel = new GamePanel(null); // אם יש צורך תעדכן כאן

                // חשוב להוסיף את ה-boardPanel ל-gamePanel אם זה לא קורה ב-gamePanel עצמו
                gamePanel.add(boardPanel, BorderLayout.CENTER);

                // יצירת לקוח WebSocket
                GameWebSocketClient client = new GameWebSocketClient(serverUri, gamePanel, boardPanel);
                client.connect();

                // יצירת חלון JFrame להצגת המשחק
                JFrame frame = new JFrame("KFChess WebSocket Client");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(gamePanel, BorderLayout.CENTER);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
