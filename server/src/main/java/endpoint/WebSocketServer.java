package endpoint;

import org.glassfish.tyrus.server.Server;
;

public class WebSocketServer {
    public static void main(String[] args) throws Exception {
        Server ws = new Server("localhost", 8025, "/ws", null, ChessServerEndpoint.class);
        ws.start();
        System.out.println("WebSocket server started at ws://localhost:8025/ws/game");
        System.out.println("Press any key to stop...");
        System.in.read();
        ws.stop();
    }
}
