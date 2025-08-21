package constants;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility class for accessing localized messages from resources/messages.properties.
 * <p>
 * Provides default fallback values for missing keys and supports formatting with arguments.
 * </p>
 */
public final class Messages {

    /** Name of the resource bundle file (without extension). */
    private static final String BUNDLE_NAME = "messages";

    /** Loaded resource bundle for accessing message strings. */
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    // Private constructor to prevent instantiation
    private Messages() {}

    /**
     * Enum containing message keys and their default values.
     * <p>
     * Use these keys to retrieve localized messages safely.
     * </p>
     */
    public enum Key {
        CLIENT_CONNECTED_LOG("client.connected.log", "Client connected: "),
        ASSIGNED_PLAYERID_LOG("assigned.playerId.log", " | assigned playerId: "),
        WAIT_MESSAGE("wait.message", "Waiting for second player to join..."),
        GAME_FULL_MESSAGE("game.full.message", "Game is full"),
        RECEIVED_MESSAGE_LOG("received.message.log", "Received message from "),
        UNKNOWN_SESSION_ERROR("unknown.session.error", "Unknown session, ignoring message"),
        PLAYER_ID_MISMATCH_ERROR("player.id.mismatch.error", "Player ID mismatch! Ignoring message from player %d"),
        SET_NAME_LOG("set.name.log", "Set name for player %d: %s"),
        CLIENT_DISCONNECTED_LOG("client.disconnected.log", "Client disconnected: "),
        REASON_LOG("reason.log", " Reason: "),
        SESSION_ERROR_LOG("session.error.log", "Error on session %s: %s"),
        UNKNOWN_MESSAGE_TYPE_ERROR("unknown.message.type.error", "Unknown message type: %s"),
        PROCESS_MESSAGE_ERROR("process.message.error", "Failed to process message: %s"),
        PLAYER_1_NAME("player.1.name", "player Black"),
        PLAYER_2_NAME("player.2.name", "player White");

        private final String key;
        private final String defaultValue;

        Key(String key, String defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        /** Returns the key string used in the resource bundle. */
        public String key() {
            return key;
        }

        /** Returns the default value if the key is missing in the bundle. */
        public String defaultValue() {
            return defaultValue;
        }
    }

    /**
     * Retrieves the message string for the given key, formatted with optional arguments.
     * <p>
     * If the key is missing or formatting fails, falls back to the default value.
     * </p>
     *
     * @param key  the message key
     * @param args optional arguments for formatting
     * @return the formatted message string
     */
    public static String get(Key key, Object... args) {
        String pattern;
        try {
            pattern = BUNDLE.getString(key.key());
        } catch (MissingResourceException e) {
            pattern = key.defaultValue();
        }

        if (args != null && args.length > 0) {
            try {
                return String.format(pattern, args);
            } catch (Exception e) {
                return pattern; // fallback if formatting fails
            }
        }

        return pattern;
    }
}
