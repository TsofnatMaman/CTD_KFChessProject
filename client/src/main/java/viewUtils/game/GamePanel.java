package viewUtils.game;

import constants.PlayerConstants;
import endpoint.controller.IGameUI;
import interfaces.IPlayer;
import utils.LogUtils;
import viewUtils.board.BaseBoardPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * GamePanel is responsible for displaying the main game UI.
 * <p>
 * This panel contains:
 * <ul>
 *     <li>The chess board ({@link BaseBoardPanel})</li>
 *     <li>Player info panels ({@link PlayerInfoPanel})</li>
 *     <li>Game timer</li>
 *     <li>Background image</li>
 * </ul>
 * The panel handles layout, painting, and updates triggered by the game controller.
 * </p>
 */
public class GamePanel extends JPanel implements IGameUI {

    /**
     * Reference to the board panel displaying the chess board.
     */
    private final BaseBoardPanel boardPanel;

    /**
     * List of player info panels (typically 2 players).
     */
    private final List<PlayerInfoPanel> playerPanels;

    /**
     * Background image displayed behind all UI components.
     */
    private final Image backgroundImage;

    /**
     * Timer panel displayed at the top of the panel.
     */
    private final TimerPanel timerPanel;

    /**
     * Constructs a new {@code GamePanel} with the given board and player info panels.
     *
     * @param boardPanel   the {@link BaseBoardPanel} representing the chess board
     * @param playerPanels list of {@link PlayerInfoPanel} instances for each player
     */
    public GamePanel(BaseBoardPanel boardPanel, List<PlayerInfoPanel> playerPanels) {
        this.boardPanel = boardPanel;
        this.playerPanels = playerPanels;

        // Layout settings
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Load background image
        backgroundImage = loadBackgroundImage("background/background.jpg");

        // Initialize timer panel and UI components
        timerPanel = new TimerPanel();
        initUI();

        LogUtils.logDebug("GamePanel initialized");
    }

    // ----------- UI Initialization -----------

    /**
     * Adds all child components to the panel and configures layout.
     */
    private void initUI() {
        // Add player info panels to EAST and WEST
        add(playerPanels.get(0), BorderLayout.WEST);
        add(playerPanels.get(1), BorderLayout.EAST);

        // Add the main board panel in the center
        add(boardPanel, BorderLayout.CENTER);

        // Add timer panel at the top
        add(timerPanel, BorderLayout.NORTH);
    }

    /**
     * Loads an image from the resources folder.
     *
     * @param path the path to the image relative to the classpath
     * @return the loaded Image, or null if loading failed
     */
    private Image loadBackgroundImage(String path) {
        try {
            return ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(path)));
        } catch (IOException | IllegalArgumentException e) {
            LogUtils.logDebug("Could not load background image: " + e.getMessage());
            return null;
        }
    }

    // ----------- Game UI Interface -----------

    /**
     * Displays a non-blocking dialog announcing the winner.
     *
     * @param winner the winning player
     */
    @Override
    public void onWin(IPlayer winner) {
        JOptionPane pane = new JOptionPane(
                "Game Over. Winner: Player " + winner.getName() + " (" +
                        PlayerConstants.COLORS_NAME[winner.getId()] + ")",
                JOptionPane.INFORMATION_MESSAGE
        );
        JDialog dialog = pane.createDialog(this, "Game Over");
        dialog.setModal(false);
        dialog.setVisible(true);
    }

    /**
     * Updates the timer label displayed at the top of the panel.
     *
     * @param text the new timer text
     */
    @Override
    public void updateTimerLabel(String text) {
        timerPanel.updateTimerLabel(text);
    }

    /**
     * Called when the game state is updated (e.g., piece moved, captured).
     * Triggers a repaint of the board panel.
     */
    @Override
    public void onGameUpdate() {
        boardPanel.update();
    }

    // ----------- Overrides -----------

    /**
     * Paints the panel, including the background image.
     *
     * @param g the {@link Graphics} context
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // ----------- Getters -----------

    /**
     * Returns the main board panel.
     *
     * @return the {@link BaseBoardPanel} displaying the chess board
     */
    @Override
    public BaseBoardPanel getBoardPanel() {
        return boardPanel;
    }
}
