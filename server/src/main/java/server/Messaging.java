package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Message;

import javax.websocket.Session;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles sending messages to sessions.
 */
public class Messaging {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = Logger.getLogger(Messaging.class.getName());

    public static void sendMessage(Session session, Message<?> message) {
        if (session == null || !session.isOpen()) return;
        try {
            session.getBasicRemote().sendText(MAPPER.writeValueAsString(message));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to send message to session " + session.getId(), e);
        }
    }

    public static void broadcastMessage(Set<Session> sessions, Message<?> message) {
        sessions.forEach(s -> sendMessage(s, message));
    }
}
