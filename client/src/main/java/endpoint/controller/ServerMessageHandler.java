package endpoint.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import dto.*;
import utils.LogUtils;

/**
 * Handles parsing and reacting to messages received from the server.
 *
 * <p>Each message is expected to be a JSON string containing a "type" field
 * and optionally a "data" field. The handler routes the message to
 * appropriate methods in {@link GameController} based on its type.</p>
 */
public class ServerMessageHandler {

    /** Reference to the main game controller to trigger actions. */
    private final GameController controller;

    /**
     * Constructs a new ServerMessageHandler.
     *
     * @param controller the GameController instance to delegate actions to
     */
    public ServerMessageHandler(GameController controller) {
        this.controller = controller;
    }

    /**
     * Handles a raw JSON message received from the server.
     *
     * @param message the raw JSON string
     */
    public void handleMessage(String message) {
        try {
            // Parse the message into a JSON tree
            JsonNode root = controller.getMapper().readTree(message);
            String typeStr = root.path("type").asText("");
            JsonNode dataNode = root.path("data");

            // Determine the event type, defaulting to UNKNOWN if invalid
            EventType type = EventType.UNKNOWN;
            try {
                type = EventType.valueOf(typeStr);
            } catch (IllegalArgumentException ignored) {}

            // Route message based on type
            switch (type) {
                case WAIT ->
                        controller.fireEvent(l -> l.onWaitMessage(dataNode.asText("")));
                case GAME_INIT ->
                        controller.playInit(controller.getMapper().treeToValue(dataNode, GameDTO.class));
                case PLAYER_SELECTED ->
                        controller.onPlayerSelect(controller.getMapper().treeToValue(dataNode, PlayerSelectedDTO.class));
                case PLAYER_ID ->
                        controller.onPlayerId(dataNode.asInt(-1));
                default ->
                        controller.fireEvent(l -> l.onUnknownMessage(typeStr));
            }
        } catch (JsonProcessingException e) {
            // Log JSON parsing errors for debugging purposes
            LogUtils.logDebug("Failed to parse JSON message: " + e.getMessage());
        }
    }
}
