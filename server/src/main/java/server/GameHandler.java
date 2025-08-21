package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import constants.BoardConstants;
import constants.GameConstants;
import constants.Messages;
import dto.*;
import game.GameFactory;
import game.GameLoop;
import interfaces.IGame;
import interfaces.IGameLoop;
import interfaces.IPlayer;
import player.PlayerFactory;
import board.BoardConfig;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles all core logic related to the multiplayer chess game:
 * <ul>
 *   <li>Manages player sessions and IDs</li>
 *   <li>Processes incoming WebSocket messages</li>
 *   <li>Initializes and starts the game when ready</li>
 *   <li>Delegates game events to {@link IGame}</li>
 * </ul>
 *
 * <p>
 * Each instance of {@code GameHandler} manages its own game state and players.
 * </p>
 */
public class GameHandler {

    private static final Logger LOGGER = Logger.getLogger(GameHandler.class.getName());
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Maps connected WebSocket sessions to player IDs.
     */
    private final Map<Session, Integer> sessionPlayerIds = new ConcurrentHashMap<>();

    /**
     * Stores player names in order (synchronized for thread safety).
     */
    private final List<String> playerNames = Collections.synchronizedList(
            new ArrayList<>(List.of("Player1", "Player2"))
    );

    /**
     * The current game instance (null until both players have joined).
     */
    private IGame game = null;

    // ---------------------- Connection Handling ----------------------

    /**
     * Handles a new WebSocket connection.
     *
     * @param session the new client session
     * @throws IOException if the connection cannot be established
     */
    public synchronized void handleOpen(Session session) throws IOException {
        int playerId = sessionPlayerIds.size();

        // Reject connection if max players reached
        if (playerId >= GameConstants.MAX_PLAYERS) {
            session.close(new CloseReason(
                    CloseReason.CloseCodes.TRY_AGAIN_LATER,
                    Messages.get(Messages.Key.GAME_FULL_MESSAGE))
            );
            return;
        }

        // Assign new player ID to session
        sessionPlayerIds.put(session, playerId);
        logInfo("Client connected: %s, assigned playerId: %d", session.getId(), playerId);

        // Notify client of assigned ID
        Messaging.sendMessage(session, new Message<>(EventType.PLAYER_ID, playerId));

        // If waiting for another player, notify current one
        if (sessionPlayerIds.size() < GameConstants.MAX_PLAYERS) {
            Messaging.sendMessage(session,
                    new Message<>(EventType.WAIT, Messages.get(Messages.Key.WAIT_MESSAGE)));
        }
    }

    /**
     * Handles when a client disconnects.
     *
     * @param session the disconnected session
     * @param reason  the reason for disconnection
     */
    public void handleClose(Session session, CloseReason reason) {
        sessionPlayerIds.remove(session);
        logInfo("Client disconnected: %s, reason: %s", session.getId(), reason);

        // If all players left, shut down server
        if (sessionPlayerIds.isEmpty()) {
            logInfo("Server closed. All players leave.");
            System.exit(0);
        }
    }

    /**
     * Handles errors that occur during a WebSocket session.
     *
     * @param session   the session where the error occurred
     * @param throwable the thrown exception or error
     */
    public void handleError(Session session, Throwable throwable) {
        LOGGER.log(Level.SEVERE,
                String.format("Session error for %s: %s",
                        session != null ? session.getId() : "unknown",
                        throwable.getMessage()), throwable);
    }

    // ---------------------- Message Handling ----------------------

    /**
     * Handles an incoming WebSocket message from a client.
     *
     * @param message the raw message in JSON format
     * @param session the session that sent the message
     */
    public void handleMessage(String message, Session session) {
        Integer playerId = sessionPlayerIds.get(session);
        if (playerId == null) return; // Ignore if session not recognized

        try {
            // Deserialize into generic message with JSON payload
            Message<JsonNode> genericMsg = mapper.readValue(
                    message,
                    mapper.getTypeFactory().constructParametricType(Message.class, JsonNode.class)
            );

            // Route message by type
            handleMessageByType(genericMsg, session, playerId);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Failed to process message", e);
        }
    }

    /**
     * Dispatches a message to the appropriate handler based on its type.
     */
    private void handleMessageByType(Message<JsonNode> msg, Session session, int playerId) {
        switch (msg.type()) {
            case SET_NAME -> handleSetName(msg.data(), playerId);
            case PLAYER_SELECTED -> handlePlayerSelected(msg.data(), playerId);
            default -> LOGGER.warning("Unknown message type: " + msg.type());
        }
    }

