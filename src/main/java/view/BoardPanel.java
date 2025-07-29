package view;

import game.IBoardView;
import interfaces.*;
import board.BoardRenderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import pieces.Position;
import utils.LogUtils;

/**
 * Panel for displaying the game board and handling player input.
 */
public class BoardPanel extends JPanel implements IBoardView {
    private BufferedImage boardImage;
    private final IBoard board;

    private final IPlayerCursor cursor1;
    private final IPlayerCursor cursor2;

    private Consumer<Void> onPlayer1Action;
    private Consumer<Void> onPlayer2Action;

    private Position selected1 = null;
    private List<Position> legalMoves1 = Collections.emptyList();

    private Position selected2 = null;
    private List<Position> legalMoves2 = Collections.emptyList();

    private static final Color SELECT_COLOR_P1 = new Color(255, 0, 0, 128);   // אדום חצי שקוף
    private static final Color SELECT_COLOR_P2 = new Color(0, 0, 255, 128);   // כחול חצי שקוף


    public BoardPanel(IBoard board, IPlayerCursor pc1, IPlayerCursor pc2) {
        this.board = board;
        this.cursor1 = pc1;
        this.cursor2 = pc2;

        setPreferredSize(new Dimension(800, 800));
        setFocusable(true);
        loadBoardImage();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e);
            }
        });
    }

    private void loadBoardImage() {
        try {
            URL imageUrl = getClass().getClassLoader().getResource("board/board.png");
            if (imageUrl != null) {
                boardImage = ImageIO.read(imageUrl);
            } else {
                System.err.println("Image not found in resources!");
                LogUtils.logDebug("Image not found in resources!");
            }
        } catch (IOException e) {
            String mes = "Exception loading board image: " + e.getMessage();
            LogUtils.logDebug(mes);
            throw new RuntimeException(mes);
        }
    }

    private void handleKey(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_UP -> cursor1.moveUp();
            case KeyEvent.VK_DOWN -> cursor1.moveDown();
            case KeyEvent.VK_LEFT -> cursor1.moveLeft();
            case KeyEvent.VK_RIGHT -> cursor1.moveRight();
            case KeyEvent.VK_ENTER -> {
                Position pos = cursor1.getPosition();
                if (selected1 == null) {
                    IPiece p = board.getPiece(pos);
                    if (p == null || p.isCaptured() || board.getPlayerOf(p) != 0 || !p.getCurrentStateName().isCanAction()) {
                        LogUtils.logDebug("can not choose piece");
                    } else {
                        selected1 = pos;
                        legalMoves1 = board.getLegalMoves(pos);
                    }
                } else {
                    selected1 = null;
                    legalMoves1 = Collections.emptyList();
                }
                if (onPlayer1Action != null) onPlayer1Action.accept(null);
                repaint();
            }
            }

            switch (key) {
            case KeyEvent.VK_W -> cursor2.moveUp();
            case KeyEvent.VK_S -> cursor2.moveDown();
            case KeyEvent.VK_A -> cursor2.moveLeft();
            case KeyEvent.VK_D -> cursor2.moveRight();
            case KeyEvent.VK_SPACE -> {
                Position pos = cursor2.getPosition();
                if (selected2 == null) {
                    IPiece p = board.getPiece(pos);
                    if (p == null || p.isCaptured() || board.getPlayerOf(p) != 1 || !p.getCurrentStateName().isCanAction()) {
                        System.err.println("can not choose piece");
                        LogUtils.logDebug("can not choose piece");
                    } else {
                        selected2 = pos;
                        legalMoves2 = board.getLegalMoves(pos);
                    }
                } else {
                    selected2 = null;
                    legalMoves2 = Collections.emptyList();
                }
                if (onPlayer2Action != null) onPlayer2Action.accept(null);
            }
        }

        repaint();
    }

    public void setOnPlayer1Action(Consumer<Void> handler) {
        this.onPlayer1Action = handler;
    }

    public void setOnPlayer2Action(Consumer<Void> handler) {
        this.onPlayer2Action = handler;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (boardImage != null) {
            g.drawImage(boardImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        if (board != null)
            BoardRenderer.draw(g, board, getWidth(), getHeight());

        if (cursor1 != null) cursor1.draw(g, getWidth(), getHeight());
        if (cursor2 != null) cursor2.draw(g, getWidth(), getHeight());

        Graphics2D g2 = (Graphics2D) g;
        int cellW = getWidth() / board.getCOLS();
        int cellH = getHeight() / board.getROWS();

        // --- Player 1 selection and legal moves ---
        if (selected1 != null) {
            g2.setColor(SELECT_COLOR_P1);
            g2.fillRect(selected1.getCol() * cellW, selected1.getRow() * cellH, cellW, cellH);

            g2.setColor(SELECT_COLOR_P1);
            for (Position move : legalMoves1) {
                int x = move.getCol() * cellW + cellW / 4;
                int y = move.getRow() * cellH + cellH / 4;
                int w = cellW / 2;
                int h = cellH / 2;
                g2.fillOval(x, y, w, h);
            }
        }

        // --- Player 2 selection and legal moves ---
        if (selected2 != null) {
            g2.setColor(SELECT_COLOR_P2);
            g2.fillRect(selected2.getCol() * cellW, selected2.getRow() * cellH, cellW, cellH);

            g2.setColor(SELECT_COLOR_P2);
            for (Position move : legalMoves2) {
                int x = move.getCol() * cellW + cellW / 4;
                int y = move.getRow() * cellH + cellH / 4;
                int w = cellW / 2;
                int h = cellH / 2;
                g2.fillOval(x, y, w, h);
            }
        }
    }

}
