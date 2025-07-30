package webSocket.client;

import interfaces.IBoard;
import pieces.Position;

import javax.swing.*;
import javax.websocket.*;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

@ClientEndpoint
public class ChessClientEndpoint {

    private Session session;
    private int playerId = -1;
    private IBoard receivedBoard;

    private final CountDownLatch idLatch = new CountDownLatch(1);
    private final CountDownLatch boardLatch = new CountDownLatch(1);

    public ChessClientEndpoint(URI serverUri) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, serverUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // קבלת הודעת טקסט (JSON) עם מזהה שחקן
    @OnMessage
    public void onMessage(String message) {
        try {
            if (message.contains("\"type\":\"playerId\"")) {
                int start = message.indexOf("\"id\":") + 5;
                int end = message.indexOf("}", start);
                playerId = Integer.parseInt(message.substring(start, end));
                idLatch.countDown();
                System.out.println("Player ID received: " + playerId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // קבלת הודעה בינארית - אובייקט IBoard מסריאליזציה
    @OnMessage
    public void onMessage(ByteBuffer buffer) {
        try {
            // קריאה נכונה למערך בתים מתוך ByteBuffer
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            receivedBoard = (IBoard) ois.readObject();
            boardLatch.countDown();
            System.out.println("Board received from server.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to server.");
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Connection closed: " + reason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    // שליחת בחירה (Position) לשרת
    public void sendPosition(Position pos) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(pos);
            oos.flush();
            byte[] data = baos.toByteArray();

            session.getAsyncRemote().sendBinary(ByteBuffer.wrap(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // המתנה לקבלת מזהה שחקן
    public int waitForPlayerId() throws InterruptedException {
        idLatch.await();
        return playerId;
    }

    // המתנה לקבלת לוח מהשרת
    public IBoard waitForBoard() throws InterruptedException {
        boardLatch.await();
        return receivedBoard;
    }
}
