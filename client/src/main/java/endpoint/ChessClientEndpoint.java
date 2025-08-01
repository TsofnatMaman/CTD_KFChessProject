package endpoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;
import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@ClientEndpoint
public class ChessClientEndpoint {

    private Session session;
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private volatile int playerId = -1;

    public ChessClientEndpoint(URI endpointURI) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, endpointURI);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println(utils.ConfigLoader.getMessage("client.connected.log", "Connected to server")); // replaced string with messages.properties key
    }

    private final ObjectMapper mapper = new ObjectMapper();

    @OnMessage
    public void onMessage(String message) {
        messageQueue.add(message);

        try {
            JsonNode root = mapper.readTree(message);
            if (root.has("type") && root.get("type").asText().equals(constants.CommandNames.PLAYER_ID)) { // replaced "playerId" with constant
                playerId = root.get("data").asInt();
                System.out.println(utils.ConfigLoader.getMessage("client.connected.log", "Updated playerId to ") + playerId); // replaced string with messages.properties key
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println(utils.ConfigLoader.getMessage("client.disconnected.log", "Connection closed: ") + reason); // replaced string with messages.properties key
    }

    public int getPlayerId() {
        return playerId;
    }

    public void sendText(String message) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        } else {
            throw new IllegalStateException("WebSocket session is not open");
        }
    }

    public String waitForNextMessage() throws InterruptedException {
        return messageQueue.take();
    }
}
