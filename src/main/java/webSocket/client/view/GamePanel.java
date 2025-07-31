package webSocket.client.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import interfaces.IGame;
import interfaces.IPlayerCursor;
import pieces.Position;
import player.PlayerCursor;
import utils.LogUtils;
import webSocket.client.ChessClientEndpoint;
import webSocket.server.dto.PlayerSelected;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;

/**
 * ממשק המשחק הכולל: לוח במרכז, מידע שחקנים בצדדים, רקע וטיימר.
 * מקבל מודל משחק מלא שמתחיל מהשרת.
 */
public class GamePanel extends JPanel implements IEventListener {

    private final BoardPanel boardPanel;
    private final IGame model;
    private Image backgroundImage;

    private final JLabel timerLabel;
    private final Timer timerForUI;

    private long startTimeNano;

    private final ObjectMapper mapper;
    private final ChessClientEndpoint client;

    public GamePanel(IGame model, int playerId, ChessClientEndpoint client, ObjectMapper mapper) {
        this.model = model;
        this.client = client;
        this.mapper = mapper;

        // הגדרות פריסה ומרווחים
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // טעינת תמונת רקע
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("background/background.jpg")));
        } catch (IOException | IllegalArgumentException e) {
            LogUtils.logDebug("Could not load background image: " + e.getMessage());
        }

        // יצירת פאנל מידע שחקן בצד שמאל עם השחקן המקומי
        PlayerInfoPanel playerPanel = new PlayerInfoPanel(model.getPlayerById(playerId));
        Color semiTransparent = new Color(255, 255, 255, 180);
        playerPanel.setBackground(semiTransparent);

        IPlayerCursor cursor = new PlayerCursor(new Position(0, 0), model.getPlayerById(playerId).getColor());

        // יצירת לוח המשחק עם הסמן
        boardPanel = new BoardPanel(model.getBoard(), playerId, cursor);
        boardPanel.setPreferredSize(new Dimension(700, 700));
        boardPanel.setOpaque(false);

        // הגדרת מאזין לפעולות השחקן - העברת הבחירה למודל המשחק
        boardPanel.setOnPlayerAction((v) -> {
            try {
                Position pos = cursor.getPosition();
                PlayerSelected cmd = new PlayerSelected(playerId, pos);
                String jsonCmd = mapper.writeValueAsString(cmd);
                client.sendText(jsonCmd);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // הבטחת לוח הפוקוס ללוח בעת לחיצה
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boardPanel.requestFocusInWindow();
            }
        });

        SwingUtilities.invokeLater(boardPanel::requestFocusInWindow);

        // הוספת פאנלים לפריסה
        add(playerPanel, BorderLayout.WEST);
        add(boardPanel, BorderLayout.CENTER);

        // יצירת תצוגת טיימר מעל הלוח
        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setOpaque(false);
        add(timerLabel, BorderLayout.NORTH);

        timerForUI = new Timer(1000, e -> updateTimer());
        timerForUI.start();

        LogUtils.logDebug("GamePanel initialized");

        // הרשמה לאירוע סיום המשחק
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_ENDED, this);
    }

    private void updateTimer() {
        long nowNano = System.nanoTime();
        long elapsedNanos = nowNano - startTimeNano;  // הזמן שחלף
        long elapsedMillis = elapsedNanos / 1_000_000; // ממיר לננו-שניות למילישניות

        int seconds = (int) (elapsedMillis / 1000) % 60;
        int minutes = (int) (elapsedMillis / (1000 * 60));

        timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    @Override
    public void onEvent(GameEvent event) {
        timerForUI.stop();
        JOptionPane.showMessageDialog(this, "Game Over. Winner: Player " + model.win().getName());
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    public IGame getModel() {
        return model;
    }

    public void setStartTimeNano(long startTimeNano) {
        this.startTimeNano = startTimeNano;
    }
}
