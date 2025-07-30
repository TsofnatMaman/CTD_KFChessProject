package webSocket.client;

import javax.websocket.*;
import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@ClientEndpoint
public class ChessClientEndpoint {

    private Session session;
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private volatile int playerId = -1;

    public ChessClientEndpoint(URI endpointURI) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, endpointURI);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to server");
    }

    @OnMessage
    public void onMessage(String message) {
        messageQueue.add(message);

        // בדיקת מזהה שחקן מתוך ההודעה
        if (message.contains("\"playerId\"")) {
            // לדוגמה עדכון playerId מתוך ההודעה (אפשר לשפר עם JSON parsing)
            try {
                // פשוט נשלוף את המספר בין המרכאות - או להשתמש ב-Jackson
                int idx = message.indexOf("playerId");
                if (idx >= 0) {
                    String sub = message.substring(idx);
                    String number = sub.replaceAll("[^0-9]", "");
                    if (!number.isEmpty()) {
                        playerId = Integer.parseInt(number);
                        System.out.println("Updated playerId to " + playerId);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Connection closed: " + reason);
    }

    public int getPlayerId() {
        return playerId;
    }

    public void sendText(String message) throws Exception {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        } else {
            throw new IllegalStateException("WebSocket session is not open");
        }
    }

    public String waitForNextMessage() throws InterruptedException {
        return messageQueue.take();
    }
}
