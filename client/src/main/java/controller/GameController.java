package controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.GameDTO;
import dto.PlayerSelectedDTO;
import endpoint.launch.ChessClientEndpoint;
import utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GameController implements Runnable {


    private final ChessClientEndpoint client;
    private final ObjectMapper mapper;

    private final List<GameEventListener> listeners = new ArrayList<>();

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
            }
            LogUtils.logDebug("GameController stopped listening thread");
        }
    }

    public void addListener(GameEventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
            LogUtils.logDebug("Added listener: " + listener);
        }
    }

    public void removeListener(GameEventListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
            LogUtils.logDebug("Removed listener: " + listener);
        }
    }

    @Override
    public void run() {
        LogUtils.logDebug("GameController run() loop started");
        while (running) {
            try {
                String message = client.pollNextMessage(1, TimeUnit.SECONDS);
                if (message == null) continue;

                LogUtils.logDebug("Received message: " + message);

                JsonNode root = mapper.readTree(message);
                String type = root.path("type").asText("");
                JsonNode dataNode = root.path("data");

                switch (type) {
                    case constants.CommandNames.WAIT -> {
                        LogUtils.logDebug("Dispatching WAIT message");
                        fireWaitMessage(dataNode.asText(""));
                    }
                    case constants.CommandNames.GAME_INIT -> {
                        LogUtils.logDebug("Dispatching GAME_INIT message");
                        GameDTO gameDTO = mapper.treeToValue(dataNode, GameDTO.class);
                        fireGameInit(gameDTO);
                    }
                    case constants.CommandNames.PLAYER_SELECTED -> {
                        LogUtils.logDebug("Dispatching PLAYER_SELECTED message");
                        PlayerSelectedDTO cmd = mapper.treeToValue(dataNode, PlayerSelectedDTO.class);
                        firePlayerSelected(cmd);
                    }
                    case constants.CommandNames.PLAYER_ID -> {
                        int playerId = dataNode.asInt(-1);
                        LogUtils.logDebug("Dispatching PLAYER_ID message: " + playerId);
                        firePlayerId(playerId);
                    }
                    default -> {
                        LogUtils.logDebug("Unknown message type: " + type);
                        fireUnknownMessage(type);
                    }
                }
            } catch (InterruptedException e) {
                LogUtils.logDebug("GameController thread interrupted, stopping...");
                running = false;
            } catch (Exception e) {
                LogUtils.logDebug("Error in GameController loop"+ e);
            }
        }
        LogUtils.logDebug("GameController run() loop ended");
    }

    // Event firing methods:

    private void fireWaitMessage(String message) {
        synchronized (listeners) {
            for (GameEventListener l : listeners) {
                try {
                    l.onWaitMessage(message);
                } catch (Exception e) {
                    LogUtils.logDebug("Listener error in onWaitMessage"+ e);
                }
            }
        }
    }

    private void fireGameInit(GameDTO gameDTO) {
        synchronized (listeners) {
            for (GameEventListener l : listeners) {
                try {
                    l.onGameInit(gameDTO);
                } catch (Exception e) {
                    LogUtils.logDebug("Listener error in onGameInit"+ e);
                }
            }
        }
    }

    private void firePlayerSelected(PlayerSelectedDTO cmd) {
        synchronized (listeners) {
            for (GameEventListener l : listeners) {
                try {
                    l.onPlayerSelected(cmd);
                } catch (Exception e) {
                    LogUtils.logDebug("Listener error in onPlayerSelected"+ e);
                }
            }
        }
    }

    private void firePlayerId(int playerId) {
        synchronized (listeners) {
            for (GameEventListener l : listeners) {
                try {
                    l.onPlayerId(playerId);
                } catch (Exception e) {
                    LogUtils.logDebug("Listener error in onPlayerId"+ e);
                }
            }
        }
    }

    private void fireUnknownMessage(String type) {
        synchronized (listeners) {
            for (GameEventListener l : listeners) {
                try {
                    l.onUnknownMessage(type);
                } catch (Exception e) {
                    LogUtils.logDebug("Listener error in onUnknownMessage"+ e);
                }
            }
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
