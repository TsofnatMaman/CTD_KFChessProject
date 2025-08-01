package endpoint;

import board.BoardConfig;
import board.Dimension;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@ServerEndpoint(constants.GameConstants.SERVER_ENDPOINT) // Extracted endpoint to GameConstants
public class ChessServerEndpoint {

    private static final Map<Session, Integer> sessionPlayerIds = new ConcurrentHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static IGame game = null; // Not yet created

    // Use constant from GameConstants
    private static final int MAX_PLAYERS = constants.GameConstants.MAX_PLAYERS;

    // Extracted player names to messages.properties
    private static final List<String> playersName = Collections
            .synchronizedList(new ArrayList<>(List.of(
                utils.ConfigLoader.getMessage("player.1.name", "player 1"),
                utils.ConfigLoader.getMessage("player.2.name", "player 2")
            ))); // extracted player names

    @OnOpen
    public synchronized void onOpen(Session session) throws IOException {
        int playerId = sessionPlayerIds.size();
        if (playerId >= MAX_PLAYERS) {
            // Reject additional connections or notify that the game is full
            session.close(new CloseReason(CloseReason.CloseCodes.TRY_AGAIN_LATER,
                utils.ConfigLoader.getMessage("game.full.message", "Game is full")));
            return;
        }

        sessionPlayerIds.put(session, playerId);

        System.out.println(utils.ConfigLoader.getMessage("client.connected.log", "Client connected: ")
            + session.getId() + utils.ConfigLoader.getMessage("assigned.playerId.log", constants.PieceConstants.POSITION_SEPARATOR + " assigned playerId: ") + playerId); // extracted separator

        // If all players are now connected
        if (sessionPlayerIds.size() < MAX_PLAYERS) {
            Message<String> waitMsg = new Message<>(
                constants.CommandNames.WAIT,
                utils.ConfigLoader.getMessage("wait.message", "Waiting for second player to join...")
            );
            session.getBasicRemote().sendText(mapper.writeValueAsString(waitMsg));
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println(utils.ConfigLoader.getMessage("received.message.log", "Received message from ")
            + session.getId() + ": " + message);

        Integer playerId = sessionPlayerIds.get(session);
        if (playerId == null) {
            System.err.println(utils.ConfigLoader.getMessage("unknown.session.error", "Unknown session, ignoring message"));
            return;
        }

        try {
            // Deserialize message envelope generically:
            Message<JsonNode> genericMsg = mapper.readValue(message, mapper.getTypeFactory()
                    .constructParametricType(Message.class, JsonNode.class));

            String type = genericMsg.getType();
            JsonNode dataNode = genericMsg.getData();

            switch (type) {
                case constants.CommandNames.PLAYER_SELECTED:
                    PlayerSelected cmd = mapper.treeToValue(dataNode, PlayerSelected.class);

                    if (cmd.getPlayerId() != playerId) {
                        System.err.println(utils.ConfigLoader.getMessage("player.id.mismatch.error", "Player ID mismatch! Ignoring message from player ") + playerId);
                        return;
                    }

                    game.handleSelection(cmd.getPlayerId(), cmd.getSelection());

                    Message<PlayerSelected> update = new Message<>(constants.CommandNames.PLAYER_SELECTED, cmd);
                    String json = mapper.writeValueAsString(update);
                    sendBrodcast(json);
                    break;

                case constants.CommandNames.SET_NAME:
                    String name = dataNode.asText("");
                    playersName.set(playerId, name);
                    System.out.println(utils.ConfigLoader.getMessage("set.name.log", "Set name for player ") + playerId + ": " + name);

                    if (sessionPlayerIds.size() == MAX_PLAYERS) {
                        BoardConfig boardConfig = new BoardConfig(
                            new Dimension(constants.GameConstants.BOARD_SIZE),
                            new Dimension(constants.GameConstants.SQUARE_SIZE * constants.GameConstants.BOARD_SIZE)
                        ); // extracted board size and square size

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

                            Message<GameDTO> initMsg = new Message<>(constants.CommandNames.GAME_INIT, initialGameState);
                            s.getBasicRemote().sendText(mapper.writeValueAsString(initMsg));

                            Message<Integer> idMsg = new Message<>(constants.CommandNames.PLAYER_ID, id);
                            s.getBasicRemote().sendText(mapper.writeValueAsString(idMsg));
                        }
                    }
                    break;

                default:
                    System.err.println(utils.ConfigLoader.getMessage("unknown.message.type.error", "Unknown message type: ") + type);
            }

        } catch (Exception e) {
            System.err.println(utils.ConfigLoader.getMessage("process.message.error", "Failed to process message: ") + e.getMessage());
            e.printStackTrace();
        }
    }


    public void sendBrodcast(String msg) {
        for (Session s : sessionPlayerIds.keySet()) {
            s.getAsyncRemote().sendText(msg);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        sessionPlayerIds.remove(session);
        System.out.println(utils.ConfigLoader.getMessage("client.disconnected.log", "Client disconnected: ")
            + session.getId() + utils.ConfigLoader.getMessage("reason.log", " Reason: ") + reason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
        System.err.println(utils.ConfigLoader.getMessage("session.error.log", "Error on session ")
            + session.getId() + ": " + throwable.getMessage());
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
