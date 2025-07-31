package webSocket.server;

import board.BoardConfig;
import board.Dimension;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.Game;
import interfaces.IGame;
import interfaces.IPlayer;
import player.Player;
import webSocket.server.dto.GameDTO;
import webSocket.server.dto.PlayerDTO;
import webSocket.server.dto.PlayerSelected;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/game")
public class ChessServerEndpoint {

    // Map שממפה Session ל-playerId שלו
    private static final Map<Session, Integer> sessionPlayerIds = new ConcurrentHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final IGame game;
    private static final GameDTO initialGameState;

    static {
        BoardConfig boardConfig = new BoardConfig(new Dimension(8), new Dimension(64 * 8));
        IPlayer p1 = new Player("player 1", boardConfig);
        IPlayer p2 = new Player("player 2", boardConfig);
        game = new Game(boardConfig, new IPlayer[]{p1, p2});

        initialGameState = createInitialGameDTO();
    }

    @OnOpen
    public void onOpen(Session session) {
        int playerId = sessionPlayerIds.size(); // מזהה שחקן לפי סדר חיבור (0,1,...)
        sessionPlayerIds.put(session, playerId);

        System.out.println("Client connected: " + session.getId() + ", assigned playerId: " + playerId);

        try {
            // שולחים מצב התחלתי (gameInit)
            ServerMessage<GameDTO> initMsg = new ServerMessage<>("gameInit", initialGameState);
            String jsonInit = mapper.writeValueAsString(initMsg);
            session.getBasicRemote().sendText(jsonInit);

            // שולחים מזהה שחקן (playerId)
            ServerMessage<Integer> idMsg = new ServerMessage<>("playerId", playerId);
            String jsonId = mapper.writeValueAsString(idMsg);
            session.getBasicRemote().sendText(jsonId);

        } catch (IOException e) {
            e.printStackTrace();
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
            // קוראים את הפקודה מסוג PlayerSelected מהלקוח
            PlayerSelected cmd = mapper.readValue(message, PlayerSelected.class);

            // בדיקה שה-playerId בפקודה תואם ל-session
            if (cmd.getPlayerId() != playerId) {
                System.err.println("Player ID mismatch! Ignoring message from player " + playerId);
                return;
            }

            // מעדכנים את המשחק בהתאם לבחירה של השחקן
            game.handleSelection(cmd.getPlayerId(), cmd.getSelection());

            // שולחים עדכון לכל הלקוחות (כולל השולח) כדי לסנכרן את המצב
            ServerMessage<PlayerSelected> update = new ServerMessage<>("playerSelected", cmd);
            String json = mapper.writeValueAsString(update);

            for (Session s : sessionPlayerIds.keySet()) {
                s.getAsyncRemote().sendText(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        sessionPlayerIds.remove(session);
        System.out.println("Client disconnected: " + session.getId() + " Reason: " + reason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error on session " + session.getId() + ": " + throwable.getMessage());
    }

    private static GameDTO createInitialGameDTO() {
        GameDTO gameDTO = new GameDTO();
        gameDTO.setBoardConfig(game.getBoard().getBoardConfig());
        gameDTO.setPlayers((PlayerDTO[]) Arrays.stream(game.getPlayers())
                .map(PlayerDTO::from)
                .toArray(PlayerDTO[]::new));
        return gameDTO;
    }
}
