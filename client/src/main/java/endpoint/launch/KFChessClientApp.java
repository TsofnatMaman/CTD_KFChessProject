package endpoint.launch;

import constants.PlayerConstants;
import constants.ServerConfig;
import endpoint.controller.GameController;
import endpoint.controller.GameController.GameEventListener;
import dto.EventType;
import dto.GameDTO;
import endpoint.view.AskUserName;
import endpoint.view.WaitDialog;

import com.fasterxml.jackson.databind.ObjectMapper;
import viewUtils.game.GamePanel;

import javax.swing.*;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main client application for KFChess.
 * Handles UI initialization, server communication, and game start.
 */
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

        // Keep WebSocket alive even if UI is closed
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    /**
     * Initializes the WebSocket connection, sends player name,
     * and shows a waiting dialog until the game starts.
     */
    private void initUIAndConnect() throws Exception {
        String wsUrl = String.format("ws://%s:%s%s%s",
                ServerConfig.HOST,
                ServerConfig.PORT,
                ServerConfig.WS_PATH,
                ServerConfig.SERVER_ENDPOINT);

        client = new ChessClientEndpoint(new URI(wsUrl));

        // Prompt for username
        String username;
        try {
            username = AskUserName.askUsername();
        } catch (Exception e) {
            username = null;
        }

        if (username == null || username.isBlank()) {
            username = "Anonymous - " + (playerId != -1 ? PlayerConstants.COLORS_NAME[playerId] : "");
        }

        // Send player name to server
        client.sendCommand(EventType.SET_NAME, username);

        // Initialize waiting dialog
        waitDialog = new WaitDialog();
        waitDialog.setOnCloseAction(() -> {
            shutdown();
            System.exit(0);
        });

        // Initialize game controller
        controller = new GameController(client, new ObjectMapper());
        controller.addListener(this);
        controller.startListening();

        // Show initial waiting message
        SwingUtilities.invokeLater(() -> waitDialog.showOrUpdate("Waiting for player ID from server..."));

        // Wait asynchronously for playerId
        playerIdLatch.await(60, TimeUnit.SECONDS);

        if (playerId == -1) {
            SwingUtilities.invokeLater(() -> waitDialog.showOrUpdate(
                    "Error: Did not receive player ID from server."));
            shutdown();
            throw new RuntimeException("Failed to receive playerId from server");
        }

        if (!gameStarted) {
            SwingUtilities.invokeLater(() -> waitDialog.showOrUpdate("Waiting for the game to start..."));
        }
    }

    // ------------------- Event Listener Callbacks -------------------

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

        // If game init was received before playerId, process it now
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

        // Close waiting dialog
        SwingUtilities.invokeLater(() -> waitDialog.close());

        // Initialize and display the game UI
        SwingUtilities.invokeLater(this::initializeGameUI);
    }

    // ------------------- UI Initialization -------------------

    private void initializeGameUI() {
        JFrame frame = new JFrame("KFCHESS - Player " + (playerId + 1) +
                " - " + controller.getModel().getPlayerById(playerId).getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Get the GamePanel from controller
        GamePanel gamePanel = (GamePanel) controller.getGamePanel();
        frame.add(gamePanel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        // Start the main game loop
        controller.startRunGame();
    }

    // ------------------- Shutdown -------------------

    public void shutdown() {
        try {
            if (controller != null) controller.stopListening();
            if (client != null) client.close();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error during shutdown", e);
        }
    }

    // ------------------- Main Entry Point -------------------

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                new KFChessClientApp();

                // Keep process alive until manual exit
                synchronized (KFChessClientApp.class) {
                    KFChessClientApp.class.wait();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "App-Starter").start();
    }
}