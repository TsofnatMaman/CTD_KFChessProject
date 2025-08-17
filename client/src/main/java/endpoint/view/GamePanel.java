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
import sound.EventListener;
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
 * Clean GamePanel: handles UI layout, timer, background, player panels, boardPanel.
 * Purely visual and communication (sending selection to server), no game logic.
 */
public class GamePanel extends JPanel implements IEventListener {

    private final IGame model;
    private final BoardPanel boardPanel;

    private final Image backgroundImage;

    private final JLabel timerLabel;
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

        add(createPlayerInfoPanel(0), BorderLayout.WEST);
        add(createPlayerInfoPanel(1), BorderLayout.EAST);

        IPlayerCursor cursor = new PlayerCursor(new Position(0, 0), model.getPlayerById(playerId).getColor());

        boardPanel = new BoardPanel(model.getBoard(), playerId, cursor);
        boardPanel.setPreferredSize(new Dimension(700, 700));
        boardPanel.setOpaque(false);
        enableBoardFocus(boardPanel);

        boardPanel.setOnPlayerAction(this::sendPlayerSelection);

        add(boardPanel, BorderLayout.CENTER);

        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setOpaque(false);
        add(timerLabel, BorderLayout.NORTH);

        Timer uiTimer = new Timer(constants.GameConstants.UI_TIMER_MS, e -> updateTimer());
        uiTimer.start();

        LogUtils.logDebug("GamePanel initialized");

        EventPublisher.getInstance().subscribe(EGameEvent.GAME_ENDED, this);
        new EventListener();
    }

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

    private void enableBoardFocus(BoardPanel board) {
        board.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                board.requestFocusInWindow();
            }
        });
        SwingUtilities.invokeLater(board::requestFocusInWindow);
    }

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    @Override
    public void onEvent(GameEvent event) {
        JOptionPane.showMessageDialog(this, "Game Over. Winner: Player " + model.win().getName() +": "+ PlayerConstants.PIECES_COLOR[model.win().getId()].toString());
    }

    public BoardPanel getBoardPanel() { return boardPanel; }
    public IGame getModel() { return model; }
    public void setStartTimeNano(long startTimeNano) { this.startTimeNano = startTimeNano; }
}
