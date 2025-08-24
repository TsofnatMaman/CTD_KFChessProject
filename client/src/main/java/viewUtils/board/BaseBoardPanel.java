package viewUtils.board;

import dto.PieceView;
import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import game.IBoardView;
import interfaces.AppLogger;
import interfaces.IBoard;
import utils.Slf4jAdapter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Abstract UI panel responsible for rendering the game board and its state.
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Displays the board background image.</li>
 *     <li>Draws all pieces, cursors, selections, and legal moves.</li>
 *     <li>Handles repainting when receiving external events.</li>
 *     <li>Ensures focus to allow key bindings.</li>
 * </ul>
 * </p>
 * This class is updated externally (by controllers) and subscribes
 * to {@link EGameEvent#GAME_UPDATE} to repaint when game state changes.
 */
public abstract class BaseBoardPanel extends JPanel implements IBoardView, IEventListener {

    private static final AppLogger logger = new Slf4jAdapter(BaseBoardPanel.class);

    /** Background image of the board (loaded from resources). */
    protected BufferedImage boardImage;

    /** Reference to the game board model. */
    protected final IBoard board;

    /**
     * Constructs the board panel for a given board.
     * <p>
     * Initializes UI settings, loads the board image, and subscribes to game events.
     * </p>
     *
     * @param board the board model associated with this view
     */
    public BaseBoardPanel(IBoard board) {
        this.board = board;

        // Set preferred dimensions according to board configuration
        setPreferredSize(board.getBoardConfig().panelDimension());

        // Enable Swing optimizations
        setFocusable(true);
        requestFocusInWindow();

        setOpaque(false);
        setDoubleBuffered(true);

        // Ensure focus is available for keyboard input
        enableBoardFocus();

        // Load the background image
        loadBoardImage();

        // Subscribe to board update events
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_UPDATE, this);
    }

    /**
     * Ensures the {@code BaseBoardPanel} can receive keyboard focus when clicked.
     * <p>
     * This allows key bindings (defined in {@link #initKeyBindings()}) to work properly.
     * </p>
     */
    private void enableBoardFocus() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        // Request focus once UI is ready
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    /**
     * Initializes key bindings for this panel.
     * <p>
     * Concrete implementations must provide actual key-action mappings.
     * </p>
     */
    protected abstract void initKeyBindings();

    /**
     * Loads the board background image from {@code resources/board/board.png}.
     * <p>
     * If the image is not found, a debug message is logged instead.
     * </p>
     */
    private void loadBoardImage() {
        try {
            URL imageUrl = getClass().getClassLoader().getResource("board/board.png");
            if (imageUrl != null) {
                boardImage = ImageIO.read(imageUrl);
            } else {
                logger.debug("Board image not found in resources!");
            }
        } catch (IOException e) {
            logger.error("Exception loading board image", e);
        }
    }

    /**
     * Paints the board and its elements.
     * <p>
     * Draws the background image if available, otherwise fills with a dark gray color.
     * Delegates piece rendering to {@link BoardRenderer}.
     * </p>
     *
     * @param g the {@link Graphics} object used for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background (board image or fallback color)
        if (boardImage != null) {
            g.drawImage(boardImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Draw pieces and other board elements
        if (board != null) {
            BoardRenderer.draw(g, PieceView.toPieceViews(board), board.getBoardConfig());
        }
    }

    /**
     * Forces a repaint of the board.
     * <p>
     * Should be called when the underlying board state changes.
     * </p>
     */
    public void update() {
        repaint();
    }

    /**
     * Handles game events.
     * <p>
     * On receiving a {@link GameEvent}, triggers a repaint of the board.
     * </p>
     *
     * @param event the game event
     */
    @Override
    public void onEvent(GameEvent event) {
        repaint();
    }
}
