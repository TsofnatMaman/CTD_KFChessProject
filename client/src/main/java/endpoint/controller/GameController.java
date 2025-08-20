package endpoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.*;
import endpoint.launch.ChessClientEndpoint;
import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import interfaces.IGame;
import pieces.Position;
import utils.LogUtils;
import utils.Utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Controller class managing game state, player interactions, and communication with the server.
 */
public class GameController implements Runnable, IEventListener {

    private IGame model;
    private IGameUI gamePanel;
    private int playerId = -1;

    private final ChessClientEndpoint client;
    private final ObjectMapper mapper;
    private final PlayerActionHandler playerActionHandler;
    private final ServerMessageHandler serverMessageHandler;
    private final List<GameEventListener> listeners = new CopyOnWriteArrayList<>();
    private Thread listenerThread;

    public GameController(ChessClientEndpoint client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
        this.playerActionHandler = new PlayerActionHandler(this);
        this.serverMessageHandler = new ServerMessageHandler(this);

        EventPublisher.getInstance().subscribe(EGameEvent.GAME_ENDED, this);
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_UPDATE, this);
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_END_MOVED, this);
    }

    // ------------------- Threads -------------------

    public void startListening() {
        if (listenerThread == null || !listenerThread.isAlive()) {
            listenerThread = new Thread(this, "GameController-Listener");
            listenerThread.setDaemon(true);
            listenerThread.start();
            LogUtils.logDebug("GameController started listening thread");
        }
    }

    public void stopListening() {
        if (listenerThread != null) {
            listenerThread.interrupt();
            try { listenerThread.join(2000); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            LogUtils.logDebug("GameController stopped listening thread");
        }
    }

    public void startRunGame() {
        if (model != null && !Thread.currentThread().isInterrupted()) {
            new Thread(() -> model.run(), "Game-Loop-Thread").start();
        }
    }

    @Override
    public void run() {
        LogUtils.logDebug("GameController run() loop started");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String message = client.pollNextMessage(500, TimeUnit.MILLISECONDS);
                if (message != null) serverMessageHandler.handleMessage(message);
            } catch (Exception e) {
                LogUtils.logDebug("Error in GameController loop: " + e);
            }
        }
        LogUtils.logDebug("GameController run() loop ended");
    }

    // ------------------- Event Handling -------------------

    @Override
    public void onEvent(GameEvent event) {
        switch (event.type()) {
            case GAME_ENDED -> gamePanel.onWin(model.win());
            case GAME_UPDATE -> {
                gamePanel.onGameUpdate();
                gamePanel.updateTimerLabel(Utils.formatElapsedTime(model.getElapsedMillis()));
            }
            case PIECE_END_MOVED -> playerActionHandler.refreshLegalMoves();
        }
    }

    // ------------------- API & Helper Methods -------------------

    void fireEvent(Consumer<GameEventListener> action) {
        for (GameEventListener l : listeners) {
            try { action.accept(l); } catch (Exception e) { LogUtils.logDebug("Listener error: " + e); }
        }
    }

    public void addListener(GameEventListener listener) { listeners.add(listener); }
    public void removeListener(GameEventListener listener) { listeners.remove(listener); }

    public IGameUI getGamePanel() { return gamePanel; }
    public IGame getModel() { return model; }
    public int getPlayerId() { return playerId; }
    public ObjectMapper getMapper() { return mapper; }

    void onPlayerId(int id) {
        this.playerId = id;
        fireEvent(l -> l.onPlayerId(id));
    }

    void playInit(GameDTO dto) {
        GameHelper helper = new GameHelper(playerId);
        model = helper.createGame(dto);
        gamePanel = helper.createGamePanel(model, playerActionHandler);
        fireEvent(GameEventListener::onGameInit);
    }

    void onPlayerSelect(PlayerSelectedDTO cmd) {
        model.handleSelection(cmd.playerId(), cmd.selection());
    }

    void sendPlayerSelection(Position pos) throws Exception {
        client.sendCommand(EventType.PLAYER_SELECTED, new PlayerSelectedDTO(playerId, pos));
    }

    // ------------------- Listener Interface -------------------

    public interface GameEventListener {
        void onWaitMessage(String message);
        void onPlayerId(int playerId);
        void onUnknownMessage(String type);
        void onGameInit();
    }
}
