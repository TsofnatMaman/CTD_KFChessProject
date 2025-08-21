package endpoint.launch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.EventType;
import dto.Message;
import utils.LogUtils;

import javax.websocket.*;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * WebSocket client endpoint for communicating with the chess server.
 *
 * <p>This class handles connection, message sending, message receiving,
 * reconnection logic, and player ID management.</p>
 */
@ClientEndpoint
public class ChessClientEndpoint implements Closeable {

    /** The current WebSocket session */
    private Session session;

    /** Queue to hold incoming messages */
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    /** The player's assigned ID, -1 if not yet assigned */
    private volatile int playerId = -1;

    /** JSON object mapper */
    private final ObjectMapper mapper = new ObjectMapper();

    /** The server endpoint URI */
    private final URI endpointURI;

    /** Executor for scheduling reconnection attempts */
    private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor();

    /** Flag to track if connected */
    private final AtomicBoolean connected = new AtomicBoolean(false);

    /** Flag to track if client is closing */
    private final AtomicBoolean closing = new AtomicBoolean(false);

    /** Counter for reconnect attempts */
    private int reconnectAttempts = 0;

    // ---------------------- Constructor ----------------------

    /**
     * Creates a new ChessClientEndpoint and connects to the server.
     *
     * @param endpointURI the server WebSocket URI
     * @throws Exception if connection fails
     */
    public ChessClientEndpoint(URI endpointURI) throws Exception {
        this.endpointURI = endpointURI;
        connect();
    }

    /**
     * Connects to the server using a WebSocket container.
     *
     * @throws Exception if connection fails
     */
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
        messageQueue.add(message); // store message for external retrieval
        processMessage(message);   // process immediately
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
        // Reconnection handled by onClose
    }

    // ---------------------- Message Processing ----------------------

    /**
     * Processes an incoming JSON message.
     *
     * @param message the raw JSON message
     */
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

    /**
     * Updates the player's ID.
     *
     * @param id the new player ID
     */
    private void updatePlayerId(int id) {
        playerId = id;
        log("Updated playerId to " + playerId);
    }

    // ---------------------- Reconnect Logic ----------------------

    /**
     * Schedules a reconnect attempt using exponential backoff.
     */
    private void scheduleReconnect() {
        reconnectAttempts++;
        long delay = Math.min(60, 1 << Math.min(reconnectAttempts, 6)); // cap at 60 seconds

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

    /**
     * Sends a raw text message to the server.
     *
     * @param message the message to send
     * @throws IllegalStateException if the session is not open
     */
    public void sendText(String message) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        } else {
            throw new IllegalStateException("WebSocket session is not open");
        }
    }

    /**
     * Sends a typed command with data to the server.
     *
     * @param <T> the type of the data
     * @param type the event type
     * @param data the data payload
     * @throws Exception if JSON serialization fails
     */
    public <T> void sendCommand(EventType type, T data) throws Exception {
        Message<T> msg = new Message<>(type, data);
        sendText(mapper.writeValueAsString(msg));
    }

    // ---------------------- Receiving Messages ----------------------

    /**
     * Waits for the next message, blocking if none is available.
     *
     * @return the next message string
     * @throws InterruptedException if interrupted while waiting
     */
    public String waitForNextMessage() throws InterruptedException {
        return messageQueue.take();
    }

    /**
     * Polls for the next message with a timeout.
     *
     * @param timeout the timeout duration
     * @param unit the time unit
     * @return the next message or null if none arrives in time
     * @throws InterruptedException if interrupted while waiting
     */
    public String pollNextMessage(long timeout, TimeUnit unit) throws InterruptedException {
        return messageQueue.poll(timeout, unit);
    }

    /**
     * Gets the player's assigned ID.
     *
     * @return the player ID
     */
    public int getPlayerId() {
        return playerId;
    }

    // ---------------------- Close / Shutdown ----------------------

    /**
     * Closes the WebSocket connection and shuts down the executor.
     */
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

    /**
     * Simple log helper.
     *
     * @param msg the message to log
     */
    private void log(String msg) {
        System.out.println("[ChessClientEndpoint] " + msg);
    }
}
