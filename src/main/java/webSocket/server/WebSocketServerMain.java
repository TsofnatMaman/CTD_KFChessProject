package webSocket.server;

import org.glassfish.tyrus.server.Server;

public class WebSocketServerMain {
    public static void main(String[] args) {
        Server server = new Server("localhost", 8025, "/ws", null, ChessServerEndpoint.class);
        try {
            server.start();
            System.out.println("Press any key to stop the server...");
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}
