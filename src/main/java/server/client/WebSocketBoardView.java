package server.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import game.IBoardView;
import interfaces.IGame;
import server.dto.GameDelta;
import utils.LogUtils;

import javax.websocket.Session;
import java.io.IOException;

public class WebSocketBoardView implements IBoardView {
    private final Session session;
    private final IGame game;
    private final int playerId;
    private final ObjectMapper mapper = new ObjectMapper();

    public WebSocketBoardView(Session session, IGame game, int playerId) {
        this.session = session;
        this.game = game;
        this.playerId = playerId;
    }

    @Override
    public void repaint() {
        GameDelta delta = GameDelta.fromGame(game, playerId);
        try {
            String json = mapper.writeValueAsString(delta);
            session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            LogUtils.logDebug("WebSocketBoardView repaint error: " + e.getMessage());
        }
    }
}
