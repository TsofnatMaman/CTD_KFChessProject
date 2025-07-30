package webSocket.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import pieces.Position;
import webSocket.server.dto.GameDTO;

import javax.websocket.*;
import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

@ClientEndpoint
public class ChessClientEndpoint {

    private Session session;
    private final ObjectMapper mapper = new ObjectMapper();
    private final BlockingQueue<GameDTO> deltaQueue = new LinkedBlockingQueue<>();

    private int playerId = -1;

    public ChessClientEndpoint(URI serverUri) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, serverUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("✅ Connected to server.");
    }


    private final CountDownLatch idLatch = new CountDownLatch(1);
    private final CountDownLatch deltaLatch = new CountDownLatch(1);

    @OnMessage
    public void onMessage(String message) {
        try {
            if (message.contains("\"type\":\"playerId\"")) {
                int start = message.indexOf("\"id\":") + 5;
                int end = message.indexOf("}", start);
                playerId = Integer.parseInt(message.substring(start, end));
                System.out.println("🎮 Received playerId: " + playerId);
                idLatch.countDown();
            } else {
                GameDTO delta = mapper.readValue(message, GameDTO.class);
                System.out.println("📩 Received GameDelta for player " + playerId);
                deltaQueue.put(delta); // ← הוספה קריטית!
                deltaLatch.countDown();
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to parse GameDelta: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("❌ Disconnected: " + closeReason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("⚠️ WebSocket Error: " + throwable.getMessage());
    }

    // --- שליחת בחירת מיקום לשרת ---
    public void sendSelection(Position pos) {
        try {
            String json = mapper.writeValueAsString(pos);
            session.getAsyncRemote().sendText(json);
            System.out.println("➡️ Sent position: " + pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- למשיכת העדכון האחרון ---
    public GameDTO waitForNextDelta() throws InterruptedException {
        return deltaQueue.take();
    }

    public int getPlayerId() {
        return playerId;
    }
}
