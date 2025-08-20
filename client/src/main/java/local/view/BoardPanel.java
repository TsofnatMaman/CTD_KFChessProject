package local.view;

import interfaces.IBoard;
import interfaces.IPlayerCursor;
import local.controller.Controller;
import pieces.Position;
import viewUtils.BaseBoardPanel;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class BoardPanel extends BaseBoardPanel {

    private final IPlayerCursor cursor1;
    private final IPlayerCursor cursor2;

    private Position selected1 = null;
    private List<Position> legalMoves1 = Collections.emptyList();

    private Position selected2 = null;
    private List<Position> legalMoves2 = Collections.emptyList();

    private final Color SELECT_COLOR_P1 = new Color(255, 0, 0, 128);
    private final Color SELECT_COLOR_P2 = new Color(0, 0, 255, 128);

    private Controller controller;

    public BoardPanel(IBoard board, IPlayerCursor cursor1, IPlayerCursor cursor2) {
        super(board);
        this.cursor1 = cursor1;
        this.cursor2 = cursor2;

        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                handleKey(e.getKeyCode());
            }
        });
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private void handleKey(int keyCode) {
        switch (keyCode) {
            case java.awt.event.KeyEvent.VK_UP -> cursor1.moveUp();
            case java.awt.event.KeyEvent.VK_DOWN -> cursor1.moveDown();
            case java.awt.event.KeyEvent.VK_LEFT -> cursor1.moveLeft();
            case java.awt.event.KeyEvent.VK_RIGHT -> cursor1.moveRight();
            case java.awt.event.KeyEvent.VK_ENTER -> {
                if (controller != null)
                    controller.handlePlayerMove(0, cursor1.getPosition());
            }

            case java.awt.event.KeyEvent.VK_W -> cursor2.moveUp();
            case java.awt.event.KeyEvent.VK_S -> cursor2.moveDown();
            case java.awt.event.KeyEvent.VK_A -> cursor2.moveLeft();
            case java.awt.event.KeyEvent.VK_D -> cursor2.moveRight();
            case java.awt.event.KeyEvent.VK_SPACE -> {
                if (controller != null)
                    controller.handlePlayerMove(1, cursor2.getPosition());
            }
        }

        repaint();
    }

    public void setSelected1(Position pos) { selected1 = pos; }
    public void setLegalMoves1(List<Position> moves) { legalMoves1 = moves; }
    public void setSelected2(Position pos) { selected2 = pos; }
    public void setLegalMoves2(List<Position> moves) { legalMoves2 = moves; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        cursor1.draw(g, getWidth(), getHeight());
        cursor2.draw(g, getWidth(), getHeight());

        Graphics2D g2 = (Graphics2D) g;
        int cellW = getWidth() / board.getCols();
        int cellH = getHeight() / board.getRows();

        if (selected1 != null) {
            g2.setColor(SELECT_COLOR_P1);
            g2.fillRect(selected1.getCol() * cellW, selected1.getRow() * cellH, cellW, cellH);
            for (Position move : legalMoves1) {
                int x = move.getCol() * cellW + cellW/4;
                int y = move.getRow() * cellH + cellH/4;
                int w = cellW/2, h = cellH/2;
                g2.fillOval(x, y, w, h);
            }
        }

        if (selected2 != null) {
            g2.setColor(SELECT_COLOR_P2);
            g2.fillRect(selected2.getCol() * cellW, selected2.getRow() * cellH, cellW, cellH);
            for (Position move : legalMoves2) {
                int x = move.getCol() * cellW + cellW/4;
                int y = move.getRow() * cellH + cellH/4;
                int w = cellW/2, h = cellH/2;
                g2.fillOval(x, y, w, h);
            }
        }
    }
}
