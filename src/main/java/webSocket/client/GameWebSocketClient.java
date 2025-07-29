package webSocket.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import pieces.Position;
import view.BoardPanel;
import view.GamePanel;
import webSocket.server.dto.GameDelta;

import javax.swing.*;
import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class GameWebSocketClient {

    private final URI serverUri;
    private Session session;

    private final ObjectMapper mapper = new ObjectMapper();

    private final GamePanel gamePanel;
    private final BoardPanel boardPanel;

    private GameDelta currentDelta;

    public GameWebSocketClient(URI serverUri, GamePanel gamePanel, BoardPanel boardPanel) {
        this.serverUri = serverUri;
        this.gamePanel = gamePanel;
        this.boardPanel = boardPanel;
    }

    public void connect() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            container.connectToServer(this, serverUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connected");
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        try {
            GameDelta delta = mapper.readValue(message, GameDelta.class);
            this.currentDelta = delta;

            SwingUtilities.invokeLater(() -> {
                updateModelFromDelta(delta);
                boardPanel.repaint();
                gamePanel.repaint();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket closed: " + closeReason);
        this.session = null;
    }

    @OnError
    public void onError(Session session, Throwable thr) {
        thr.printStackTrace();
    }

    /**
     * מעדכן את המודל המקומי לפי המידע שהתקבל משרת WebSocket.
     */
    private void updateModelFromDelta(GameDelta delta) {
        // כאן צריך לעדכן את הלוח (board) לפי pieces מהרשימה
        // אם יש לך מתודה שמתעדכנת לפי רשימת כלים, תקרא לה כאן
        // לדוגמה:
        // boardPanel.setBoard( ...המרה של pieces ל-IBoard... );

        // לעת עתה, נעדכן רק בחירה ומהלכים חוקיים לפי השחקן
        int playerId = delta.getPlayerId();
        boardPanel.setSelectedForPlayer(playerId, delta.getSelectedPiece());
        boardPanel.setLegalMovesForPlayer(playerId, delta.getLegalMoves());

        // אפשר כאן להוסיף גם טיפול במצב המשחק, זמן, הודעות וכו' לפי הצורך
    }

    /**
     * שולח לשרת את מיקום הבחירה של השחקן.
     */
    public void sendSelection(Position pos) {
        if (session != null && session.isOpen()) {
            try {
                String json = mapper.writeValueAsString(pos);
                session.getBasicRemote().sendText(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
