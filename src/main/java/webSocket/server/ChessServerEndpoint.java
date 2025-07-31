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

    private static final Map<Session, Integer> sessionPlayerIds = new ConcurrentHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static IGame game = null; // עוד לא נוצר

    private static final int MAX_PLAYERS = 2;

    @OnOpen
    public synchronized void onOpen(Session session) throws IOException {
        int playerId = sessionPlayerIds.size();
        if (playerId >= MAX_PLAYERS) {
            // אפשר לדחות חיבורים נוספים או להודיע שהמשחק מלא
            session.close(new CloseReason(CloseReason.CloseCodes.TRY_AGAIN_LATER, "Game is full"));
            return;
        }

        sessionPlayerIds.put(session, playerId);

        System.out.println("Client connected: " + session.getId() + ", assigned playerId: " + playerId);

        // אם התחברו עכשיו כל השחקנים (2)
        if (sessionPlayerIds.size() == MAX_PLAYERS) {
            BoardConfig boardConfig = new BoardConfig(new Dimension(8), new Dimension(64 * 8));
            IPlayer p1 = new Player("player 1", boardConfig);
            IPlayer p2 = new Player("player 2", boardConfig);
            game = new Game(boardConfig, new IPlayer[]{p1, p2});
            game.run();

            GameDTO initialGameState = createInitialGameDTO();

            // שולחים לכל הלקוחות את מצב המשחק ההתחלתי ומזהה השחקן
            for (Map.Entry<Session, Integer> entry : sessionPlayerIds.entrySet()) {
                Session s = entry.getKey();
                int id = entry.getValue();

                ServerMessage<GameDTO> initMsg = new ServerMessage<>("gameInit", initialGameState);
                s.getBasicRemote().sendText(mapper.writeValueAsString(initMsg));

                ServerMessage<Integer> idMsg = new ServerMessage<>("playerId", id);
                s.getBasicRemote().sendText(mapper.writeValueAsString(idMsg));
            }
        } else {
            // אם זה השחקן הראשון, אפשר לשלוח הודעה זמנית או פשוט להמתין
            ServerMessage<String> waitMsg = new ServerMessage<>("wait", "Waiting for second player to join...");
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

            sendBrodcast(json);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void sendBrodcast(String msg){
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
