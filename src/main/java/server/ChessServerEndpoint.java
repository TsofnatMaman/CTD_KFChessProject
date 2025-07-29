package server;

import game.GameController;
import game.Game;
import interfaces.ICommand;
import interfaces.IPlayer;
import pieces.Position;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/chess")
public class ChessServerEndpoint {

    private static final GameController gameController = new GameController();
    private static final ConcurrentHashMap<Session, Integer> connections = new ConcurrentHashMap<>();

    private static int mone=0;

    @OnOpen
    public void onOpen(Session session) throws IOException {
        if (mone >= 2) {
            session.close(new CloseReason(CloseReason.CloseCodes.TRY_AGAIN_LATER, "Only two players allowed."));
            return;
        }

        int playerId = mone++;
        connections.put(session, playerId);
        gameController.addClient(session, playerId);
        System.out.println("Client connected: " + session.getId());
    }

    @OnMessage
    public void onMessage(Session session, String msg) {
        JsonObject json = Json.createReader(new StringReader(msg)).readObject();
        String type = json.getString("type");

        if ("selection".equals(type)) {
            Position pos = Position.fromString(json.getString("position"));
            int playerId = json.getInt("playerId");

            gameController.handleMessage(playerId, pos);
        }
    }

    @OnClose
    public void onClose(Session session) {
//        Integer playerId = connections.remove(session);
//        if (playerId != null) {
//            gameController.removeClient(playerId);
//            System.out.println("Client " + playerId + " disconnected: " + session.getId());
//        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error on session " + session.getId() + ": " + throwable.getMessage());
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
