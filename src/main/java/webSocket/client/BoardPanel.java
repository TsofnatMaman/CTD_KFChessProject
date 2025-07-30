package webSocket.client;

import board.BoardConfig;
import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import game.IBoardView;
import interfaces.*;
import pieces.Position;
import utils.LogUtils;
import view.BoardRenderer;
import webSocket.server.dto.BoardDTO;
import webSocket.server.dto.PieceDTO;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Panel for displaying the game board and handling player input.
 * מעודכן לתמיכה בעדכון מצב מבחוץ (למשל מ-WebSocket).
 */
public class BoardPanel extends JPanel implements IBoardView, IEventListener {
    private BufferedImage boardImage;
    private IBoard board;  // אפשר לעדכן את הלוח לפי המצב שהתקבל

    private final IPlayerCursor cursor;
    private Consumer<Void> onPlayerAction;

    private Position selected = null;
    private List<Position> legalMoves = Collections.emptyList();


    private static final Color SELECT_COLOR_P = new Color(255, 0, 0, 128);   // אדום חצי שקוף

    public BoardPanel(IBoard board, IPlayerCursor pc) {
        this.board = board;
        this.cursor = pc;

        setPreferredSize(new Dimension(800, 800));
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

    private void handleKey(KeyEvent e) {
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
                    if (p == null || p.isCaptured() || board.getPlayerOf(p) != 0 || !p.getCurrentStateName().isCanAction()) {
                        LogUtils.logDebug("can not choose piece");
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

    // SETTERS לעדכון מבחוץ (למשל מהשרת)
    public void setSelectedForPlayer(Position selected) {
        this.selected = selected;
        repaint();
    }

    public void setLegalMovesForPlayer(Position pos) {
        legalMoves = board.getLegalMoves(pos);
        repaint();
    }

    /**
     * עדכון לוח לפי רשימת כלים שנשלחה מבחוץ (למשל מ-WebSocket).
     * כאן אפשר להחליף או לעדכן את ה-board או מודל הלוח בהתאם.
     * חשוב שה-board שלך תומך בעדכון כלים.
     */
    public void setBoard(IBoard newBoard) {
        this.board = newBoard;
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
        int cellW = getWidth() / board.getCOLS();
        int cellH = getHeight() / board.getROWS();

        // --- Player 1 selection and legal moves ---
        if (selected != null) {
            g2.setColor(SELECT_COLOR_P);
            g2.fillRect(selected.getCol() * cellW, selected.getRow() * cellH, cellW, cellH);

            g2.setColor(SELECT_COLOR_P);
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

    public void setBoardFromSnapshot(BoardDTO snapshot) {
        if (snapshot == null) return;

        // יצירת רשימת כלים מתוך תמונת מצב
        List<IPiece> pieces = Arrays.stream(snapshot.boardGrid)
                .flatMap(row -> Arrays.stream(row))     // משטחים את כל השורות
                .filter(Objects::nonNull)
                .map(PieceDTO::toPiece)
                .toList();          // אוספים לרשימה

        // יצירת לוח תצוגה צד לקוח
        this.board = new ClientSideBoard(snapshot.rows, snapshot.cols, pieces);
        repaint();
    }

    private static class ClientSideBoard implements IBoard {
        private final int rows;
        private final int cols;
        private final IPiece[][] grid;

        public ClientSideBoard(int rows, int cols, List<IPiece> pieces) {
            this.rows = rows;
            this.cols = cols;
            this.grid = new IPiece[rows][cols];
            for (IPiece p : pieces) {
                if (!p.isCaptured()) {
                    grid[p.getRow()][p.getCol()] = p;
                }
            }
        }

        @Override
        public IPiece getPiece(int row, int col) {
            if (row < 0 || row >= rows || col < 0 || col >= cols) return null;
            return grid[row][col];
        }

        @Override
        public IPiece getPiece(Position pos) {
            return getPiece(pos.getRow(), pos.getCol());
        }

        @Override public boolean hasPiece(int row, int col) { return getPiece(row, col) != null; }
        @Override public int getPlayerOf(int row) { return row < rows / 2 ? 0 : 1; }
        @Override public int getPlayerOf(Position pos) { return getPlayerOf(pos.getRow()); }
        @Override public int getPlayerOf(IPiece piece) { return getPlayerOf(piece.getRow()); }

        @Override public int getCOLS() { return cols; }
        @Override public int getROWS() { return rows; }

        // שאר הפונקציות לא רלוונטיות לצד לקוח, אז פשוט מחזירות ערכים ברירת מחדל
        @Override public void placePiece(IPiece piece) {}
        @Override public void move(Position from, Position to) {}
        @Override public void jump(IPiece p) {}
        @Override public void updateAll() {}
        @Override public boolean isInBounds(int r, int c) { return r >= 0 && r < rows && c >= 0 && c < cols; }
        @Override public boolean isInBounds(Position p) { return isInBounds(p.getRow(), p.getCol()); }
        @Override public boolean isMoveLegal(Position from, Position to) { return false; }
        @Override public boolean isPathClear(Position from, Position to) { return false; }
        @Override public boolean isJumpLegal(IPiece p) { return false; }
        @Override public IPlayer[] getPlayers() { return new IPlayer[0]; }
        @Override public BoardConfig getBoardConfig() { return null; }
        @Override public List<Position> getLegalMoves(Position selectedPosition) { return List.of(); }
    }


}
