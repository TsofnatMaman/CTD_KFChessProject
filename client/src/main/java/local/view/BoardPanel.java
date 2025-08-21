package local.view;

import constants.KeyConstants;
import viewUtils.board.CursorController;
import interfaces.IBoard;
import interfaces.IPlayerCursor;
import pieces.Position;
import viewUtils.board.BaseBoardPanel;
import viewUtils.board.KeyManager;
import viewUtils.board.SelectionRenderer;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * UI component representing the chess board in local mode.
 * <p>
 * Extends {@link BaseBoardPanel} and provides:
 * <ul>
 *   <li>Two player cursors (for keyboard navigation).</li>
 *   <li>Selection handling and highlighting of legal moves.</li>
 *   <li>Keyboard bindings for both players.</li>
 *   <li>Callback system to forward actions to the controller.</li>
 * </ul>
 * </p>
 */
public class BoardPanel extends BaseBoardPanel {

    /** Cursor for player 1 (arrow keys). */
    private final IPlayerCursor cursor1;

    /** Cursor for player 2 (WASD keys). */
    private final IPlayerCursor cursor2;

    /** Manages keyboard input and bindings. */
    private final KeyManager keyManager;

    /** Current selected position for player 1 (if any). */
    private Position selected1 = null;

    /** Legal moves available for player 1's selected piece. */
    private List<Position> legalMoves1 = Collections.emptyList();

    /** Current selected position for player 2 (if any). */
    private Position selected2 = null;

    /** Legal moves available for player 2's selected piece. */
    private List<Position> legalMoves2 = Collections.emptyList();

    /** Semi-transparent highlight color for player 1 selection. */
    private static final Color SELECT_COLOR_P1 = new Color(255, 0, 0, 128);

    /** Semi-transparent highlight color for player 2 selection. */
    private static final Color SELECT_COLOR_P2 = new Color(0, 0, 255, 128);

    /** Callback to notify when a player performs an action (selection/move). */
    private BiConsumer<Integer, Position> onPlayerAction;

    /**
     * Constructs a new {@code BoardPanel}.
     *
     * @param board   the game board
     * @param cursor1 player 1's cursor
     * @param cursor2 player 2's cursor
     */
    public BoardPanel(IBoard board, IPlayerCursor cursor1, IPlayerCursor cursor2) {
        super(board);
        this.cursor1 = cursor1;
        this.cursor2 = cursor2;

        keyManager = new KeyManager(this);
        initKeyBindings();
    }

    /**
     * Sets the action handler callback for player actions.
     *
     * @param handler consumer of (playerId, position)
     */
    public void setOnPlayerAction(BiConsumer<Integer, Position> handler) {
        this.onPlayerAction = handler;
    }

    /** Sets player 1's current selection. */
    public void setSelected1(Position pos) {
        selected1 = pos;
    }

    /** Sets player 1's legal moves. */
    public void setLegalMoves1(List<Position> moves) {
        legalMoves1 = moves;
    }

    /** Sets player 2's current selection. */
    public void setSelected2(Position pos) {
        selected2 = pos;
    }

    /** Sets player 2's legal moves. */
    public void setLegalMoves2(List<Position> moves) {
        legalMoves2 = moves;
    }

    /**
     * Clears selection and legal moves for player 1.
     * Forces UI repaint.
     */
    public void clearSelection1() {
        selected1 = null;
        legalMoves1 = Collections.emptyList();
        repaint();
    }

    /**
     * Clears selection and legal moves for player 2.
     * Forces UI repaint.
     */
    public void clearSelection2() {
        selected2 = null;
        legalMoves2 = Collections.emptyList();
        repaint();
    }

    /**
     * Initializes key bindings for both players:
     * <ul>
     *   <li>Player 1: Arrow keys + Enter</li>
     *   <li>Player 2: WASD keys + Space</li>
     * </ul>
     */
    @Override
    protected void initKeyBindings() {
        // Player 1 controls (arrows + Enter)
        CursorController.KeyBindings player1Keys =
                new CursorController.KeyBindings(
                        KeyConstants.UP, KeyConstants.DOWN,
                        KeyConstants.LEFT, KeyConstants.RIGHT,
                        KeyConstants.ENTER
                );
        setCursorController(0, cursor1, player1Keys);

        // Player 2 controls (WASD + Space)
        CursorController.KeyBindings player2Keys =
                new CursorController.KeyBindings(
                        KeyConstants.W, KeyConstants.S,
                        KeyConstants.A, KeyConstants.D,
                        KeyConstants.SPACE
                );
        setCursorController(1, cursor2, player2Keys);
    }

    /**
     * Attaches a {@link CursorController} for a player and
     * connects its action callback to {@link #onPlayerAction}.
     *
     * @param playerId      player ID (0 or 1)
     * @param playerCursor  player's cursor
     * @param keys          key bindings to control the cursor
     */
    private void setCursorController(int playerId, IPlayerCursor playerCursor, CursorController.KeyBindings keys) {
        CursorController cursorController = new CursorController(playerCursor, keys, keyManager);
        cursorController.setOnPlayerAction(pos -> {
            if (onPlayerAction != null) {
                onPlayerAction.accept(playerId, pos);
            }
        });
    }

    /**
     * Paints the board panel including:
     * <ul>
     *   <li>Player cursors</li>
     *   <li>Selection highlights for both players</li>
     *   <li>Legal move highlights</li>
     * </ul>
     *
     * @param g the graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw cursors
        cursor1.draw(g, getWidth(), getHeight());
        cursor2.draw(g, getWidth(), getHeight());

        // Draw selections and legal moves
        Graphics2D g2 = (Graphics2D) g;
        SelectionRenderer.draw(
                g2, selected1, legalMoves1, SELECT_COLOR_P1,
                board.getCols(), board.getRows(), getWidth(), getHeight()
        );
        SelectionRenderer.draw(
                g2, selected2, legalMoves2, SELECT_COLOR_P2,
                board.getCols(), board.getRows(), getWidth(), getHeight()
        );
    }
}
