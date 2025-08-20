package endpoint.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import dto.*;
import utils.LogUtils;

/**
 * Handles parsing and reacting to server messages.
 */
public class ServerMessageHandler {

    private final GameController controller;

    public ServerMessageHandler(GameController controller) {
        this.controller = controller;
    }

    public void handleMessage(String message) {
        try {
            JsonNode root = controller.getMapper().readTree(message);
            String typeStr = root.path("type").asText("");
            JsonNode dataNode = root.path("data");

            EventType type = EventType.UNKNOWN;
            try { type = EventType.valueOf(typeStr); } catch (IllegalArgumentException ignored) {}

            switch (type) {
                case WAIT -> controller.fireEvent(l -> l.onWaitMessage(dataNode.asText("")));
                case GAME_INIT -> controller.playInit(controller.getMapper().treeToValue(dataNode, GameDTO.class));
                case PLAYER_SELECTED -> controller.onPlayerSelect(controller.getMapper().treeToValue(dataNode, PlayerSelectedDTO.class));
                case PLAYER_ID -> controller.onPlayerId(dataNode.asInt(-1));
                default -> controller.fireEvent(l -> l.onUnknownMessage(typeStr));
            }
        } catch (JsonProcessingException e) {
            LogUtils.logDebug("Failed to parse JSON message: " + e.getMessage());
        }
    }
}
