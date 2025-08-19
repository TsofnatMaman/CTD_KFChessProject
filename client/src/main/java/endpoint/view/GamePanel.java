package endpoint.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import constants.PlayerConstants;
import dto.EventType;
import dto.Message;
import dto.PlayerSelectedDTO;
import endpoint.launch.ChessClientEndpoint;
import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import interfaces.IGame;
import interfaces.IPlayerCursor;
import pieces.Position;
import player.PlayerCursor;
import sound.EventSoundListener;
import utils.LogUtils;
import viewUtils.PlayerInfoPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;

/**
 * GamePanel אחראי על הצגת הלוח, שני שחקנים, הטיימר והרקע.
 * הקוד מסודר לפונקציות קטנות ונקי, עם שני PlayerInfoPanel.
 */
public class GamePanel extends JPanel implements IEventListener {

    private final IGame model;
    private BoardPanel boardPanel;
    private final Image backgroundImage;
    private JLabel timerLabel;
    private long startTimeNano;

    private final ChessClientEndpoint client;
    private final ObjectMapper mapper;
    private final int playerId;

    public GamePanel(IGame model, int playerId, ChessClientEndpoint client, ObjectMapper mapper) {
        this.model = model;
        this.client = client;
        this.mapper = mapper;
        this.playerId = playerId;

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        backgroundImage = loadBackgroundImage("background/background.jpg");

        initUI();
        initTimer();
        subscribeEvents();

        LogUtils.logDebug("GamePanel initialized");
    }

    // ----------- INIT METHODS -----------

    private void initUI() {
        // שני פאנלים של השחקנים: מזרח ומערב
        add(createPlayerInfoPanel(0), BorderLayout.WEST);
        add(createPlayerInfoPanel(1), BorderLayout.EAST);

        IPlayerCursor cursor = new PlayerCursor(
                new Position(0,0),
                model.getPlayerById(playerId).getColor()
        );

        boardPanel = new BoardPanel(model.getBoard(), playerId, cursor);
        boardPanel.setPreferredSize(new Dimension(700, 700));
        boardPanel.setOpaque(false);
        enableBoardFocus(boardPanel);
        boardPanel.setOnPlayerAction(this::sendPlayerSelection);

        add(boardPanel, BorderLayout.CENTER);

        timerLabel = createTimerLabel();
        add(timerLabel, BorderLayout.NORTH);
    }

    private void initTimer() {
        Timer uiTimer = new Timer(constants.GameConstants.UI_TIMER_MS, e -> updateTimer());
        uiTimer.start();
    }

    private void subscribeEvents() {
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_ENDED, this);
        new EventSoundListener();
    }

    // ----------- COMPONENT CREATORS -----------

    private Image loadBackgroundImage(String path) {
        try {
            return ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(path)));
        } catch (IOException | IllegalArgumentException e) {
            LogUtils.logDebug("Could not load background image: " + e.getMessage());
            return null;
        }
    }

    private PlayerInfoPanel createPlayerInfoPanel(int pid) {
        PlayerInfoPanel panel = new PlayerInfoPanel(model.getPlayerById(pid));
        panel.setBackground(new Color(255, 255, 255, 180));
        return panel;
    }

    private JLabel createTimerLabel() {
        JLabel label = new JLabel("Time: 00:00");
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private void enableBoardFocus(BoardPanel board) {
        board.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                board.requestFocusInWindow();
            }
        });
        SwingUtilities.invokeLater(board::requestFocusInWindow);
    }

    // ----------- GAME ACTIONS -----------

    private void sendPlayerSelection(Position pos) {
        try {
            PlayerSelectedDTO cmd = new PlayerSelectedDTO(playerId, pos);
            Message<PlayerSelectedDTO> msg = new Message<>(EventType.PLAYER_SELECTED, cmd);
            client.sendText(mapper.writeValueAsString(msg));
        } catch (Exception e) {
            LogUtils.logDebug("Failed to send player selection: " + e.getMessage());
        }
    }

    private void updateTimer() {
        long elapsedMillis = (System.nanoTime() - startTimeNano) / 1_000_000;
        int seconds = (int) (elapsedMillis / 1000) % 60;
        int minutes = (int) (elapsedMillis / (1000 * 60));
        timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
    }

    // ----------- OVERRIDES -----------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    @Override
    public void onEvent(GameEvent event) {
        JOptionPane pane = new JOptionPane(
                "Game Over. Winner: Player " + model.win().getName() + ": " +
                        PlayerConstants.COLORS_NAME[model.win().getId()],
                JOptionPane.INFORMATION_MESSAGE
        );
        JDialog dialog = pane.createDialog(this, "Game Over");
        dialog.setModal(false);
        dialog.setVisible(true);
    }

    // ----------- GETTERS & SETTERS -----------

    public BoardPanel getBoardPanel() { return boardPanel; }
    public IGame getModel() { return model; }
    public void setStartTimeNano(long startTimeNano) { this.startTimeNano = startTimeNano; }
}
