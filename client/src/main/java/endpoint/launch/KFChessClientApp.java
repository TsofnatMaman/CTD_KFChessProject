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
 *
 * <p>This class initializes the UI, manages the WebSocket connection to the server,
 * handles incoming events, and starts the game once all prerequisites are met.</p>
 */
public class KFChessClientApp implements GameEventListener {

    private static final Logger logger = Logger.getLogger(KFChessClientApp.class.getName());

    /** WebSocket client endpoint */
    private ChessClientEndpoint client;

    /** Game controller for handling game logic and events */
    private GameController controller;

    /** Player ID assigned by the server */
    private volatile int playerId = -1;

    /** Latch to wait for player ID before proceeding */
    private final CountDownLatch playerIdLatch = new CountDownLatch(1);

    /** Flag indicating whether the game has started */
    private boolean gameStarted = false;

    /** Waiting dialog displayed before the game starts */
    private WaitDialog waitDialog;

    /** Holds a pending game initialization DTO if received before player ID */
    private volatile GameDTO pendingGameDTO = null;

    /**
     * Constructs and initializes the KFChess client application.
     *
     * @throws Exception if initialization or connection fails
     */
    public KFChessClientApp() throws Exception {
        initUIAndConnect();

        // Ensure WebSocket closes properly on application exit
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    /**
     * Initializes the UI, connects to the server, and sends the player name.
     *
     * @throws Exception if WebSocket connection or player ID retrieval fails
     */
    private void initUIAndConnect() throws Exception {
        String wsUrl = String.format("ws://%s:%s%s%s",
                ServerConfig.HOST,
                ServerConfig.PORT,
                ServerConfig.WS_PATH,
                ServerConfig.SERVER_ENDPOINT);

        client = new ChessClientEndpoint(new URI(wsUrl));

        // Prompt user for a username
        String username;
        try {
            username = AskUserName.askUsername();
        } catch (Exception e) {
            username = null;
        }

        if (username == null || username.isBlank()) {
            username = "Anonymous - " + (playerId != -1 ? PlayerConstants.COLORS_NAME[playerId] : "");
        }

        // Send player name to the server
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
        SwingUtilities.invokeLater(() ->
                waitDialog.showOrUpdate("Waiting for player ID from server..."));

        // Wait asynchronously for player ID
        playerIdLatch.await(60, TimeUnit.SECONDS);

        if (playerId == -1) {
            SwingUtilities.invokeLater(() ->
                    waitDialog.showOrUpdate("Error: Did not receive player ID from server."));
            shutdown();
            throw new RuntimeException("Failed to receive playerId from server");
        }

        if (!gameStarted) {
            SwingUtilities.invokeLater(() ->
                    waitDialog.showOrUpdate("Waiting for the game to start..."));
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

        // Process pending game init if received before playerId
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

    /**
     * Initializes and displays the main game window with the GamePanel.
     */
    private void initializeGameUI() {
        JFrame frame = new JFrame("KFCHESS - Player " + (playerId + 1) +
                " - " + controller.getModel().getPlayerById(playerId).getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add GamePanel to the frame
        GamePanel gamePanel = (GamePanel) controller.getGamePanel();
        frame.add(gamePanel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Start the main game loop
        controller.startRunGame();
    }

    // ------------------- Shutdown -------------------

    /**
     * Safely shuts down the application by stopping listeners and closing the WebSocket.
     */
    public void shutdown() {
        try {
            if (controller != null) controller.stopListening();
            if (client != null) client.close();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error during shutdown", e);
        }
    }

    // ------------------- Main Entry Point -------------------

    /**
     * Launches the KFChess client application.
     *
     * @param args command-line arguments (ignored)
     */
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
