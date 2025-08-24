package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Message;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class responsible for sending messages to WebSocket clients.
 * <p>
 * Provides methods for sending a single message to one session
 * or broadcasting the same message to multiple sessions.
 * </p>
 */
public class Messaging {

    /**
     * Shared Jackson mapper for serializing messages into JSON.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Logger for reporting errors or warnings during message sending.
     */
    private static final Logger LOGGER = Logger.getLogger(Messaging.class.getName());

    /**
     * Sends a message to a single WebSocket session.
     *
     * @param session the WebSocket session to send the message to
     * @param message the message object to send (will be serialized to JSON)
     */
    public static void sendMessage(Session session, Message<?> message) {
        if (session == null || !session.isOpen()) return;

        try {
            // Serialize message to JSON and send to client
            session.getBasicRemote().sendText(MAPPER.writeValueAsString(message));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING,
                    "Failed to send message to session " + session.getId(), e);
        }
    }

    /**
     * Broadcasts a message to all connected sessions.
     *
     * @param sessions the set of sessions to send the message to
     * @param message  the message object to broadcast
     */
    public static void broadcastMessage(Set<Session> sessions, Message<?> message) {
        sessions.forEach(s -> sendMessage(s, message));
    }
}
