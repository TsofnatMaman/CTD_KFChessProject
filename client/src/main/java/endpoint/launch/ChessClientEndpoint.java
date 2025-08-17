package endpoint.launch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dto.EventType;
import utils.LogUtils;

import javax.websocket.*;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@ClientEndpoint
public class ChessClientEndpoint implements Closeable {

    private Session session;
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private volatile int playerId = -1;

    private final ObjectMapper mapper = new ObjectMapper();
    private final URI endpointURI;

    private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicBoolean closing = new AtomicBoolean(false);
    private int reconnectAttempts = 0;

    // ---------------------- Constructor ----------------------

    public ChessClientEndpoint(URI endpointURI) throws Exception {
        this.endpointURI = endpointURI;
        connect();
    }

    private void connect() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, endpointURI);
    }

    // ---------------------- WebSocket Callbacks ----------------------

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        connected.set(true);
        reconnectAttempts = 0;
        log("Connected to server");
    }

    @OnMessage
    public void onMessage(String message) {
        messageQueue.add(message);
        processMessage(message);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        connected.set(false);
        log("Connection closed: " + reason);
        if (!closing.get()) scheduleReconnect();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LogUtils.logDebug("WebSocket error: " + throwable.getMessage());
        // let onClose handle reconnect
    }

    // ---------------------- Message Processing ----------------------

    private void processMessage(String message) {
        try {
            JsonNode root = mapper.readTree(message);
            String type = root.path("type").asText("");
            JsonNode data = root.path("data");

            EventType eventType;
            try {
                eventType = EventType.valueOf(type);
            } catch (IllegalArgumentException e) {
                eventType = EventType.UNKNOWN;
            }

            switch (eventType) {
                case PLAYER_ID -> updatePlayerId(data.asInt(-1));
                default -> LogUtils.logDebug("Unknown message type: " + type);
            }


        } catch (Exception e) {
            LogUtils.logDebug("Failed to parse incoming message: " + e);
        }
    }

    private void updatePlayerId(int id) {
        playerId = id;
        log("Updated playerId to " + playerId);
    }

    // ---------------------- Reconnect ----------------------

    private void scheduleReconnect() {
        reconnectAttempts++;
        long delay = Math.min(60, 1 << Math.min(reconnectAttempts, 6)); // exponential backoff
        reconnectExecutor.schedule(() -> {
            if (closing.get()) return;
            try {
                connect();
            } catch (Exception e) {
                log("Reconnect attempt failed: " + e);
                scheduleReconnect();
            }
        }, delay, TimeUnit.SECONDS);
    }

    // ---------------------- Sending Messages ----------------------

    public void sendText(String message) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        } else {
            throw new IllegalStateException("WebSocket session is not open");
        }
    }

    public void sendCommand(EventType type, String data) throws Exception {
        ObjectNode msg = mapper.createObjectNode();
        msg.put("type", type.toString());
        msg.put("data", data);
        sendText(mapper.writeValueAsString(msg));
    }

    // ---------------------- Receiving Messages ----------------------

    public String waitForNextMessage() throws InterruptedException {
        return messageQueue.take();
    }

    public String pollNextMessage(long timeout, TimeUnit unit) throws InterruptedException {
        return messageQueue.poll(timeout, unit);
    }

    public int getPlayerId() {
        return playerId;
    }

    // ---------------------- Close / Shutdown ----------------------

    @Override
    public void close() {
        closing.set(true);
        connected.set(false);
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                log("Error closing WebSocket session: " + e.getMessage());
            }
        }
        reconnectExecutor.shutdownNow();
    }

    // ---------------------- Helper Logging ----------------------

    private void log(String msg) {
        System.out.println("[ChessClientEndpoint] " + msg);
    }
}
