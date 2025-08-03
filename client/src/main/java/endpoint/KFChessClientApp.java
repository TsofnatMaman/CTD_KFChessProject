package endpoint;

import constants.ServerConfig;
import dto.GameDTO;
import dto.PlayerDTO;
import dto.PlayerSelected;
import game.Game;
import interfaces.IPlayer;
import sound.EventListener;
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
    private static final Logger logger = Logger.getLogger(KFChessClientApp.class.getName());

    private final ChessClientEndpoint client;
    private volatile int playerId = -1;
    private GamePanel gamePanel;
    private IGame gameModel;
    private final ObjectMapper mapper = new ObjectMapper();

    private final CountDownLatch playerIdLatch = new CountDownLatch(1);
    private volatile boolean gameStarted = false;

    private final WaitDialog waitDialog;

    public KFChessClientApp() throws Exception {
        String username = JOptionPane.showInputDialog(null,
            utils.ConfigLoader.getMessage("enter.name", "Enter your name:"),
            utils.ConfigLoader.getMessage("welcome.title", "Welcome to KFCHESS"),
            JOptionPane.PLAIN_MESSAGE);
        if (username == null || username.trim().isEmpty()) {
            username = utils.ConfigLoader.getMessage("anonymous.name", "Anonymous"); // extracted default name
        }

        String wsHost = ServerConfig.HOST;
        int wsPort = ServerConfig.PORT;
        String wsPath = ServerConfig.WS_PATH;
        String wsEndpoint = ServerConfig.SERVER_ENDPOINT;
        String wsUrl = String.format("ws://%s:%s%s%s", wsHost, wsPort, wsPath, wsEndpoint); // extracted connection string
        client = new ChessClientEndpoint(new URI(wsUrl));

        client.sendText(String.format("{\"type\":\"%s\", \"data\":\"%s\"}", constants.CommandNames.SET_NAME, username)); // extracted message type

        waitDialog = new WaitDialog();
        MessageListener messageListener = new MessageListener(client, mapper, this);

        messageListener.start();

        boolean gotId = playerIdLatch.await(60, TimeUnit.SECONDS);
        if (!gotId) {
            logger.severe(utils.ConfigLoader.getMessage("error.no.playerid", "Did not receive playerId in time."));
            SwingUtilities.invokeLater(() -> waitDialog.showOrUpdate(utils.ConfigLoader.getMessage("error.no.playerid", "Error: Did not receive player ID from server.")));
            throw new RuntimeException("Failed to receive playerId from server");
        }

        if (!gameStarted) {
            SwingUtilities.invokeLater(() -> waitDialog.showOrUpdate(utils.ConfigLoader.getMessage("waiting.game.start", "Waiting for the game to start...")));
        }
    }

    /**
     * Called by MessageListener with the received JsonNode message.
     */
    public void onMessage(JsonNode root) {
        String type = root.path("type").asText("");
        switch (type) {
            case constants.CommandNames.WAIT:
                if (gameStarted) return;
                String msg = root.path("data").asText("");
                SwingUtilities.invokeLater(() -> waitDialog.showOrUpdate(msg));
                break;

            case constants.CommandNames.GAME_INIT:
                gameStarted = true;
                SwingUtilities.invokeLater(waitDialog::close);
                GameDTO gameDTO;
                try {
                    gameDTO = mapper.treeToValue(root.path("data"), GameDTO.class);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, utils.ConfigLoader.getMessage("process.message.error", "Failed to deserialize gameInit data"), e); // replaced string with messages.properties key
                    return;
                }
                IPlayer[] players = Arrays.stream(gameDTO.getPlayers())
                        .map(p -> PlayerDTO.to(p, gameDTO.getBoardConfig()))
                        .toArray(IPlayer[]::new);
                gameModel = new Game(gameDTO.getBoardConfig(), players);

                new EventListener();

                SwingUtilities.invokeLater(() -> initializeGameUI(gameDTO));
                break;

            case constants.CommandNames.PLAYER_SELECTED:
                if (gameModel == null) return;
                PlayerSelected cmd;
                try {
                    cmd = mapper.treeToValue(root.path("data"), PlayerSelected.class);
                } catch (Exception e) {
                    logger.log(Level.WARNING, utils.ConfigLoader.getMessage("process.message.error", "Failed to deserialize playerSelected data"), e); // replaced string with messages.properties key
                    return;
                }
                gameModel.handleSelection(cmd.getPlayerId(), cmd.getSelection());
                SwingUtilities.invokeLater(() -> {
                    if (gamePanel != null && gamePanel.getBoardPanel() != null) {
                        gamePanel.getBoardPanel().repaint();
                    }
                });
                break;

            case constants.CommandNames.PLAYER_ID:
                playerId = root.path("data").asInt(-1);
                logger.info(utils.ConfigLoader.getMessage("client.connected.log", "Received playerId: ") + playerId); // replaced string with messages.properties key
                playerIdLatch.countDown();
                break;

            default:
                logger.warning(utils.ConfigLoader.getMessage("unknown.message.type.error", "Unknown message type: ") + type); // replaced string with messages.properties key
        }
    }

    private void initializeGameUI(GameDTO gameDTO) {
        JFrame frame = new JFrame("KFCHESS - Player " + (playerId + 1) + " - " + gameModel.getPlayerById(playerId).getName());
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
