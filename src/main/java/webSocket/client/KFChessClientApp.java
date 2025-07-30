package webSocket.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.Game;
import interfaces.IPlayer;
import interfaces.IGame;
import pieces.Position;
import player.PlayerCursor;
import webSocket.client.view.GamePanel;
import webSocket.server.ServerMessage;
import webSocket.server.dto.GameDTO;
import webSocket.server.dto.PlayerDTO;
import webSocket.server.dto.PlayerSelected;

import javax.swing.*;
import java.net.URI;
import java.util.Arrays;

public class KFChessClientApp {

    private final ChessClientEndpoint client;
    private int playerId = -1;
    private PlayerCursor cursor;
    private GamePanel gamePanel;
    private IGame gameModel;  // מודל המשחק המקומי
    private final ObjectMapper mapper = new ObjectMapper();

    public KFChessClientApp() throws Exception {
        URI uri = new URI("ws://localhost:8025/ws/game");  // חשוב שהכתובת תתאים לשרת שלך!

        client = new ChessClientEndpoint(uri);

        // מחכים לקבלת playerId (מזהה השחקן מהשרת)
        while (client.getPlayerId() < 0) {
            Thread.sleep(100);
        }
        playerId = client.getPlayerId();
        System.out.println("Client playerId = " + playerId);

        // מאזין להודעות מהשרת בלולאה נפרדת
        new Thread(() -> {
            try {
                while (true) {
                    String message = client.waitForNextMessage();
                    if (message == null) continue;

                    System.out.println("Received raw message: " + message);

                    JsonNode root = mapper.readTree(message);
                    String type = root.has("type") ? root.get("type").asText() : "";

                    switch (type) {
                        case "gameInit": {
                            JsonNode dataNode = root.get("data");
                            GameDTO gameDTO = (GameDTO) mapper.treeToValue(dataNode, ServerMessage.class).getData();

                            // יצירת מערך שחקנים מ-PlayerDTO
                            IPlayer[] players = Arrays.stream(gameDTO.getPlayers())
                                    .map(p -> PlayerDTO.to(p, gameDTO.getBoardConfig()))
                                    .toArray(IPlayer[]::new);

                            // יצירת מופע משחק מלא עם הנתונים מהשרת
                            gameModel = new Game(gameDTO.getBoardConfig(), players);

                            // יצירת GUI על EDT
                            SwingUtilities.invokeLater(() -> {
                                gamePanel = new GamePanel(gameModel, playerId);

                                // שמירת הסמן מקומי (למשל סמן עבור המשתמש)
                                cursor = (PlayerCursor) gamePanel.getBoardPanel().getPlayerCursor();

                                // מאזין לפעולות של השחקן בלוח
                                gamePanel.getBoardPanel().setOnPlayerAction((v) -> {
                                    try {
                                        Position pos = cursor.getPosition();
                                        PlayerSelected cmd = new PlayerSelected(playerId, pos);
                                        String jsonCmd = mapper.writeValueAsString(cmd);
                                        client.sendText(jsonCmd);
                                        System.out.println("Sent selection: " + pos);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });

                                JFrame frame = new JFrame("KFCHESS - Player " + (playerId + 1));
                                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                frame.add(gamePanel);
                                frame.pack();
                                frame.setLocationRelativeTo(null);
                                frame.setVisible(true);

                                gamePanel.setFocusable(true);
                                gamePanel.requestFocusInWindow();

                                // אפשר להפעיל את הלולאת המשחק (טיימר וכו')
                                gameModel.run();
                            });
                            break;
                        }
                        case "playerSelected": {
                            PlayerSelected cmd = mapper.treeToValue(root.get("data"), PlayerSelected.class);
                            if (gameModel != null) {
                                // עדכון מצב המשחק המקומי בהתאם לבחירה מהשרת
                                gameModel.handleSelection(cmd.getPlayerId(), cmd.getSelection());
                                SwingUtilities.invokeLater(() -> gamePanel.getBoardPanel().repaint());
                            }
                            break;
                        }
                        default:
                            System.out.println("Unknown message type: " + type);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new KFChessClientApp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
