package endpoint.view;

import interfaces.IBoard;
import interfaces.IPlayerCursor;
import pieces.Position;
import viewUtils.BaseBoardPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Pure UI panel: displays board, pieces, cursor, selection and legal moves.
 * Receives all updates from external sources (controller).
 */
public class BoardPanel extends BaseBoardPanel {
    private final IPlayerCursor cursor;

    private Position selected = null;
    private List<Position> legalMoves = Collections.emptyList();
    private final Color selectColor;

    private Consumer<Position> onPlayerAction; // returns selected cursor position

    public BoardPanel(IBoard board, IPlayerCursor cursor) {
        super(board);
        this.cursor = cursor;

        Color base = cursor.getColor();
        selectColor = new Color(base.getRed(), base.getGreen(), base.getBlue(), 128);
    }

    public void initKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        im.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        im.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        im.put(KeyStroke.getKeyStroke("ENTER"), "select");

        am.put("moveUp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { cursor.moveUp(); repaint(); }
        });
        am.put("moveDown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { cursor.moveDown(); repaint(); }
        });
        am.put("moveLeft", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { cursor.moveLeft(); repaint(); }
        });
        am.put("moveRight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { cursor.moveRight(); repaint(); }
        });
        am.put("select", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { if(onPlayerAction!=null) onPlayerAction.accept(cursor.getPosition()); }
        });
    }

    public void setOnPlayerAction(Consumer<Position> handler) {
        this.onPlayerAction = handler;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        cursor.draw(g, getWidth(), getHeight());

        if (selected != null) {
            Graphics2D g2 = (Graphics2D) g;
            int cellW = getWidth() / constants.GameConstants.BOARD_COLS;
            int cellH = getHeight() / constants.GameConstants.BOARD_ROWS;

            g2.setColor(selectColor);
            g2.fillRect(selected.getCol() * cellW, selected.getRow() * cellH, cellW, cellH);

            for (Position move : legalMoves) {
                int x = move.getCol() * cellW + cellW / 4;
                int y = move.getRow() * cellH + cellH / 4;
                int w = cellW / 2;
                int h = cellH / 2;
                g2.fillOval(x, y, w, h);
            }
        }
    }

    public void setSelected(Position selected) {
        this.selected = selected;
    }

    public void setLegalMoves(List<Position> legalMoves) {
        this.legalMoves = legalMoves;
    }

    public void clearSelection() { setSelected(null); setLegalMoves(Collections.emptyList()); repaint(); }
}
