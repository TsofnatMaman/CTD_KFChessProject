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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint("/game")
public class ChessServerEndpoint {

    private static final List<Session> sessions = new CopyOnWriteArrayList<>();
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
        sessions.add(session);
        System.out.println("Client connected: " + session.getId());

        int playerId = sessions.indexOf(session); // מזהה שחקן לפי סדר חיבור (0,1,...)

        try {
            // שולחים מצב התחלתי (gameInit)
            ServerMessage<GameDTO> initMsg = new ServerMessage<GameDTO>("gameInit", initialGameState);
            String jsonInit = mapper.writeValueAsString(initMsg);
            session.getBasicRemote().sendText(jsonInit);

            // שולחים מזהה שחקן (playerId)
            ServerMessage<Integer> idMsg = new ServerMessage<Integer>("playerId", playerId);
            String jsonId = mapper.writeValueAsString(idMsg);
            session.getBasicRemote().sendText(jsonId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message from " + session.getId() + ": " + message);
        try {
            // קוראים את הפקודה מסוג PlayerSelected מהלקוח
            PlayerSelected cmd = mapper.readValue(message, PlayerSelected.class);

            // מעדכנים את המשחק בהתאם לבחירה של השחקן
            game.handleSelection(cmd.getPlayerId(), cmd.getSelection());

            // שולחים עדכון לכל הלקוחות (כולל השולח) כדי לסנכרן את המצב
            ServerMessage<PlayerSelected> update = new ServerMessage<PlayerSelected>("playerSelected", cmd);
            String json = mapper.writeValueAsString(update);

            for (Session s : sessions) {
                s.getAsyncRemote().sendText(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnClose
    public void onClose(Session session, CloseReason reason) {
        sessions.remove(session);
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
