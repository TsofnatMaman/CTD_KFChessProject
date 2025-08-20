package endpoint.launch;

import constants.PlayerConstants;
import constants.ServerConfig;
import controller.GameController;
import dto.EventType;
import dto.GameDTO;
import endpoint.view.AskUserName;
import endpoint.view.GamePanel;
import endpoint.view.WaitDialog;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.net.URI;
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

    private final CountDownLatch playerIdLatch = new CountDownLatch(1);
    private boolean gameStarted = false;

    private WaitDialog waitDialog;
    private volatile GameDTO pendingGameDTO = null;

    public KFChessClientApp() throws Exception {
        initUIAndConnect();
        // שמירה על החיבור פעיל גם אם המשתמש סוגר את ה-UI
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void initUIAndConnect() throws Exception {
        // יצירת URL ל-WebSocket
        String wsUrl = String.format("ws://%s:%s%s%s",
                ServerConfig.HOST,
                ServerConfig.PORT,
                ServerConfig.WS_PATH,
                ServerConfig.SERVER_ENDPOINT);

        client = new ChessClientEndpoint(new URI(wsUrl));

        String username;
        try{
            username= AskUserName.askUsername();
        } catch (Exception e) {
            username = null;
        }
        if (username == null || username.isBlank()) {
            username = "Anonymous - "+(playerId !=-1? PlayerConstants.COLORS_NAME[playerId]:"");
        }

        // שליחת שם השחקן
        client.sendCommand(EventType.SET_NAME, username);

        waitDialog = new WaitDialog();
        waitDialog.setOnCloseAction(() -> {
            shutdown();
            System.exit(0);
        });

        controller = new GameController(client, new ObjectMapper());
        controller.addListener(this);
        controller.startListening();

        // המתנה לקבלת PlayerId בצורה אסינכרונית
        SwingUtilities.invokeLater(() -> waitDialog.showOrUpdate("Waiting for player ID from server..."));

        playerIdLatch.await(60, TimeUnit.SECONDS);

        if (playerId == -1) { // בדיקה סופית אחרי ההמתנה
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
    public void onPlayerId(int id) {
        playerId = id;
        logger.info("Received playerId: " + playerId);

        playerIdLatch.countDown();

        if (pendingGameDTO != null) {
            pendingGameDTO = null;
            SwingUtilities.invokeLater(this::onGameInit);
        }
    }

    @Override
    public void onUnknownMessage(String type) {
        logger.warning("Unknown message type: " + type);
    }

    @Override
    public void onGameInit() {
        gameStarted = true;
        SwingUtilities.invokeLater(() -> waitDialog.close());

        SwingUtilities.invokeLater(this::initializeGameUI);
    }

    private void initializeGameUI() {
        JFrame frame = new JFrame("KFCHESS - Player " + (playerId + 1) +
                " - " + controller.getModel().getPlayerById(playerId).getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GamePanel gamePanel = (GamePanel) controller.getGamePanel();

        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        controller.startRunGame();
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