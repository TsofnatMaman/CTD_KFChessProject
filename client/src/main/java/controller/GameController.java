package controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.EventType;
import dto.GameDTO;
import dto.PlayerSelectedDTO;
import endpoint.launch.ChessClientEndpoint;
import utils.LogUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Controller אחראי על קבלת הודעות מהשרת והפצתן ל־listeners.
 */
public class GameController implements Runnable {

    private final ChessClientEndpoint client;
    private final ObjectMapper mapper;
    private final List<GameEventListener> listeners = new CopyOnWriteArrayList<>();

    private volatile boolean running = true;
    private Thread listenerThread;

    public GameController(ChessClientEndpoint client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    public void startListening() {
        if (listenerThread == null || !listenerThread.isAlive()) {
            listenerThread = new Thread(this, "GameController-Listener");
            listenerThread.setDaemon(true);
            running = true;
            listenerThread.start();
            LogUtils.logDebug("GameController started listening thread");
        }
    }

    public void stopListening() {
        running = false;
        if (listenerThread != null) {
            listenerThread.interrupt();
            try {
                listenerThread.join(2000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            LogUtils.logDebug("GameController stopped listening thread");
        }
    }

    public void addListener(GameEventListener listener) {
        listeners.add(listener);
        LogUtils.logDebug("Added listener: " + listener);
    }

    public void removeListener(GameEventListener listener) {
        listeners.remove(listener);
        LogUtils.logDebug("Removed listener: " + listener);
    }

    @Override
    public void run() {
        LogUtils.logDebug("GameController run() loop started");
        while (running) {
            try {
                String message = client.pollNextMessage(500, TimeUnit.MILLISECONDS);
                if (message == null) continue;

                LogUtils.logDebug("Received message: " + message);

                JsonNode root = mapper.readTree(message);
                String typeStr = root.path("type").asText("");
                JsonNode dataNode = root.path("data");

                EventType type;
                try {
                    type = EventType.valueOf(typeStr);
                } catch (IllegalArgumentException e) {
                    type = EventType.UNKNOWN;
                }

                switch (type) {
                    case WAIT -> fireEvent(l -> l.onWaitMessage(dataNode.asText("")));
                    case GAME_INIT -> fireEvent(l -> {
                        try { l.onGameInit(mapper.treeToValue(dataNode, GameDTO.class)); }
                        catch (Exception e) { LogUtils.logDebug(e.toString()); }
                    });
                    case PLAYER_SELECTED -> fireEvent(l -> {
                        try { l.onPlayerSelected(mapper.treeToValue(dataNode, PlayerSelectedDTO.class)); }
                        catch (Exception e) { LogUtils.logDebug(e.toString()); }
                    });
                    case PLAYER_ID -> fireEvent(l -> l.onPlayerId(dataNode.asInt(-1)));
                    default -> fireEvent(l -> l.onUnknownMessage(typeStr));
                }

            } catch (InterruptedException e) {
                LogUtils.logDebug("GameController thread interrupted, stopping...");
                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception e) {
                LogUtils.logDebug("Error in GameController loop: " + e);
            }
        }
        LogUtils.logDebug("GameController run() loop ended");
    }

    private void fireEvent(Consumer<GameEventListener> action) {
        for (GameEventListener l : listeners) {
            try { action.accept(l); }
            catch (Exception e) { LogUtils.logDebug("Listener error: " + e); }
        }
    }

    public interface GameEventListener {
        void onWaitMessage(String message);
        void onGameInit(GameDTO gameDTO);
        void onPlayerSelected(PlayerSelectedDTO cmd);
        void onPlayerId(int playerId);
        void onUnknownMessage(String type);
    }
}
