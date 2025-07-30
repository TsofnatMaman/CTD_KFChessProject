package game;

import board.BoardConfig;
import board.Dimension;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import interfaces.IPlayer;
import pieces.Position;
import player.Player;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameController {

    private final Map<Integer, Session> playerSessions = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    private final Game game;

    public GameController() {
        BoardConfig boardConfig = new BoardConfig(new Dimension(8), new Dimension(64*8));
        // יצירת 2 שחקנים מראש עם מזהים פנימיים 0 ו־1 (ע"י המונה בפנים)
        IPlayer p1 = new Player("Player 1", boardConfig);
        IPlayer p2 = new Player("Player 2", boardConfig);

        this.game = new Game(boardConfig, new IPlayer[]{p1, p2});
        this.game.run(); // מפעיל את לולאת המשחק (כמו ב־main)
    }

    public synchronized void addClient(Session session, int playerId) {
        if (playerId >= 2) {
            System.err.println("Maximum 2 players supported.");
            return;
        }
        playerSessions.put(playerId, session);
        // אל תשלח כאן את הדלתא! תן ל־Endpoint לשלוח אחרי קבלת playerId.
    }

    public synchronized void handleMessage(int playerId, Position pos) {
        IPlayer player = game.getPlayerById(playerId);
        if (player == null) return;

        game.handleSelection(player, pos);
        game.update(); // הפעלת הפקודות בתור
    }

    public void removeClient(int playerId) {
        playerSessions.remove(playerId);
    }

    public Game getGame() {
        return game;
    }

}
