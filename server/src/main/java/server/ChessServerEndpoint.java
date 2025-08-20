package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import constants.ServerConfig;
import dto.Message;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * WebSocket endpoint for chess game.
 * Delegates all logic to GameHandler.
 */
@ServerEndpoint(ServerConfig.SERVER_ENDPOINT)
public class ChessServerEndpoint {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final GameHandler gameHandler = new GameHandler();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        gameHandler.handleOpen(session);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        gameHandler.handleClose(session, reason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        gameHandler.handleError(session, throwable);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        gameHandler.handleMessage(message, session);
    }
}
