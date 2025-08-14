package endpoint.launch;

import org.glassfish.tyrus.server.Server;
import constants.ServerConfig;

public class WebSocketServer {
    public static void main(String[] args) throws Exception {
        Server ws = new Server(ServerConfig.HOST, ServerConfig.PORT, ServerConfig.WS_PATH, null, ChessServerEndpoint.class);
        ws.start();
        System.out.printf("WebSocket server started at ws://%s:%d%s%s%n",
                ServerConfig.HOST, ServerConfig.PORT, ServerConfig.WS_PATH, ServerConfig.SERVER_ENDPOINT);
        System.out.println("Press any key to stop...");
        System.in.read();
        ws.stop();
    }
}
