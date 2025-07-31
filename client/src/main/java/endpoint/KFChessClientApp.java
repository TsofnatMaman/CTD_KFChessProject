package endpoint;

import dto.GameDTO;
import dto.PlayerDTO;
import dto.PlayerSelected;
import game.Game;
import interfaces.IPlayer;
import sound.listeners.CapturedSoundListener;
import sound.listeners.GameEndSoundListener;
import sound.listeners.JumpsSoundListener;
import sound.listeners.MovesSoundListener;
import interfaces.IGame;
import view.GamePanel;

import javax.swing.*;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KFChessClientApp {

    private static int numPlayerConnect = 0;
    private static final int MAX_PLAYER=2;
    private static final Logger logger = Logger.getLogger(KFChessClientApp.class.getName());

    private final ChessClientEndpoint client;
    private volatile int playerId = -1;
    private GamePanel gamePanel;
    private IGame gameModel;
    private final ObjectMapper mapper = new ObjectMapper();

    private final CountDownLatch playerIdLatch = new CountDownLatch(1);
    private volatile boolean gameStarted = false;

    private final WaitDialog waitDialog;
    private final MessageListener messageListener;

    public KFChessClientApp() throws Exception {
        if(numPlayerConnect > MAX_PLAYER){
            System.out.println("game full");
            throw new RuntimeException("game full");
        }

        String username = JOptionPane.showInputDialog(null, "Enter your name:", "Welcome to KFCHESS", JOptionPane.PLAIN_MESSAGE);
        if (username == null || username.trim().isEmpty()) {
            username = "Anonymous"; // ברירת מחדל
        }
        
        client = new ChessClientEndpoint(new URI("ws://localhost:8025/ws/game"));
        numPlayerConnect++;

        client.sendText("{\"type\":\"setName\", \"data\":\"" + username + "\"}");

        waitDialog = new WaitDialog();
        messageListener = new MessageListener(client, mapper, this);

        messageListener.start();

        boolean gotId = playerIdLatch.await(60, TimeUnit.SECONDS);
        if (!gotId) {
            logger.severe("Did not receive playerId in time.");
            SwingUtilities.invokeLater(() -> waitDialog.showOrUpdate("Error: Did not receive player ID from server."));
            throw new RuntimeException("Failed to receive playerId from server");
        }

        if (!gameStarted) {
            SwingUtilities.invokeLater(() -> waitDialog.showOrUpdate("Waiting for the game to start..."));
        }
    }

    /**
     * Called by MessageListener with the received JsonNode message.
     */
    public void onMessage(JsonNode root) {
        String type = root.path("type").asText("");
        switch (type) {
            case "wait":
                if (gameStarted) return;
                String msg = root.path("data").asText("");
                SwingUtilities.invokeLater(() -> waitDialog.showOrUpdate(msg));
                break;

            case "gameInit":
                gameStarted = true;
                SwingUtilities.invokeLater(waitDialog::close);
                GameDTO gameDTO;
                try {
                    gameDTO = mapper.treeToValue(root.path("data"), GameDTO.class);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Failed to deserialize gameInit data", e);
                    return;
                }
                IPlayer[] players = Arrays.stream(gameDTO.getPlayers())
                        .map(p -> PlayerDTO.to(p, gameDTO.getBoardConfig()))
                        .toArray(IPlayer[]::new);
                gameModel = new Game(gameDTO.getBoardConfig(), players);

                new MovesSoundListener();
                new JumpsSoundListener();
                new CapturedSoundListener();
                new GameEndSoundListener();

                SwingUtilities.invokeLater(() -> initializeGameUI(gameDTO));
                break;

            case "playerSelected":
                if (gameModel == null) return;
                PlayerSelected cmd;
                try {
                    cmd = mapper.treeToValue(root.path("data"), PlayerSelected.class);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to deserialize playerSelected data", e);
                    return;
                }
                gameModel.handleSelection(cmd.getPlayerId(), cmd.getSelection());
                SwingUtilities.invokeLater(() -> {
                    if (gamePanel != null && gamePanel.getBoardPanel() != null) {
                        gamePanel.getBoardPanel().repaint();
                    }
                });
                break;

            case "playerId":
                playerId = root.path("data").asInt(-1);
                logger.info("Received playerId: " + playerId);
                playerIdLatch.countDown();
                break;

            default:
                logger.warning("Unknown message type: " + type);
        }
    }

    private void initializeGameUI(GameDTO gameDTO) {
        JFrame frame = new JFrame("KFCHESS - Player " + (playerId + 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel = new GamePanel(gameModel, playerId, client, new ObjectMapper());
        gamePanel.setStartTimeNano(gameDTO.getStartTimeNano());

        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        gameModel.run();
    }

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                new KFChessClientApp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "App-Starter").start();
    }
}
