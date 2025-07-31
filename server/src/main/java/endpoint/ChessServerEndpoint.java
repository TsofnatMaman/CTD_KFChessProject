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
import player.Player;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/game")
public class ChessServerEndpoint {

    private static final Map<Session, Integer> sessionPlayerIds = new ConcurrentHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static IGame game = null; // Not yet created

    private static final int MAX_PLAYERS = 2;

    private static final List<String> playersName = Collections
            .synchronizedList(new ArrayList<>(List.of("player 1", "player 2")));

    @OnOpen
    public synchronized void onOpen(Session session) throws IOException {
        int playerId = sessionPlayerIds.size();
        if (playerId >= MAX_PLAYERS) {
            // Reject additional connections or notify that the game is full
            session.close(new CloseReason(CloseReason.CloseCodes.TRY_AGAIN_LATER, "Game is full"));
            return;
        }

        sessionPlayerIds.put(session, playerId);

        System.out.println("Client connected: " + session.getId() + ", assigned playerId: " + playerId);

        // If all players (2) are now connected
        if (sessionPlayerIds.size() == MAX_PLAYERS) {

        } else {
            // If this is the first player, send a waiting message
            Message<String> waitMsg = new Message<>("wait", "Waiting for second player to join...");
            session.getBasicRemote().sendText(mapper.writeValueAsString(waitMsg));
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message from " + session.getId() + ": " + message);

        Integer playerId = sessionPlayerIds.get(session);
        if (playerId == null) {
            System.err.println("Unknown session, ignoring message");
            return;
        }

        try {
            // Deserialize message envelope generically:
            Message<JsonNode> genericMsg = mapper.readValue(message, mapper.getTypeFactory()
                    .constructParametricType(Message.class, JsonNode.class));

            String type = genericMsg.getType();
            JsonNode dataNode = genericMsg.getData();

            switch (type) {
                case "playerSelected":
                    PlayerSelected cmd = mapper.treeToValue(dataNode, PlayerSelected.class);

                    if (cmd.getPlayerId() != playerId) {
                        System.err.println("Player ID mismatch! Ignoring message from player " + playerId);
                        return;
                    }

                    game.handleSelection(cmd.getPlayerId(), cmd.getSelection());

                    Message<PlayerSelected> update = new Message<>("playerSelected", cmd);
                    String json = mapper.writeValueAsString(update);
                    sendBrodcast(json);
                    break;

                case "setName":
                    String name = dataNode.asText("");
                    playersName.set(playerId, name);
                    System.out.println("Set name for player " + playerId + ": " + name);

                    if (sessionPlayerIds.size() == MAX_PLAYERS) {
                        BoardConfig boardConfig = new BoardConfig(new Dimension(8), new Dimension(64 * 8));
                        IPlayer p1 = new Player(playersName.get(0), boardConfig);
                        IPlayer p2 = new Player(playersName.get(1), boardConfig);
                        game = new Game(boardConfig, new IPlayer[] { p1, p2 });
                        game.run();

                        GameDTO initialGameState = createInitialGameDTO();

                        for (Map.Entry<Session, Integer> entry : sessionPlayerIds.entrySet()) {
                            Session s = entry.getKey();
                            int id = entry.getValue();

                            Message<GameDTO> initMsg = new Message<>("gameInit", initialGameState);
                            s.getBasicRemote().sendText(mapper.writeValueAsString(initMsg));

                            Message<Integer> idMsg = new Message<>("playerId", id);
                            s.getBasicRemote().sendText(mapper.writeValueAsString(idMsg));
                        }
                    }
                    break;

                default:
                    System.err.println("Unknown message type: " + type);
            }

        } catch (Exception e) {
            System.err.println("Failed to process message: " + e.getMessage());
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
        System.out.println("Client disconnected: " + session.getId() + " Reason: " + reason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
        System.err.println("Error on session " + session.getId() + ": " + throwable.getMessage());
    }

    private static GameDTO createInitialGameDTO() {
        GameDTO gameDTO = new GameDTO();
        gameDTO.setBoardConfig(game.getBoard().getBoardConfig());
        gameDTO.setStartTimeNano(game.getStartTimeNano());
        gameDTO.setPlayers((PlayerDTO[]) Arrays.stream(game.getPlayers())
                .map(PlayerDTO::from)
                .toArray(PlayerDTO[]::new));
        return gameDTO;
    }
}
