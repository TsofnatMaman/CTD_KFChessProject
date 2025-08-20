package endpoint.launch;

import org.glassfish.tyrus.server.Server;
import constants.ServerConfig;

/**
 * Entry point for launching the WebSocket server for the chess game.
 */
public class WebSocketServer {

    public static void main(String[] args) throws Exception {
        // Create a WebSocket server with specified HOST, PORT, and PATH
        Server server = new Server(
                ServerConfig.HOST,
                ServerConfig.PORT,
                ServerConfig.WS_PATH,
                null,
                ChessServerEndpoint.class
        );

        try {
            server.start();
            System.out.printf("WebSocket server started at ws://%s:%d%s%s%n",
                    ServerConfig.HOST,
                    ServerConfig.PORT,
                    ServerConfig.WS_PATH,
                    ServerConfig.SERVER_ENDPOINT
            );

            System.out.println("Press any key to stop...");
            System.in.read(); // Wait for key press to stop the server
        } finally {
            server.stop();
            System.out.println("WebSocket server stopped.");
        }
    }
}
