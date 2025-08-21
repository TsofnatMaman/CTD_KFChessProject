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
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Pure UI panel: displays board, pieces, cursor, selection and legal moves.
 * Receives all updates from external sources (controller).
 */
public class BoardPanel extends BaseBoardPanel {
    private final IPlayerCursor cursor;
    private final KeyManager keyManager;

    private Position selected = null;
    private List<Position> legalMoves = Collections.emptyList();
    private final Color selectColor;

    private Consumer<Position> onPlayerAction; // returns selected cursor position

    public BoardPanel(IBoard board, IPlayerCursor cursor) {
        super(board);
        this.cursor = cursor;

        keyManager = new KeyManager(this);

        initKeyBindings();

        Color base = cursor.getColor();
        selectColor = new Color(base.getRed(), base.getGreen(), base.getBlue(), 128);
    }

    public void initKeyBindings() {
        // Controller for player  (arrows + Enter)
        CursorController.KeyBindings playerKey = new CursorController.KeyBindings(KeyConstants.UP, KeyConstants.DOWN, KeyConstants.LEFT, KeyConstants.RIGHT, KeyConstants.ENTER);

        CursorController cursorController = new CursorController(cursor, playerKey, keyManager);
        cursorController.setOnPlayerAction(pos -> {
            if(onPlayerAction != null)
                onPlayerAction.accept(pos);
        });
    }

    public void setOnPlayerAction(Consumer<Position> handler) {
        this.onPlayerAction = handler;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        cursor.draw(g, getWidth(), getHeight());

        Graphics2D g2 = (Graphics2D) g;
        SelectionRenderer.draw(g2, selected, legalMoves, selectColor, board.getCols(), board.getRows(), getWidth(), getHeight());
    }

    public void setSelected(Position selected) {
        this.selected = selected;
    }

    public void setLegalMoves(List<Position> legalMoves) {
        this.legalMoves = legalMoves;
    }

    public void clearSelection() { setSelected(null); setLegalMoves(Collections.emptyList()); repaint(); }
}
