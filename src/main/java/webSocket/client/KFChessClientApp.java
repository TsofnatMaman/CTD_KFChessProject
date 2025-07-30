package webSocket.client;

import pieces.Position;
import player.PlayerCursor;
import utils.LogUtils;

import webSocket.server.dto.GameDTO;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class KFChessClientApp {

    public static void main(String[] args) throws Exception {
        // URI לשרת WebSocket
        URI uri = new URI("ws://localhost:8025/ws/game");
        ChessClientEndpoint client = new ChessClientEndpoint(uri);

        // חכה לעדכון הראשון עם מזהה שחקן ולוח
        GameDTO initialDelta = client.waitForNextDelta();
        int playerId = client.getPlayerId();

        // יצירת GUI בסיסי
        JFrame frame = new JFrame("KFCHESS - Player " + (playerId + 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // יצירת סמן רק לשחקן הנוכחי
        Color color = (playerId == 0) ? Color.RED : Color.BLUE;
        PlayerCursor cursor = new PlayerCursor(new Position(0, 0), color);

        // יצירת לוח תצוגה
        BoardPanel boardPanel = new BoardPanel(null, cursor);

        boardPanel.setBoardFromSnapshot(initialDelta.board);
        boardPanel.setPreferredSize(new Dimension(700, 700));

        // פעולה על מקש או תזוזה תשלח לשרת
        boardPanel.setOnPlayerAction((v) -> {
            client.sendSelection(cursor.getPosition());
        });

        frame.add(boardPanel);
        frame.pack();
        frame.setVisible(true);

        // Thread לעדכון קבוע של הלוח מהשרת
        new Thread(() -> {
            try {
                while (true) {
                    GameDTO delta = client.waitForNextDelta();
                    SwingUtilities.invokeLater(() -> {
                        boardPanel.setBoardFromSnapshot(delta.board);
                        boardPanel.setSelectedForPlayer(delta.players[playerId].pending);
                        boardPanel.setLegalMovesForPlayer(delta.players[playerId].pending);
                    });
                }
            } catch (InterruptedException e) {
                LogUtils.logDebug("Client listener stopped");
            }
        }).start();
    }
}
