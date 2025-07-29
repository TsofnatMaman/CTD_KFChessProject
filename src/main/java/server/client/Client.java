package server.client;

import interfaces.IGame;

import javax.websocket.Session;

public class Client {
    private final int playerId;
    private final Session session;
    private final WebSocketBoardView boardView;

    public Client(int playerId, Session session, IGame game) {
        this.playerId = playerId;
        this.session = session;
        this.boardView = new WebSocketBoardView(session, game, playerId);
    }

    public int getPlayerId() {
        return playerId;
    }

    public Session getSession() {
        return session;
    }

    public WebSocketBoardView getBoardView() {
        return boardView;
    }
}
