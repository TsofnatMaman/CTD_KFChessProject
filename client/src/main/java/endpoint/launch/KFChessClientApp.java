package endpoint.launch;

import constants.ServerConfig;
import controller.GameController;
import dto.EventType;
import dto.GameDTO;
import dto.PlayerDTO;
import dto.PlayerSelectedDTO;
import endpoint.view.AskUserName;
import endpoint.view.GamePanel;
import endpoint.view.WaitDialog;
import game.Game;
import interfaces.IPlayer;
import interfaces.IGame;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import controller.GameController.GameEventListener;

public class KFChessClientApp implements GameEventListener {

    private static final Logger logger = Logger.getLogger(KFChessClientApp.class.getName());

    private ChessClientEndpoint client;
    private GameController controller;

    private volatile int playerId = -1;
    private GamePanel gamePanel;
    private IGame gameModel;
    private final ObjectMapper mapper = new ObjectMapper();

    private final CountDownLatch playerIdLatch = new CountDownLatch(1);
    private volatile boolean gameStarted = false;

    private WaitDialog waitDialog;
    private volatile GameDTO pendingGameDTO = null;

    public KFChessClientApp() throws Exception {
        initUIAndConnect();
        // שמירה על החיבור פעיל גם אם המשתמש סוגר את ה-UI
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void initUIAndConnect() throws Exception {
        String username = AskUserName.askUsername();
        if (username == null || username.isBlank()) {
            logger.warning("No username entered, exiting.");
            shutdown();
            System.exit(0);
        }

        // יצירת URL ל-WebSocket
        String wsUrl = String.format("ws://%s:%s%s%s",
                ServerConfig.HOST,
                ServerConfig.PORT,
                ServerConfig.WS_PATH,
                ServerConfig.SERVER_ENDPOINT);

        client = new ChessClientEndpoint(new URI(wsUrl));

        // שליחת שם השחקן
        client.sendCommand(EventType.SET_NAME, username);

        waitDialog = new WaitDialog();
        waitDialog.setOnCloseAction(() -> {
            shutdown();
            System.exit(0);
        });

        controller = new GameController(client, mapper);
        controller.addListener(this);
        controller.startListening();

        // המתנה לקבלת PlayerId
        boolean gotId = playerIdLatch.await(60, TimeUnit.SECONDS);
        if (!gotId) {
            logger.severe("Did not receive playerId from server in time.");
            SwingUtilities.invokeLater(() -> waitDialog.showOrUpdate(
                    "Error: Did not receive player ID from server."));
            shutdown();
            throw new RuntimeException("Failed to receive playerId from server");
        }

        if (!gameStarted) {
            SwingUtilities.invokeLater(() -> waitDialog.showOrUpdate("Waiting for the game to start..."));
        }
    }

    @Override
    public void onWaitMessage(String message) {
        if (gameStarted) return;
        SwingUtilities.invokeLater(() -> waitDialog.showOrUpdate(message));
    }

    @Override
    public void onGameInit(GameDTO gameDTO) {
        gameStarted = true;
        SwingUtilities.invokeLater(() -> waitDialog.close());

        try {
            IPlayer[] players = Arrays.stream(gameDTO.getPlayers())
                    .map(p -> PlayerDTO.to(p, gameDTO.getBoardConfig()))
                    .toArray(IPlayer[]::new);
            gameModel = new Game(gameDTO.getBoardConfig(), players);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize game model", e);
            return;
        }

        if (playerId == -1) {
            pendingGameDTO = gameDTO;
        } else {
            SwingUtilities.invokeLater(() -> initializeGameUI(gameDTO));
        }
    }

    @Override
    public void onPlayerSelected(PlayerSelectedDTO cmd) {
        if (gameModel == null) return;

        gameModel.handleSelection(cmd.getPlayerId(), cmd.getSelection());
        SwingUtilities.invokeLater(() -> {
            if (gamePanel != null && gamePanel.getBoardPanel() != null) {
                gamePanel.getBoardPanel().repaint();
            }
        });
    }

    @Override
    public void onPlayerId(int id) {
        playerId = id;
        logger.info("Received playerId: " + playerId);

        playerIdLatch.countDown();

        if (pendingGameDTO != null) {
            GameDTO dto = pendingGameDTO;
            pendingGameDTO = null;
            SwingUtilities.invokeLater(() -> initializeGameUI(dto));
        }
    }

    @Override
    public void onUnknownMessage(String type) {
        logger.warning("Unknown message type: " + type);
    }

    private void initializeGameUI(GameDTO gameDTO) {
        JFrame frame = new JFrame("KFCHESS - Player " + (playerId + 1) +
                " - " + gameModel.getPlayerById(playerId).getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel = new GamePanel(gameModel, playerId, client, new ObjectMapper());
        gamePanel.setStartTimeNano(gameDTO.getStartTimeNano());

        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        // הפעלת Thread של הלוגיקה של המשחק
        new Thread(gameModel::run, "Game-Loop-Thread").start();
    }

    public void shutdown() {
        try {
            if (controller != null) controller.stopListening();
            if (client != null) client.close();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error during shutdown", e);
        }
    }

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                new KFChessClientApp();
                // שמירה על התהליך פעיל עד סגירה ידנית
                synchronized (KFChessClientApp.class) {
                    KFChessClientApp.class.wait();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "App-Starter").start();
    }
}
