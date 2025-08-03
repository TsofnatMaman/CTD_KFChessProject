package view;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Message;
import dto.PlayerSelected;
import endpoint.ChessClientEndpoint;
import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import interfaces.IGame;
import interfaces.IPlayerCursor;
import pieces.Position;
import player.PlayerCursor;
import utils.LogUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;

/**
 * Main game interface: board in the center, player info on the sides, background and timer.
 * Receives a full game model initialized from the server.
 */
public class GamePanel extends JPanel implements IEventListener {

    private final BoardPanel boardPanel;
    private final IGame model;
    private Image backgroundImage;

    private final JLabel timerLabel;
    private final Timer timerForUI;

    private long startTimeNano;

    public GamePanel(IGame model, int playerId, ChessClientEndpoint client, ObjectMapper mapper) {
        this.model = model;

        // Layout and spacing settings
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Load background image
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("background/background.jpg")));
        } catch (IOException | IllegalArgumentException e) {
            LogUtils.logDebug("Could not load background image: " + e.getMessage());
        }

        // Create player info panel on the left with the local player
        PlayerInfoPanel playerPanel1 = new PlayerInfoPanel(model.getPlayerById(0));
        PlayerInfoPanel playerPanel2 = new PlayerInfoPanel(model.getPlayerById(1));

        Color semiTransparent = new Color(255, 255, 255, 180);
        playerPanel1.setBackground(semiTransparent);

        IPlayerCursor cursor = new PlayerCursor(new Position(0, 0), model.getPlayerById(playerId).getColor());

        // Create the game board with the cursor
        boardPanel = new BoardPanel(model.getBoard(), playerId, cursor);
        boardPanel.setPreferredSize(new Dimension(700, 700));
        boardPanel.setOpaque(false);

        // Set listener for player actions - send selection to game model
        boardPanel.setOnPlayerAction((v) -> {
            try {
                Position pos = cursor.getPosition();
                PlayerSelected cmd = new PlayerSelected(playerId, pos);

                Message<PlayerSelected> msg = new Message<>(constants.CommandNames.PLAYER_SELECTED, cmd); // extracted message type
                String jsonCmd = mapper.writeValueAsString(msg);

                client.sendText(jsonCmd);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });


        // Ensure board gets focus on click
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boardPanel.requestFocusInWindow();
            }
        });

        SwingUtilities.invokeLater(boardPanel::requestFocusInWindow);

        // Add panels to layout
        add(playerPanel1, BorderLayout.WEST);
        add(playerPanel2, BorderLayout.EAST);

        add(boardPanel, BorderLayout.CENTER);

        // Create timer display above the board
        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setOpaque(false);
        add(timerLabel, BorderLayout.NORTH);

        timerForUI = new Timer(constants.GameConstants.UI_TIMER_MS, e -> updateTimer());
        timerForUI.start();

        LogUtils.logDebug("GamePanel initialized");

        // Subscribe to game end event
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_ENDED, this);
    }

    private void updateTimer() {
        long nowNano = System.nanoTime();
        long elapsedNanos = nowNano - startTimeNano;  // elapsed time
        long elapsedMillis = elapsedNanos / 1_000_000; // convert nanoseconds to milliseconds

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
