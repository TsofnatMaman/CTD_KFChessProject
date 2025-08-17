package endpoint.view;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import game.IBoardView;
import interfaces.IBoard;
import interfaces.IPiece;
import interfaces.IPlayerCursor;
import pieces.Position;
import utils.LogUtils;
import viewUtils.BoardRenderer;

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

/**
 * Pure UI panel: displays board, pieces, cursor, selection and legal moves.
 * Receives all updates from external sources (controller).
 */
public class BoardPanel extends JPanel implements IBoardView, IEventListener {

    private BufferedImage boardImage;
    private final IBoard board;
    private final IPlayerCursor cursor;

    private Position selected = null;
    private List<Position> legalMoves = Collections.emptyList();
    private final Color selectColor;
    private final int playerId;

    private Consumer<Position> onPlayerAction; // returns selected cursor position

    public BoardPanel(IBoard board, int playerId, IPlayerCursor cursor) {
        this.board = board;
        this.cursor = cursor;
        this.playerId = playerId;

        Color base = cursor.getColor();
        selectColor = new Color(base.getRed(), base.getGreen(), base.getBlue(), 128);

        setPreferredSize(new Dimension(
                constants.GameConstants.BOARD_SIZE * constants.GameConstants.SQUARE_SIZE,
                constants.GameConstants.BOARD_SIZE * constants.GameConstants.SQUARE_SIZE
        ));
        setFocusable(true);

        loadBoardImage();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e);
            }
        });

        EventPublisher.getInstance().subscribe(EGameEvent.GAME_UPDATE, this);
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_ENDED, this);

    }

    private void loadBoardImage() {
        try {
            URL imageUrl = getClass().getClassLoader().getResource("board/board.png");
            if (imageUrl != null) {
                boardImage = ImageIO.read(imageUrl);
            } else {
                LogUtils.logDebug("Board image not found in resources!");
            }
        } catch (IOException e) {
            LogUtils.logDebug("Exception loading board image: " + e.getMessage());
        }
    }

    private void handleKey(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> cursor.moveUp();
            case KeyEvent.VK_DOWN -> cursor.moveDown();
            case KeyEvent.VK_LEFT -> cursor.moveLeft();
            case KeyEvent.VK_RIGHT -> cursor.moveRight();
            case KeyEvent.VK_ENTER -> {
                Position pos = cursor.getPosition();
                if (selected == null) {
                    IPiece p = board.getPiece(pos);
                    if (p == null || p.isCaptured() || board.getPlayerOf(p) != playerId || !p.getCurrentStateName().isCanAction()) {
                        LogUtils.logDebug("can not choose piece");
                    } else {
                        selected = pos.copy();
                        legalMoves = board.getLegalMoves(pos);
                    }
                } else {
                    selected = null;
                    legalMoves = Collections.emptyList();
                }
                if (onPlayerAction != null) onPlayerAction.accept(pos);
                repaint();
            }
        }
        repaint();
    }

    public void setOnPlayerAction(Consumer<Position> handler) {
        this.onPlayerAction = handler;
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

        BoardRenderer.draw(g, board, getWidth(), getHeight());
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

    @Override
    public void onEvent(GameEvent event) {
        repaint();
    }
}
