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
 * GamePanel is responsible for displaying:
 * - The chess board (BoardPanel)
 * - Player info panels (PlayerInfoPanel)
 * - Timer and background
 * All UI elements are initialized and updated here.
 */
public class GamePanel extends JPanel implements IGameUI {

    private final BaseBoardPanel boardPanel;
    private final List<PlayerInfoPanel> playerPanels;
    private final Image backgroundImage;
    private final TimerPanel timerPanel;

    /**
     * Constructor receives all UI components and external dependencies.
     *
     * @param boardPanel BoardPanel representing the chess board
     * @param playerPanels List of two PlayerInfoPanel instances
     */
    public GamePanel(BaseBoardPanel boardPanel, List<PlayerInfoPanel> playerPanels) {
        this.boardPanel = boardPanel;
        this.playerPanels = playerPanels;

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Load background image
        backgroundImage = loadBackgroundImage("background/background.jpg");

        // Initialize UI and timer
        timerPanel = new TimerPanel();
        initUI();

        LogUtils.logDebug("GamePanel initialized");
    }

    // ----------- UI Initialization -----------

    private void initUI() {
        // Add player info panels to EAST/WEST
        add(playerPanels.get(0), BorderLayout.WEST);
        add(playerPanels.get(1), BorderLayout.EAST);

        // Configure and add the board panel
        add(boardPanel, BorderLayout.CENTER);

        // Add timer at the top
        add(timerPanel, BorderLayout.NORTH);
    }

    // ----------- COMPONENT CREATORS -----------

    private Image loadBackgroundImage(String path) {
        try {
            return ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(path)));
        } catch (IOException | IllegalArgumentException e) {
            LogUtils.logDebug("Could not load background image: " + e.getMessage());
            return null;
        }
    }

    /**
     * Called when the game ends.
     * Displays a non-blocking dialog with the winner.
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

    @Override
    public void updateTimerLabel(String text) {
        timerPanel.updateTimerLabel(text);
    }

    /**
     * Called when the board updates (piece moved, captured, etc.)
     */
    @Override
    public void onGameUpdate() {
        boardPanel.update();
    }

    // ----------- OVERRIDES -----------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // ----------- GETTERS & SETTERS -----------

    @Override
    public BaseBoardPanel getBoardPanel() {
        return boardPanel;
    }
}
