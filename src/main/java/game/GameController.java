package game;

import board.BoardConfig;
import board.Dimension;
import interfaces.IGame;
import interfaces.IPlayer;
import pieces.Position;
import player.Player;
import player.PlayerCursor;
import server.client.Client;

import javax.websocket.Session;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GameController {
    private final IGame game;
    private final Map<Integer, Client> clients = new HashMap<>();

    public GameController() {
        BoardConfig boardConfig = new BoardConfig(new Dimension(8), new Dimension(64 * 8));
        IPlayer p1 = new Player("player 1", boardConfig);
        IPlayer p2 = new Player("player 2", boardConfig);
        this.game = new Game(boardConfig, new IPlayer[]{p1, p2});
    }

    public void addClient(Session session, int playerId) {
        Client client = new Client(playerId, session, game);
        clients.put(playerId, client);
    }

    public void handleMessage(int playerId, Position selection) {
        IPlayer player = game.getPlayerById(playerId);
        game.handleSelection(player, selection);
    }

    public void startGameLoop() {
        IBoardView[] views = clients.values().stream()
                .map(Client::getBoardView)
                .toArray(IBoardView[]::new);
        game.run(views);
    }

    public void stopGameLoop() {
        game.stopGameLoop();
    }
}
