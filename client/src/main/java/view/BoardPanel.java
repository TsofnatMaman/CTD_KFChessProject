package view;

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
 * Panel for displaying the game board and handling player input.
 * Updated to support state updates from external sources (e.g., WebSocket).
 */
public class BoardPanel extends JPanel implements IBoardView, IEventListener {
    private BufferedImage boardImage;
    private final IBoard board;
    private final int playerId;

    private final IPlayerCursor cursor;

    private Consumer<Void> onPlayerAction;

    private Position selected = null;
    private List<Position> legalMoves = Collections.emptyList();


    private final Color SELECT_COLOR; //new Color(255, 0, 0, 128);   // semi-transparent red

    public BoardPanel(IBoard board, int playerId, IPlayerCursor pc) {
        this.board = board;
        this.cursor = pc;
        this.playerId = playerId;
        Color base = cursor.getColor();
        SELECT_COLOR = new Color(base.getRed(), base.getGreen(), base.getBlue(), 128);

        setPreferredSize(new Dimension(constants.GameConstants.BOARD_SIZE * constants.GameConstants.SQUARE_SIZE,
                                      constants.GameConstants.BOARD_SIZE * constants.GameConstants.SQUARE_SIZE)); // extracted board size
        setFocusable(true);
        loadBoardImage();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e);
            }
        });
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_UPDATE, this);
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

    public void handleKey(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_UP -> cursor.moveUp();
            case KeyEvent.VK_DOWN -> cursor.moveDown();
            case KeyEvent.VK_LEFT -> cursor.moveLeft();
            case KeyEvent.VK_RIGHT -> cursor.moveRight();
            case KeyEvent.VK_ENTER -> {
                Position pos = cursor.getPosition();
                if (selected == null) {
                    IPiece p = board.getPiece(pos);
                    if (p == null || p.getPlayer() != playerId || p.isCaptured() || !p.getCurrentStateName().isCanAction()) {
                        System.out.println("can not choose piece");
                    } else {
                        selected = pos;
                        legalMoves = board.getLegalMoves(pos);
                    }
                } else {
                    selected = null;
                    legalMoves = Collections.emptyList();
                }
                if (onPlayerAction != null) onPlayerAction.accept(null);
                repaint();
            }
        }

        repaint();
    }

    // SETTERS for external updates (e.g., from server)
    public void setSelectedForPlayer(int playerId, Position selected) {
        this.selected = selected;
        repaint();
    }

    public void setLegalMovesForPlayer(int playerId, List<Position> moves) {
        legalMoves = moves;
        repaint();
    }

    public void setOnPlayerAction(Consumer<Void> handler) {
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

        if (board != null)
            BoardRenderer.draw(g, board, getWidth(), getHeight());

        if (cursor != null) cursor.draw(g, getWidth(), getHeight());

        Graphics2D g2 = (Graphics2D) g;
        int cellW = getWidth() / constants.GameConstants.BOARD_COLS; // extracted board size
        int cellH = getHeight() / constants.GameConstants.BOARD_ROWS; // extracted board size

        // --- Player 1 selection and legal moves ---
        if (selected != null) {
            g2.setColor(SELECT_COLOR);
            g2.fillRect(selected.getCol() * cellW, selected.getRow() * cellH, cellW, cellH);

            g2.setColor(SELECT_COLOR);
            for (Position move : legalMoves) {
                int x = move.getCol() * cellW + cellW / 4;
                int y = move.getRow() * cellH + cellH / 4;
                int w = cellW / 2;
                int h = cellH / 2;
                g2.fillOval(x, y, w, h);
            }
        }

    }

    public IPlayerCursor getPlayerCursor() {
        return cursor;
    }

    @Override
    public void onEvent(GameEvent event) {
        repaint();
    }
}
