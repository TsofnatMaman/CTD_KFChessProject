package endpoint.launch;

import org.glassfish.tyrus.server.Server;
import constants.ServerConfig;
import server.ChessServerEndpoint;

/**
 * Main entry point for launching the WebSocket server used in the chess game.
 * <p>
 * This class initializes and starts a Tyrus WebSocket server using configuration
 * parameters defined in {@link ServerConfig}. It binds the server to the provided
 * host, port, and WebSocket path, and exposes the {@link ChessServerEndpoint}.
 * </p>
 */
public class WebSocketServer {

    /**
     * Starts the WebSocket server and waits for user input to stop it.
     *
     * @param args command-line arguments (not used).
     * @throws Exception if the server fails to start or stop.
     */
    public static void main(String[] args) throws Exception {
        // Initialize the WebSocket server with HOST, PORT, and WS_PATH
        Server server = new Server(
                ServerConfig.HOST,
                ServerConfig.PORT,
                ServerConfig.WS_PATH,
                null,
                ChessServerEndpoint.class
        );

        try {
            // Start the server
            server.start();
            System.out.printf(
                    "WebSocket server started at ws://%s:%d%s%s%n",
                    ServerConfig.HOST,
                    ServerConfig.PORT,
                    ServerConfig.WS_PATH,
                    ServerConfig.SERVER_ENDPOINT
            );

            // Keep server running until user presses a key
            System.out.println("Press any key to stop...");
            System.in.read();
        } finally {
            // Ensure server is stopped gracefully
            server.stop();
            System.out.println("WebSocket server stopped.");
        }
    }
}
