package endpoint.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.*;
import endpoint.launch.ChessClientEndpoint;
import endpoint.view.BoardPanel;
import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import game.Game;
import interfaces.IGame;
import interfaces.IPiece;
import interfaces.IPlayer;
import interfaces.IPlayerCursor;
import pieces.Position;
import player.PlayerCursor;
import utils.LogUtils;
import utils.Utils;
import viewUtils.GamePanel;
import viewUtils.PlayerInfoPanel;

import java.awt.*;
import java.util.Arrays;
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
    private final List<GameEventListener> listeners = new CopyOnWriteArrayList<>();
    private Thread listenerThread;

    private Position selected = null;
    private ClientState clientState;

    private volatile boolean running = true;

    public GameController(ChessClientEndpoint client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
        clientState = ClientState.WAIT_SELECTING_PIECE;

        EventPublisher.getInstance().subscribe(EGameEvent.GAME_ENDED, this);
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_UPDATE, this);
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_END_MOVED, this);
    }

    /** Starts the listener thread for server messages. */
    public void startListening() {
        if (listenerThread == null || !listenerThread.isAlive()) {
            listenerThread = new Thread(this, "GameController-Listener");
            listenerThread.setDaemon(true);
            running = true;
            listenerThread.start();
            LogUtils.logDebug("GameController started listening thread");
        }
    }

    /** Stops the listener thread. */
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

    /** Adds a GameEventListener. */
    public void addListener(GameEventListener listener) {
        listeners.add(listener);
        LogUtils.logDebug("Added listener: " + listener);
    }

    /** Removes a GameEventListener. */
    public void removeListener(GameEventListener listener) {
        listeners.remove(listener);
        LogUtils.logDebug("Removed listener: " + listener);
    }

    /** Handles player selection of a position on the board. */
    private void handlePlayerSelection(Position pos) {
        IPiece p = model.getBoard().getPiece(pos);
        BoardPanel boardPanel = (BoardPanel) gamePanel.getBoardPanel();

        switch (clientState) {
            case WAIT_SELECTING_PIECE -> {
                if (p == null || p.isCaptured() || p.getPlayer() != playerId || !p.canAction()) return;
                boardPanel.setSelected(pos.copy());
                boardPanel.setLegalMoves(model.getBoard().getLegalMoves(pos));
                boardPanel.repaint();
                selected = pos.copy();
                clientState = ClientState.WAIT_SELECTING_TARGET;
            }

            case WAIT_SELECTING_TARGET -> {
                boardPanel.clearSelection();
                selected = null;
                clientState = ClientState.WAIT_SELECTING_PIECE;
            }
        }

        try {
            sendPlayerSelection(pos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Creates a PlayerInfoPanel for the given player. */
    private PlayerInfoPanel createPlayerInfoPanel(IPlayer player) {
        PlayerInfoPanel panel = new PlayerInfoPanel(player);
        panel.setBackground(new Color(255, 255, 255, 180));
        return panel;
    }

    /** Sends a player selection command to the server. */
    private void sendPlayerSelection(Position pos) throws Exception {
        client.sendCommand(EventType.PLAYER_SELECTED, new PlayerSelectedDTO(playerId, pos));
    }

    /** Sends a generic message to the server. */
    private <T> void sendMessage(EventType type, T data) {
        try {
            Message<T> msg = new Message<>(type, data);
            client.sendText(mapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            LogUtils.logDebug("Failed to send message: " + e.getMessage());
        }
    }

    /** Main run loop for polling server messages. */
    @Override
    public void run() {
        LogUtils.logDebug("GameController run() loop started");

        while (running) {
            try {
                String message = client.pollNextMessage(500, TimeUnit.MILLISECONDS);
                if (message != null) handleMessage(message);
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

    private volatile boolean gameLoopStarted = false;

    /** Starts the main game loop thread if not already started. */
    public void startRunGame() {
        if (!gameLoopStarted && model != null) {
            gameLoopStarted = true;
            new Thread(() -> model.run(), "Game-Loop-Thread").start();
        }
    }

    /** Handles a server message as JSON. */
    private void handleMessage(String message) {
        try {
            JsonNode root = mapper.readTree(message);
            String typeStr = root.path("type").asText("");
            JsonNode dataNode = root.path("data");

            EventType type = EventType.UNKNOWN;
            try { type = EventType.valueOf(typeStr); }
            catch (IllegalArgumentException ignored) {}

            switch (type) {
                case WAIT -> fireEvent(l -> l.onWaitMessage(dataNode.asText("")));
                case GAME_INIT -> {
                    GameDTO dto = mapper.treeToValue(dataNode, GameDTO.class);
                    playInit(dto);
                }
                case PLAYER_SELECTED -> {
                    PlayerSelectedDTO cmd = mapper.treeToValue(dataNode, PlayerSelectedDTO.class);
                    onPlayerSelect(cmd);
                }
                case PLAYER_ID -> onPlayerId(dataNode.asInt(-1));
                default -> fireEvent(l -> l.onUnknownMessage(typeStr));
            }
        } catch (JsonProcessingException e) {
            LogUtils.logDebug("Failed to parse JSON message: " + e.getMessage());
        }
    }

    /** Updates local player ID and notifies listeners. */
    private void onPlayerId(int id) {
        this.playerId = id;
        fireEvent(l -> l.onPlayerId(id));
    }

    /** Initializes the game model and UI panel from GameDTO. */
    private void playInit(GameDTO dto) {
        IPlayer[] players = Arrays.stream(dto.getPlayers())
                .map(p -> PlayerDTO.to(p, dto.getBoardConfig()))
                .toArray(IPlayer[]::new);

        model = new Game(dto.getBoardConfig(), players);

        IPlayerCursor cursor = new PlayerCursor(
                new Position(0,0),
                model.getPlayerById(playerId).getColor()
        );

        BoardPanel boardPanel = new BoardPanel(model.getBoard(), cursor);
        boardPanel.setOnPlayerAction(this::handlePlayerSelection);

        List<PlayerInfoPanel> pips = Arrays.stream(model.getPlayers()).map(this::createPlayerInfoPanel).toList();

        this.gamePanel = new GamePanel(boardPanel, pips);

        fireEvent(GameEventListener::onGameInit);
    }

    /** Handles a player selection received from the server. */
    private void onPlayerSelect(PlayerSelectedDTO cmd) {
        model.handleSelection(cmd.playerId(), cmd.selection());
    }

    /** Invokes the given action for all registered listeners. */
    private void fireEvent(Consumer<GameEventListener> action) {
        for (GameEventListener l : listeners) {
            try {
                action.accept(l);
            } catch (Exception e) {
                LogUtils.logDebug("Listener error: " + e);
            }
        }
    }

    /** Handles events from EventPublisher. */
    @Override
    public void onEvent(GameEvent event) {
        switch (event.type()) {
            case GAME_ENDED -> gamePanel.onWin(model.win());
            case GAME_UPDATE -> {
                gamePanel.onGameUpdate();
                gamePanel.updateTimerLabel(Utils.formatElapsedTime(model.getElapsedMillis()));
            }
            case PIECE_END_MOVED -> {
                if (selected != null)
                    ((BoardPanel)gamePanel.getBoardPanel()).setLegalMoves(model.getBoard().getLegalMoves(selected));
            }
        }
    }

    public IGameUI getGamePanel() {
        return gamePanel;
    }

    public IGame getModel() {
        return model;
    }

    /** Listener interface for external GameController events. */
    public interface GameEventListener {
        void onWaitMessage(String message);
        void onPlayerId(int playerId);
        void onUnknownMessage(String type);
        void onGameInit();
    }
}
