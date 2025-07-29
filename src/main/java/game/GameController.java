package game;

import board.BoardConfig;
import board.Dimension;
import interfaces.IGame;
import interfaces.IPlayer;
import pieces.Position;
import player.Player;
import player.PlayerCursor;

import javax.websocket.Session;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GameController {
    private final IGame game;
    private final Map<Integer, Session> clients = new HashMap<>();


    public GameController() {
        BoardConfig boardConfig = new BoardConfig(new Dimension(8), new Dimension(64 * 8));

        IPlayer p1 = new Player("player 1", new PlayerCursor(new Position(0, 0), Color.RED), boardConfig);
        IPlayer p2 = new Player("player 2", new PlayerCursor(new Position(7, 7), Color.BLUE), boardConfig);

        this.game = new Game(boardConfig, new IPlayer[]{p1, p2});

        //TODO:run???
    }

    public void addClient(Session session, int playerId) {
        clients.put(playerId, session);
    }

    public void handleMessage(int playerId, Position selection) {
        // תרגום של ההודעה לפעולה במשחק, למשל בחירת כלי או הזזה
        IPlayer player = game.getPlayerById(playerId); // ← מזהה לפי ID
        game.handleSelection(player, selection);

        String gameStateJson = game.toJson(); // תצטרך לממש את זה ב-Game
        broadcast(gameStateJson);
    }

    public void broadcast(String message) {
        for (Session session : clients.values()) {
            session.getAsyncRemote().sendText(message);
        }
    }

}
