package constants;

import utils.ConfigLoader;

public final class ServerConfig {

    public static final String SERVER_ENDPOINT = "/game"; // extracted for ServerEndpoint annotation
    public static final String HOST = ConfigLoader.getConfig("server.host", "localhost");
    public static final int PORT = Integer.parseInt(ConfigLoader.getConfig("server.port", "8025"));
    public static final String WS_PATH = ConfigLoader.getConfig("server.ws.path", "/ws");
    public static final String WS_GAME_ENDPOINT = ConfigLoader.getConfig("server.endpoint.game", "/game");

    private ServerConfig() {}
}
