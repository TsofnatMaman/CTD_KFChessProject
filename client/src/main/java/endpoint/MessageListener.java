package endpoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageListener extends Thread {

    private static final Logger logger = Logger.getLogger(MessageListener.class.getName());

    private final ChessClientEndpoint client;
    private final ObjectMapper mapper;
    private final KFChessClientApp app;

    private volatile boolean running = true;

    public MessageListener(ChessClientEndpoint client, ObjectMapper mapper, KFChessClientApp app) {
        super("Websocket-Listener-Thread");
        this.client = client;
        this.mapper = mapper;
        this.app = app;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (running) {
                String message = client.waitForNextMessage();
                if (message == null) continue;

                logger.fine("Received message: " + message);

                JsonNode root;
                try {
                    root = mapper.readTree(message);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to parse JSON message", e);
                    continue;
                }

                app.onMessage(root);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Listener thread stopped with error", e);
        }
    }

    public void stopListening() {
        running = false;
        interrupt();
    }
}
