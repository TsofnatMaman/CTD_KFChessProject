package server;

import constants.ServerConfig;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * WebSocket endpoint for the chess game server.
 * <p>
 * This class acts as a thin layer between WebSocket events and the
 * {@link GameHandler}, delegating all connection, messaging, and error-handling
 * logic to it.
 * </p>
 */
@ServerEndpoint(ServerConfig.SERVER_ENDPOINT)
public class ChessServerEndpoint {

    /** Shared game handler instance responsible for processing all events. */
    private static final GameHandler gameHandler = new GameHandler();

    /**
     * Triggered when a new WebSocket connection is established.
     *
     * @param session the WebSocket session representing the connection.
     * @throws IOException if an error occurs while handling the open event.
     */
    @OnOpen
    public void onOpen(Session session) throws IOException {
        gameHandler.handleOpen(session);
    }

    /**
     * Triggered when a WebSocket connection is closed.
     *
     * @param session the WebSocket session being closed.
     * @param reason  the reason for the connection closure.
     */
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        gameHandler.handleClose(session, reason);
    }

    /**
     * Triggered when an error occurs during a WebSocket interaction.
     *
     * @param session   the WebSocket session where the error occurred.
     * @param throwable the exception or error that was thrown.
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        gameHandler.handleError(session, throwable);
    }

    /**
     * Triggered when a message is received from a WebSocket client.
     *
     * @param message the text message received from the client.
     * @param session the session from which the message originated.
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        gameHandler.handleMessage(message, session);
    }
}
