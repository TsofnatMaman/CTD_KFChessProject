package endpoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.*;
import endpoint.launch.ChessClientEndpoint;
import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import game.GameLoop;
import interfaces.AppLogger;
import interfaces.IGame;
import interfaces.IGameLoop;
import pieces.Position;
import sound.EventSoundListener;
import utils.Slf4jAdapter;
import utils.Utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Controller class managing game state, player interactions, and communication with the server.
 * <p>
 * Responsible for handling events from the server, updating the UI, managing player actions,
 * and running the game loop.
 * </p>
 */
public class GameController implements Runnable, IEventListener {

    private static final AppLogger logger = new Slf4jAdapter(GameController.class);

    /**
     * The game model representing the current state of the game
     */
    private IGame model;

    /**
     * The UI panel for displaying the game
     */
    private IGameUI gamePanel;

    /**
     * The ID of the current player
     */
    private int playerId = -1;

    /**
     * The client endpoint for server communication
     */
    private final ChessClientEndpoint client;

    /**
     * ObjectMapper for JSON serialization/deserialization
     */
    private final ObjectMapper mapper;

    /**
     * Handler for processing player actions
     */
    private final PlayerActionHandler playerActionHandler;

    /**
     * Handler for processing server messages
     */
    private final ServerMessageHandler serverMessageHandler;

    /**
     * List of listeners for game events
     */
    private final List<GameEventListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * Thread for listening to server messages
     */
    private Thread listenerThread;

    /**
     * Constructs a GameController with the specified client and ObjectMapper.
     *
     * @param client the client endpoint for communication
     * @param mapper the JSON mapper
     */
    public GameController(ChessClientEndpoint client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
        this.playerActionHandler = new PlayerActionHandler(this);
        this.serverMessageHandler = new ServerMessageHandler(this);

        // Subscribe to relevant game events
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_ENDED, this);
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_UPDATE, this);
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_END_MOVED, this);

        // Initialize sound listener
        new EventSoundListener();
    }

    // ------------------- Threads -------------------

    /**
     * Starts the listener thread that polls messages from the server.
     */
    public void startListening() {
        if (listenerThread == null || !listenerThread.isAlive()) {
            listenerThread = new Thread(this, "GameController-Listener");
            listenerThread.setDaemon(true);
            listenerThread.start();
            logger.debug("GameController started listening thread");
        }
    }

    /**
     * Stops the listener thread gracefully.
     */
    public void stopListening() {
        if (listenerThread != null) {
            listenerThread.interrupt();
            try {
                listenerThread.join(2000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            logger.debug("GameController stopped listening thread");
        }
    }

    /**
     * Starts the main game loop in a separate thread.
     */
    public void startRunGame() {
        if (model != null && !Thread.currentThread().isInterrupted()) {
            IGameLoop gameLoop = new GameLoop(model);
            new Thread(gameLoop, "Game-Loop-Thread").start();
        }
    }

    /**
     * Main loop that polls messages from the server and delegates them to the message handler.
     */
    @Override
    public void run() {
        logger.debug("GameController run() loop started");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String message = client.pollNextMessage(500, TimeUnit.MILLISECONDS);
                if (message != null) serverMessageHandler.handleMessage(message);
            } catch (InterruptedException e) {
                logger.error("Error in GameController loop", e);
            }
        }
        logger.debug("GameController run() loop ended");
    }

    // ------------------- Event Handling -------------------

    /**
     * Handles incoming game events and updates the UI or internal state accordingly.
     *
     * @param event the game event
     */
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

    /**
     * Fires a given action to all registered listeners.
     *
     * @param action the action to perform on each listener
     */
    void fireEvent(Consumer<GameEventListener> action) {
        for (GameEventListener l : listeners) {
            try {
                action.accept(l);
            } catch (Exception e) {
                logger.error("Listener error: ", e);
            }
        }
    }

    /**
     * Adds a listener for game events
     */
    public void addListener(GameEventListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener for game events
     */
    public void removeListener(GameEventListener listener) {
        listeners.remove(listener);
    }

    public IGameUI getGamePanel() {
        return gamePanel;
    }

    public IGame getModel() {
        return model;
    }

    public int getPlayerId() {
        return playerId;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * Sets the player ID and notifies listeners.
     *
     * @param id the player ID
     */
    void onPlayerId(int id) {
        this.playerId = id;
        fireEvent(l -> l.onPlayerId(id));
    }

    /**
     * Initializes the game using a GameDTO object and notifies listeners.
     *
     * @param dto the game DTO
     */
    void playInit(GameDTO dto) {
        GameHelper helper = new GameHelper(playerId);
        model = helper.createGame(dto);
        gamePanel = helper.createGamePanel(model, playerActionHandler);
        fireEvent(GameEventListener::onGameInit);
    }

    /**
     * Handles a player selection command.
     *
     * @param cmd the player selection DTO
     */
    void onPlayerSelect(PlayerSelectedDTO cmd) {
        model.handleSelection(cmd.playerId(), cmd.selection());
    }

    /**
     * Sends the player's selection to the server.
     *
     * @param pos the selected position
     * @throws Exception if sending fails
     */
    void sendPlayerSelection(Position pos) throws Exception {
        client.sendCommand(EventType.PLAYER_SELECTED, new PlayerSelectedDTO(playerId, pos));
    }

    // ------------------- Listener Interface -------------------

    /**
     * Interface for game event listeners.
     */
    public interface GameEventListener {

        /**
         * Called when a message needs to be displayed to the player
         */
        void onWaitMessage(String message);

        /**
         * Called when the player ID is assigned
         */
        void onPlayerId(int playerId);

        /**
         * Called when an unknown message type is received
         */
        void onUnknownMessage(String type);

        /**
         * Called when the game is initialized
         */
        void onGameInit();
    }
}
