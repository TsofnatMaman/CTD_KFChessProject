package endpoint.launch;

import board.BoardConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import constants.Messages;
import constants.ServerConfig;
import constants.GameConstants;
import dto.*;
import game.Game;
import interfaces.IGame;
import interfaces.IPlayer;
import player.PlayerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint(ServerConfig.SERVER_ENDPOINT)
public class ChessServerEndpoint {

    private static final Logger LOGGER = Logger.getLogger(ChessServerEndpoint.class.getName());
    private static final Map<Session, Integer> sessionPlayerIds = new ConcurrentHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static volatile IGame game = null;

    private static final int MAX_PLAYERS = GameConstants.MAX_PLAYERS;
    private static final List<String> playersName = Collections.synchronizedList(new ArrayList<>(List.of(
            Messages.get(Messages.Key.PLAYER_1_NAME),
            Messages.get(Messages.Key.PLAYER_2_NAME)
    )));

    // ---------------------- Connection Handling ----------------------

    @OnOpen
    public synchronized void onOpen(Session session) throws IOException {
        int playerId = sessionPlayerIds.size();
        if (playerId >= MAX_PLAYERS) {
            session.close(new CloseReason(CloseReason.CloseCodes.TRY_AGAIN_LATER,
                    Messages.get(Messages.Key.GAME_FULL_MESSAGE)));
            return;
        }

        sessionPlayerIds.put(session, playerId);
        logInfo("Client connected: %s, assigned playerId: %d", session.getId(), playerId);

        if (sessionPlayerIds.size() < MAX_PLAYERS) {
            sendMessage(session, new Message<>(EventType.WAIT,
                    Messages.get(Messages.Key.WAIT_MESSAGE)));
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        sessionPlayerIds.remove(session);
        logInfo("Client disconnected: %s, reason: %s", session.getId(), reason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.log(Level.SEVERE,
                String.format("Session error for %s: %s", session != null ? session.getId() : "unknown",
                        throwable.getMessage()), throwable);
    }

    // ---------------------- Message Handling ----------------------

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println(message);
        Integer playerId = sessionPlayerIds.get(session);
        if (playerId == null) return;

        try {
            Message<JsonNode> genericMsg = mapper.readValue(message, new TypeReference<>() {});
            handleMessageByType(genericMsg, session, playerId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to process message", e);
        }
    }

    private void handleMessageByType(Message<JsonNode> msg, Session session, int playerId) throws IOException {
        switch (msg.type()) {
            case PLAYER_SELECTED -> handlePlayerSelected(msg.data(), playerId);
            case SET_NAME -> handleSetName(msg.data(), playerId, session);
            default -> LOGGER.warning("Unknown message type: " + msg.data());
        }
    }

    private void handlePlayerSelected(JsonNode data, int playerId) throws IOException {
        logInfo(data.toString());
        if (game == null) return;

        PlayerSelectedDTO cmd = mapper.treeToValue(data, PlayerSelectedDTO.class);
        if (cmd.playerId() != playerId) {
            LOGGER.severe(Messages.get(Messages.Key.PLAYER_ID_MISMATCH_ERROR, playerId));
            return;
        }

        game.handleSelection(cmd.playerId(), cmd.selection());
        broadcastMessage(new Message<>(EventType.PLAYER_SELECTED, cmd));
    }

    private void handleSetName(JsonNode data, int playerId, Session session) {
        String name = data.asText("");
        playersName.set(playerId, name);
        logInfo("Set name for player %d: %s", playerId, name);

        initializeGameIfReady();
    }

    // ---------------------- Game Initialization ----------------------

    private void initializeGameIfReady() {
        synchronized (ChessServerEndpoint.class) {
            //for one player - commented this line
            if (sessionPlayerIds.size() < MAX_PLAYERS || game != null) return;
            createGame();
            sendInitialGameStateToAll();
        }
    }

    private void createGame() {
        BoardConfig boardConfig = new BoardConfig(
                new Dimension(GameConstants.BOARD_SIZE, GameConstants.BOARD_SIZE),
                new Dimension(GameConstants.SQUARE_SIZE * GameConstants.BOARD_SIZE, GameConstants.SQUARE_SIZE * GameConstants.BOARD_SIZE),
                new Dimension(500,500)
        );

        IPlayer[] players = PlayerFactory.createPlayers(
                new String[]{playersName.get(0), playersName.get(1)},
                boardConfig
        );

        game = Game.getInstance(boardConfig, players);
        game.run();
    }

    private void sendInitialGameStateToAll() {
        GameDTO initialGameState = createInitialGameDTO();
        sessionPlayerIds.forEach((s, id) -> {
            sendMessage(s, new Message<>(EventType.PLAYER_ID, id));
            sendMessage(s, new Message<>(EventType.GAME_INIT, initialGameState));
        });
    }

    private GameDTO createInitialGameDTO() {
        GameDTO gameDTO = new GameDTO();
        gameDTO.setBoardConfig(game.getBoard().getBoardConfig());
        gameDTO.setStartTimeNano(game.getStartTimeNano());
        gameDTO.setPlayers(Arrays.stream(game.getPlayers())
                .map(PlayerDTO::from)
                .toArray(PlayerDTO[]::new));
        return gameDTO;
    }

    // ---------------------- Messaging Helpers ----------------------

    private void sendMessage(Session session, Message<?> message) {
        if (session == null || !session.isOpen()) return;
        try {
            session.getBasicRemote().sendText(mapper.writeValueAsString(message));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to send message to session " + session.getId(), e);
        }
    }

    private void broadcastMessage(Message<?> message) {
        sessionPlayerIds.keySet().forEach(s -> sendMessage(s, message));
    }

    // ---------------------- Logging Helper ----------------------

    private void logInfo(String template, Object... args) {
        LOGGER.info(() -> String.format(template, args));
    }
}