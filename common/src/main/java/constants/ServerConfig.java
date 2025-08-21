package constants;

import utils.ConfigLoader;

/**
 * Configuration constants for the game server.
 * <p>
 * Most values can be overridden via {@code config.properties}.
 * Includes host, port, WebSocket paths, and server endpoints.
 * </p>
 */
public final class ServerConfig {

    /** Server endpoint used in {@link javax.websocket.server.ServerEndpoint} annotation. */
    public static final String SERVER_ENDPOINT = "/game";

    /** Hostname or IP address of the server. Default is "localhost". */
    public static final String HOST = ConfigLoader.getConfig("server.host", "localhost");

    /** Port number of the server. Default is 8025. */
    public static final int PORT = Integer.parseInt(ConfigLoader.getConfig("server.port", "8025"));

    /** WebSocket base path. Default is "/ws". */
    public static final String WS_PATH = ConfigLoader.getConfig("server.ws.path", "/ws");

    /** WebSocket game endpoint. Default is "/game". */
    public static final String WS_GAME_ENDPOINT = ConfigLoader.getConfig("server.endpoint.game", "/game");

    // Private constructor to prevent instantiation
    private ServerConfig() {}
}
