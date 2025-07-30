package webSocket.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import game.GameController;
import interfaces.IBoard;
import pieces.Position;
import webSocket.server.dto.GameDTO;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import javax.json.Json;
import javax.json.JsonObject;
import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint("/game")
public class ChessServerEndpoint {

    private static int nextPlayerId = 0;
    private static final Map<Session, Integer> sessionPlayerMap = new ConcurrentHashMap<>();
    private static final GameController controller = new GameController();
    private static final ObjectMapper mapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session) {
        if(nextPlayerId == 2){
            System.out.println("can not support more than 2 players");
            return;
        }
        int assignedId;
        synchronized (ChessServerEndpoint.class) {
            assignedId = nextPlayerId++;
            sessionPlayerMap.put(session, assignedId);
            controller.addClient(session, assignedId);
        }

        System.out.println("Player " + assignedId + " connected.");

        // שולח ID
        try {
            JsonObject json = Json.createObjectBuilder()
                    .add("type", "playerId")
                    .add("id", assignedId)
                    .build();
            StringWriter sw = new StringWriter();
            Json.createWriter(sw).write(json);
            session.getAsyncRemote().sendText(sw.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // שולח את מצב הלוח
        sendBoardToClient(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            Position selection = mapper.readValue(message, Position.class);
            Integer playerId = sessionPlayerMap.get(session);

            if (playerId == null) {
                System.out.println("Unknown session tried to send a message.");
                return;
            }

            controller.handleMessage(playerId, selection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        Integer playerId = sessionPlayerMap.remove(session);
        if (playerId != null) {
            System.out.println("Player " + playerId + " disconnected.");
            // כאן אפשר להוסיף טיפול בלקוח שהתנתק (למשל להסיר אותו מהמשחק)
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    public IBoard getBoard(){
        return controller.getGame().getBoard();
    }

    private void sendBoardToClient(Session session) {
        try {
            // יצירת GameDelta או מבנה דומה שמתאר את מצב הלוח
            // לדוגמה: GameDelta delta = controller.getCurrentGameDelta();

            GameDTO delta = controller.createGameDTO(); // הנחה שיש שיטה כזו

            String json = mapper.writeValueAsString(delta);
            session.getAsyncRemote().sendText(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
