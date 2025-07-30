package webSocket.client;

import board.BoardRenderer;
import events.GameEvent;
import events.IEventListener;
import interfaces.IBoard;
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
 * Panel לתצוגת לוח לשחקן אחד.
 * מקבל עדכונים מבחוץ (לוח, בחירה, מהלכים חוקיים).
 * תומך בקלט דרך מקשי החצים ובחירת כלי.
 */
public class SinglePlayerBoardPanel extends JPanel implements IEventListener {

    private BufferedImage boardImage;
    private IBoard board;
    private final IPlayerCursor cursor;
    private Consumer<Position> onAction;  // לקריאות בחירה חזרה ללקוח/שרת

    private Position selected = null;
    private List<Position> legalMoves = Collections.emptyList();

    private final int playerId;

    // צבעים לבחירה
    private static final Color SELECT_COLOR = new Color(255, 0, 0, 128); // אדום שקוף

    public SinglePlayerBoardPanel(IBoard board, IPlayerCursor cursor, int playerId) {
        this.board = board;
        this.cursor = cursor;
        this.playerId = playerId;

        setPreferredSize(new Dimension(800, 800));
        setFocusable(true);

        loadBoardImage();

        // מאזין ללחיצות מקשים למעבר ועבודה עם הלוח
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e);
            }
        });

        // רישום לקבלת עדכונים (אם יש לך מערכת אירועים)
        // אפשר להתאים לפי המערכת שלך
        // EventPublisher.getInstance().subscribe(EGameEvent.GAME_UPDATE, this);
    }

    private void loadBoardImage() {
        try {
            URL url = getClass().getClassLoader().getResource("board/board.png");
            if (url != null) {
                boardImage = ImageIO.read(url);
            } else {
                LogUtils.logDebug("Image board/board.png not found!");
            }
        } catch (IOException e) {
            LogUtils.logDebug("Error loading board image: " + e.getMessage());
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
                    // בוחרים כלי אם שייך לשחקן
                    if (board.getPlayerOf(board.getPiece(pos)) == playerId) {
                        selected = pos;
                        legalMoves = board.getLegalMoves(pos);
                    } else {
                        LogUtils.logDebug("Cannot select this piece");
                    }
                } else {
                    // אם כבר נבחר כלי, ניקוי הבחירה
                    selected = null;
                    legalMoves = Collections.emptyList();
                }
                if (onAction != null) onAction.accept(selected);
            }
        }
        repaint();
    }

    // פונקציות לעדכון מבחוץ (לדוגמה מהשרת)

    /**
     * עדכון הלוח
     */
    public void setBoard(IBoard board) {
        this.board = board;
        repaint();
    }

    /**
     * עדכון מיקום הבחירה של השחקן
     */
    public void setSelected(Position selected) {
        this.selected = selected;
        repaint();
    }

    /**
     * עדכון רשימת המהלכים החוקיים
     */
    public void setLegalMoves(List<Position> legalMoves) {
        this.legalMoves = legalMoves;
        repaint();
    }

    /**
     * הגדרת callback שיקבל את מיקום הבחירה (לשליחה לשרת)
     */
    public void setOnAction(Consumer<Position> onAction) {
        this.onAction = onAction;
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

        if (board != null) {
            // ציור הלוח והכלים - יש להוסיף את המתודה המתאימה ב-BoardRenderer
            BoardRenderer.draw(g, board, getWidth(), getHeight());
        }

        // ציור הסמן
        if (cursor != null) {
            cursor.draw(g, getWidth(), getHeight());
        }

        // ציור הבחירה והמהלכים החוקיים
        if (selected != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            int cellW = getWidth() / board.getCOLS();
            int cellH = getHeight() / board.getROWS();

            g2.setColor(SELECT_COLOR);
            g2.fillRect(selected.getCol() * cellW, selected.getRow() * cellH, cellW, cellH);

            for (Position move : legalMoves) {
                int x = move.getCol() * cellW + cellW / 4;
                int y = move.getRow() * cellH + cellH / 4;
                int w = cellW / 2;
                int h = cellH / 2;
                g2.fillOval(x, y, w, h);
            }
            g2.dispose();
        }
    }

    @Override
    public void onEvent(GameEvent event) {
        repaint();
    }
}
