package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import constants.GameConstants;
import constants.Messages;
import dto.*;
import game.Game;
import interfaces.IGame;
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
 * Handles the actual game logic, messaging, and player management.
 * This version avoids static fields, so each GameHandler instance
 * manages its own game and players independently.
 */
public class GameHandler {

    private static final Logger LOGGER = Logger.getLogger(GameHandler.class.getName());
    private final ObjectMapper mapper = new ObjectMapper();

    private final Map<Session, Integer> sessionPlayerIds = new ConcurrentHashMap<>();
    private final List<String> playerNames = Collections.synchronizedList(
            new ArrayList<>(List.of("Player1", "Player2"))
    );
    private IGame game = null;

    // ---------------------- Connection Handling ----------------------

    public synchronized void handleOpen(Session session) throws IOException {
        int playerId = sessionPlayerIds.size();
        if (playerId >= GameConstants.MAX_PLAYERS) {
            session.close(new CloseReason(CloseReason.CloseCodes.TRY_AGAIN_LATER,
                    Messages.get(Messages.Key.GAME_FULL_MESSAGE)));
            return;
        }
        sessionPlayerIds.put(session, playerId);
        logInfo("Client connected: %s, assigned playerId: %d", session.getId(), playerId);

        Messaging.sendMessage(session, new Message<>(EventType.PLAYER_ID, playerId));
        if (sessionPlayerIds.size() < GameConstants.MAX_PLAYERS) {
            Messaging.sendMessage(session, new Message<>(EventType.WAIT, Messages.get(Messages.Key.WAIT_MESSAGE)));
        }
    }

    public void handleClose(Session session, CloseReason reason) {
        sessionPlayerIds.remove(session);
        logInfo("Client disconnected: %s, reason: %s", session.getId(), reason);

        if(sessionPlayerIds.isEmpty()){
            logInfo("Server closed. all players leave");
            System.exit(0);
        }
    }

    public void handleError(Session session, Throwable throwable) {
        LOGGER.log(Level.SEVERE,
                String.format("Session error for %s: %s",
                        session != null ? session.getId() : "unknown",
                        throwable.getMessage()), throwable);
    }

    // ---------------------- Message Handling ----------------------

    public void handleMessage(String message, Session session) {
        Integer playerId = sessionPlayerIds.get(session);
        if (playerId == null) return;

        try {
            Message<JsonNode> genericMsg = mapper.readValue(message,
                    mapper.getTypeFactory().constructParametricType(Message.class, JsonNode.class));
            handleMessageByType(genericMsg, session, playerId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to process message", e);
        }
    }

    private void handleMessageByType(Message<JsonNode> msg, Session session, int playerId) {
        switch (msg.type()) {
            case SET_NAME -> handleSetName(msg.data(), playerId);
            case PLAYER_SELECTED -> handlePlayerSelected(msg.data(), playerId);
            default -> LOGGER.warning("Unknown message type: " + msg.type());
        }
    }

    private void handleSetName(JsonNode data, int playerId) {
        String name = data.asText("");
        playerNames.set(playerId, name);
        logInfo("Set name for player %d: %s", playerId, name);
        initializeGameIfReady();
    }

    private void handlePlayerSelected(JsonNode data, int playerId) {
        if (game == null) return;

        try {
            PlayerSelectedDTO cmd = mapper.treeToValue(data, PlayerSelectedDTO.class);
            if (cmd.playerId() != playerId) {
                LOGGER.severe(Messages.get(Messages.Key.PLAYER_ID_MISMATCH_ERROR, playerId));
                return;
            }
            game.handleSelection(cmd.playerId(), cmd.selection());
            Messaging.broadcastMessage(sessionPlayerIds.keySet(), new Message<>(EventType.PLAYER_SELECTED, cmd));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to handle PLAYER_SELECTED", e);
        }
    }

    // ---------------------- Game Initialization ----------------------

    private void initializeGameIfReady() {
        synchronized (this) {
            if (sessionPlayerIds.size() < GameConstants.MAX_PLAYERS || game != null) return;
            createGame();
            sendInitialGameStateToAll();
        }
    }

    private void createGame() {
        BoardConfig boardConfig = new BoardConfig(
                new Dimension(GameConstants.BOARD_SIZE, GameConstants.BOARD_SIZE),
                new Dimension(GameConstants.SQUARE_SIZE * GameConstants.BOARD_SIZE,
                        GameConstants.SQUARE_SIZE * GameConstants.BOARD_SIZE),
                new Dimension(500, 500)
        );

        IPlayer[] players = PlayerFactory.createPlayers(
                new String[]{playerNames.get(0), playerNames.get(1)},
                boardConfig
        );

        game = new Game(boardConfig, players);
        game.run();
    }

    private void sendInitialGameStateToAll() {
        GameDTO dto = createInitialGameDTO();
        sessionPlayerIds.forEach((s, id) -> {
            Messaging.sendMessage(s, new Message<>(EventType.PLAYER_ID, id));
            Messaging.sendMessage(s, new Message<>(EventType.GAME_INIT, dto));
        });
    }

    private GameDTO createInitialGameDTO() {
        GameDTO dto = new GameDTO();
        dto.setBoardConfig(game.getBoard().getBoardConfig());
        dto.setStartTimeNano(game.getStartTimeNano());
        dto.setPlayers(Arrays.stream(game.getPlayers()).map(PlayerDTO::from).toArray(PlayerDTO[]::new));
        return dto;
    }

    // ---------------------- Logging ----------------------

    private void logInfo(String template, Object... args) {
        LOGGER.info(() -> String.format(template, args));
    }

    // ---------------------- Getters ----------------------

    public IGame getGame() {
        return game;
    }

    public Map<Session, Integer> getSessionPlayerIds() {
        return sessionPlayerIds;
    }
}
