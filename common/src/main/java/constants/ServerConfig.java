package constants;

import utils.ConfigLoader;

/**
 * Configuration constants for the game server.
 * Values can be overridden via config.properties.
 */
public final class ServerConfig {

    /** Server endpoint used in ServerEndpoint annotation. */
    public static final String SERVER_ENDPOINT = "/game";

    /** Hostname or IP address of the server (default "localhost"). */
    public static final String HOST = ConfigLoader.getConfig("server.host", "localhost");

    /** Port number of the server (default 8025). */
    public static final int PORT = Integer.parseInt(ConfigLoader.getConfig("server.port", "8025"));

    /** WebSocket path (default "/ws"). */
    public static final String WS_PATH = ConfigLoader.getConfig("server.ws.path", "/ws");

    /** WebSocket game endpoint (default "/game"). */
    public static final String WS_GAME_ENDPOINT = ConfigLoader.getConfig("server.endpoint.game", "/game");

    /** Private constructor to prevent instantiation. */
    private ServerConfig() {}
}
