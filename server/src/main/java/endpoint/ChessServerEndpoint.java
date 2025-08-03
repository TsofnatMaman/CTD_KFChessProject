package endpoint;

import board.BoardConfig;
import board.Dimension;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import constants.Messages;
import constants.ServerConfig;
import dto.GameDTO;
import dto.Message;
import dto.PlayerDTO;
import dto.PlayerSelected;
import game.Game;
import interfaces.IGame;
import interfaces.IPlayer;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint(ServerConfig.SERVER_ENDPOINT)
public class ChessServerEndpoint {

    private static final Logger LOGGER = Logger.getLogger(ChessServerEndpoint.class.getName());

    private static final Map<Session, Integer> sessionPlayerIds = new ConcurrentHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static volatile IGame game = null;

    private static final int MAX_PLAYERS = constants.GameConstants.MAX_PLAYERS;

    private static final List<String> playersName = Collections.synchronizedList(new ArrayList<>(List.of(
            Messages.get(Messages.Key.PLAYER_1_NAME),
            Messages.get(Messages.Key.PLAYER_2_NAME)
    )));

    @OnOpen
    public synchronized void onOpen(Session session) throws IOException {
        int playerId = sessionPlayerIds.size();
        if (playerId >= MAX_PLAYERS) {
            session.close(new CloseReason(CloseReason.CloseCodes.TRY_AGAIN_LATER,
                    Messages.get(Messages.Key.GAME_FULL_MESSAGE)));
            return;
        }

        sessionPlayerIds.put(session, playerId);

        LOGGER.info(() -> Messages.get(Messages.Key.CLIENT_CONNECTED_LOG)
                + session.getId()
                + Messages.get(Messages.Key.ASSIGNED_PLAYERID_LOG)
                + playerId
        );

        if (sessionPlayerIds.size() < MAX_PLAYERS) {
            Message<String> waitMsg = new Message<>(
                    constants.CommandNames.WAIT,
                    Messages.get(Messages.Key.WAIT_MESSAGE)
            );
            sendMessage(session, waitMsg);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        LOGGER.info(() -> Messages.get(Messages.Key.RECEIVED_MESSAGE_LOG)
                + session.getId() + ": " + message);

        Integer playerId = sessionPlayerIds.get(session);
        if (playerId == null) {
            LOGGER.severe(Messages.get(Messages.Key.UNKNOWN_SESSION_ERROR));
            return;
        }

        try {
            Message<JsonNode> genericMsg = mapper.readValue(message, mapper.getTypeFactory()
                    .constructParametricType(Message.class, JsonNode.class));

            String type = genericMsg.getType();
            JsonNode dataNode = genericMsg.getData();

            switch (type) {
                case constants.CommandNames.PLAYER_SELECTED:
                    if (game == null) {
                        LOGGER.warning("Game not initialized yet; ignoring selection from player " + playerId);
                        return;
                    }

                    PlayerSelected cmd = mapper.treeToValue(dataNode, PlayerSelected.class);

                    if (cmd.getPlayerId() != playerId) {
                        LOGGER.severe(Messages.get(Messages.Key.PLAYER_ID_MISMATCH_ERROR, playerId));
                        return;
                    }

                    game.handleSelection(cmd.getPlayerId(), cmd.getSelection());

                    broadcastMessage(new Message<>(constants.CommandNames.PLAYER_SELECTED, cmd));
                    break;

                case constants.CommandNames.SET_NAME:
                    String name = dataNode.asText("");
                    playersName.set(playerId, name);
                    LOGGER.info(Messages.get(Messages.Key.SET_NAME_LOG, playerId, name));

                    initializeGameIfReady();
                    break;

                default:
                    LOGGER.severe(Messages.get(Messages.Key.UNKNOWN_MESSAGE_TYPE_ERROR, type));
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                    Messages.get(Messages.Key.PROCESS_MESSAGE_ERROR, e.getMessage()),
                    e);
        }
    }

    private void initializeGameIfReady() {
        synchronized (ChessServerEndpoint.class) {
            if (sessionPlayerIds.size() == MAX_PLAYERS && game == null) {
                BoardConfig boardConfig = new BoardConfig(
                        new Dimension(constants.GameConstants.BOARD_SIZE),
                        new Dimension(constants.GameConstants.SQUARE_SIZE * constants.GameConstants.BOARD_SIZE)
                );

                IPlayer[] players = player.PlayerFactory.createPlayers(
                        new String[]{playersName.get(0), playersName.get(1)},
                        boardConfig
                );

                game = new Game(boardConfig, players);
                game.run();

                GameDTO initialGameState = createInitialGameDTO();

                for (Map.Entry<Session, Integer> entry : sessionPlayerIds.entrySet()) {
                    Session s = entry.getKey();
                    int id = entry.getValue();

                    sendMessage(s, new Message<>(constants.CommandNames.GAME_INIT, initialGameState));
                    sendMessage(s, new Message<>(constants.CommandNames.PLAYER_ID, id));
                }
            }
        }
    }

    private void sendMessage(Session session, Message<?> message) {
        if (session != null && session.isOpen()) {
            try {
                String json = mapper.writeValueAsString(message);
                session.getBasicRemote().sendText(json);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to send message to session " + session.getId(), e);
            }
        } else {
            LOGGER.warning("Cannot send message; session is null or closed.");
        }
    }

    private void broadcastMessage(Message<?> message) {
        sessionPlayerIds.keySet().forEach(s -> sendMessage(s, message));
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        sessionPlayerIds.remove(session);
        LOGGER.info(() -> Messages.get(Messages.Key.CLIENT_DISCONNECTED_LOG)
                + session.getId()
                + Messages.get(Messages.Key.REASON_LOG)
                + reason
        );
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.log(Level.SEVERE,
                Messages.get(Messages.Key.SESSION_ERROR_LOG, session != null ? session.getId() : "unknown", throwable.getMessage()),
                throwable);
    }

    private static GameDTO createInitialGameDTO() {
        GameDTO gameDTO = new GameDTO();
        gameDTO.setBoardConfig(game.getBoard().getBoardConfig());
        gameDTO.setStartTimeNano(game.getStartTimeNano());
        gameDTO.setPlayers(Arrays.stream(game.getPlayers())
                .map(PlayerDTO::from)
                .toArray(PlayerDTO[]::new));
        return gameDTO;
    }
}
