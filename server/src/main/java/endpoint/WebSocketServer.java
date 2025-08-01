package endpoint;

import org.glassfish.tyrus.server.Server;
;

public class WebSocketServer {
    public static void main(String[] args) throws Exception {
        // Extracted host, port, wsPath to config.properties
        String host = utils.ConfigLoader.getConfig("server.host", "localhost"); // extracted to config.properties
        int port = Integer.parseInt(utils.ConfigLoader.getConfig("server.port", "8025")); // extracted to config.properties
        String wsPath = utils.ConfigLoader.getConfig("server.ws.path", "/ws"); // extracted to config.properties
        Server ws = new Server(host, port, wsPath, null, ChessServerEndpoint.class);
        ws.start();
        System.out.println(String.format("WebSocket server started at ws://%s:%d%s%s", host, port, wsPath, constants.GameConstants.SERVER_ENDPOINT)); // extracted endpoint
        System.out.println("Press any key to stop...");
        System.in.read();
        ws.stop();
    }
}
