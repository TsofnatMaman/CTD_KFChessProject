package endpoint.view;

import constants.KeyConstants;
import interfaces.IBoard;
import interfaces.IPlayerCursor;
import pieces.Position;
import viewUtils.board.BaseBoardPanel;
import viewUtils.board.CursorController;
import viewUtils.board.KeyManager;
import viewUtils.board.SelectionRenderer;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Swing UI panel that visually represents the chess board.
 * <p>
 * It is purely responsible for rendering:
 * - The board and pieces (via {@link BaseBoardPanel})
 * - The player cursor
 * - Selection highlights and legal move indicators
 * <p>
 * All updates come from the external controller layer.
 */
public class BoardPanel extends BaseBoardPanel {

    private final IPlayerCursor cursor;         // Cursor controlled by the player
    private final KeyManager keyManager;        // Handles keyboard events

    private Position selected = null;           // Currently selected square
    private List<Position> legalMoves = Collections.emptyList(); // Highlighted legal moves
    private final Color selectColor;            // Semi-transparent selection overlay color

    private Consumer<Position> onPlayerAction;  // Callback for player actions (cursor "Enter" press)

    /**
     * Creates a new board panel bound to the given board and player cursor.
     *
     * @param board  The game board instance
     * @param cursor The player's cursor instance
     */
    public BoardPanel(IBoard board, IPlayerCursor cursor) {
        super(board);
        this.cursor = cursor;

        // Keyboard manager for binding controls
        this.keyManager = new KeyManager(this);

        // Configure input controls (arrow keys + Enter)
        initKeyBindings();

        // Semi-transparent highlight color derived from player's cursor color
        Color base = cursor.getColor();
        this.selectColor = new Color(base.getRed(), base.getGreen(), base.getBlue(), 128);
    }

    /**
     * Initializes the key bindings for the player (arrow keys + Enter).
     */
    public void initKeyBindings() {
        CursorController.KeyBindings playerKey = new CursorController.KeyBindings(
                KeyConstants.UP,
                KeyConstants.DOWN,
                KeyConstants.LEFT,
                KeyConstants.RIGHT,
                KeyConstants.ENTER
        );

        CursorController cursorController = new CursorController(cursor, playerKey, keyManager);

        // Handle player action when "Enter" is pressed
        cursorController.setOnPlayerAction(pos -> {
            if (onPlayerAction != null) {
                onPlayerAction.accept(pos);
            }
        });
    }

    /**
     * Registers a callback for when the player confirms an action (cursor "Enter").
     *
     * @param handler A consumer that accepts the selected {@link Position}
     */
    public void setOnPlayerAction(Consumer<Position> handler) {
        this.onPlayerAction = handler;
    }

    /**
     * Paints the board panel, including the board, cursor, and selection overlays.
     *
     * @param g The graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw cursor
        cursor.draw(g, getWidth(), getHeight());

        // Draw selection and legal moves
        Graphics2D g2 = (Graphics2D) g;
        SelectionRenderer.draw(
                g2,
                selected,
                legalMoves,
                selectColor,
                board.getCols(),
                board.getRows(),
                getWidth(),
                getHeight()
        );
    }

    /**
     * Sets the currently selected square on the board.
     *
     * @param selected The new selected position
     */
    public void setSelected(Position selected) {
        this.selected = selected;
    }

    /**
     * Sets the currently available legal moves for highlighting.
     *
     * @param legalMoves List of legal move positions
     */
    public void setLegalMoves(List<Position> legalMoves) {
        this.legalMoves = legalMoves;
    }

    /**
     * Clears the selection and legal moves, then repaints the board.
     */
    public void clearSelection() {
        setSelected(null);
        setLegalMoves(Collections.emptyList());
        repaint();
    }
}
