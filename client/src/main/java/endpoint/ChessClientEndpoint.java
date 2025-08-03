package endpoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    // Simple backoff state
    private int reconnectAttempts = 0;

    public ChessClientEndpoint(URI endpointURI) throws Exception {
        this.endpointURI = endpointURI;
        connect();
    }

    private void connect() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, endpointURI);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        connected.set(true);
        reconnectAttempts = 0;
        System.out.println(utils.ConfigLoader.getMessage("client.connected.log", "Connected to server"));
    }

    @OnMessage
    public void onMessage(String message) {
        messageQueue.add(message);

        try {
            JsonNode root = mapper.readTree(message);
            String type = root.path("type").asText("");
            if (constants.CommandNames.PLAYER_ID.equals(type)) {
                playerId = root.path("data").asInt(-1);
                LogUtils.logDebug(utils.ConfigLoader.getMessage("client.connected.log", "Updated playerId to ") + playerId);
            }
        } catch (Exception e) {
            LogUtils.logDebug("Failed to parse incoming message"+ e);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        connected.set(false);
        System.out.println(utils.ConfigLoader.getMessage("client.disconnected.log", "Connection closed: ") + reason);
        if (!closing.get()) {
            scheduleReconnect();
        }
    }

    @OnError
    public void onError(Session session, Throwable thr) {
        LogUtils.logDebug("WebSocket encountered error"+ thr);
        // let onClose handle reconnect if needed
    }

    private void scheduleReconnect() {
        reconnectAttempts++;
        long delay = Math.min(60, (1 << Math.min(reconnectAttempts, 6))) ; // exponential backoff capped
        reconnectExecutor.schedule(() -> {
            try {
                if (closing.get()) return;
                connect();
            } catch (Exception e) {
                LogUtils.logDebug("Reconnect attempt failed"+ e);
                scheduleReconnect();
            }
        }, delay, TimeUnit.SECONDS);
    }

    public int getPlayerId() {
        return playerId;
    }

    public void sendText(String message) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        } else {
            throw new IllegalStateException("WebSocket session is not open");
        }
    }

    // Helper for structured JSON message
    public void sendCommand(String type, String data) throws Exception {
        com.fasterxml.jackson.databind.node.ObjectNode msg = mapper.createObjectNode();
        msg.put("type", type);
        msg.put("data", data);
        sendText(mapper.writeValueAsString(msg));
    }

    public String waitForNextMessage() throws InterruptedException {
        return messageQueue.take();
    }

    public String pollNextMessage(long timeout, TimeUnit unit) throws InterruptedException {
        return messageQueue.poll(timeout, unit);
    }

    @Override
    public void close() {
        closing.set(true);
        connected.set(false);
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                LogUtils.logDebug("Error closing WebSocket session"+e.getMessage());
            }
        }
        reconnectExecutor.shutdownNow();
    }
}
