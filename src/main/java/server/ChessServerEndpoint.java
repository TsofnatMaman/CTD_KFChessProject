package server;

import game.GameController;
import game.Game;
import pieces.Position;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
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
    public void onMessage(Session session, Position selection) {
        Integer client = connections.get(session);
        if (client != null && selection != null) {
            gameController.handleMessage(client, selection);
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