    /**
     * Handles a {@link EventType#SET_NAME} message.
     *
     * @param data     JSON node containing the player's chosen name
     * @param playerId the player who sent the message
     */
    private void handleSetName(JsonNode data, int playerId) {
        String name = data.asText("");
        playerNames.set(playerId, name);
        logInfo("Set name for player %d: %s", playerId, name);

        // If all players named, initialize game
        initializeGameIfReady();
    }

    /**
     * Handles a {@link EventType#PLAYER_SELECTED} message.
     *
     * @param data     JSON node containing the selection info
     * @param playerId the player who sent the message
     */
    private void handlePlayerSelected(JsonNode data, int playerId) {
        if (game == null) return;

        try {
            PlayerSelectedDTO cmd = mapper.treeToValue(data, PlayerSelectedDTO.class);

            // Ensure playerId in command matches session playerId
            if (cmd.playerId() != playerId) {
                LOGGER.severe(Messages.get(Messages.Key.PLAYER_ID_MISMATCH_ERROR, playerId));
                return;
            }

            // Delegate selection to game logic
            game.handleSelection(cmd.playerId(), cmd.selection());

            // Broadcast selection event to all players
            Messaging.broadcastMessage(
                    sessionPlayerIds.keySet(),
                    new Message<>(EventType.PLAYER_SELECTED, cmd)
            );
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Failed to handle PLAYER_SELECTED", e);
        }
    }

    // ---------------------- Game Initialization ----------------------

    /**
     * Initializes the game once the required number of players has joined.
     */
    private void initializeGameIfReady() {
        synchronized (this) {
            if (sessionPlayerIds.size() < GameConstants.MAX_PLAYERS || game != null) return;

            createGame();
            sendInitialGameStateToAll();
        }
    }

    /**
     * Creates a new game with board configuration and players.
     */
    private void createGame() {
        BoardConfig boardConfig = new BoardConfig(
                new Dimension(BoardConstants.BOARD_ROWS, BoardConstants.BOARD_COLS),
                new Dimension(BoardConstants.SQUARE_SIZE * BoardConstants.BOARD_ROWS,
                        BoardConstants.SQUARE_SIZE * BoardConstants.BOARD_COLS),
                new Dimension(BoardConstants.BOARD_WIDTH_M, BoardConstants.BOARD_HEIGHT_M) // window size
        );

        // Create players with assigned names
        IPlayer[] players = PlayerFactory.createPlayers(
                new String[]{playerNames.get(0), playerNames.get(1)},
                boardConfig
        );

        // Create game instance
        game = GameFactory.createNewGame(boardConfig, players);

        // Start game loop
        IGameLoop gameLoop = new GameLoop(game);
        gameLoop.run();
    }

    /**
     * Sends the initial game state (board, players, time) to all connected sessions.
     */
    private void sendInitialGameStateToAll() {
        GameDTO dto = createInitialGameDTO();

        sessionPlayerIds.forEach((s, id) -> {
            Messaging.sendMessage(s, new Message<>(EventType.PLAYER_ID, id));
            Messaging.sendMessage(s, new Message<>(EventType.GAME_INIT, dto));
        });
    }

    /**
     * Builds the initial game DTO containing board config, players, and start time.
     */
    private GameDTO createInitialGameDTO() {
        GameDTO dto = new GameDTO();
        dto.setBoardConfig(game.getBoard().getBoardConfig());
        dto.setStartTimeNano(game.getStartTimeNano());
        dto.setPlayers(Arrays.stream(game.getPlayers())
                .map(PlayerDTO::from)
                .toArray(PlayerDTO[]::new));
        return dto;
    }

    // ---------------------- Logging ----------------------

    /**
     * Utility for logging formatted info messages.
     */
    private void logInfo(String template, Object... args) {
        LOGGER.info(() -> String.format(template, args));
    }

    // ---------------------- Getters ----------------------

    /**
     * @return the current game instance, or null if not started
     */
    public IGame getGame() {
        return game;
    }

    /**
     * @return the mapping of sessions to player IDs
     */
    public Map<Session, Integer> getSessionPlayerIds() {
        return sessionPlayerIds;
    }
}
