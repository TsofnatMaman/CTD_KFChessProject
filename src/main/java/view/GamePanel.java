package view;

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

/**
 * Full game panel with board in center, players on sides, and background.
 */
public class GamePanel extends JPanel implements IEventListener {
    private final BoardPanel boardPanel;
    private final PlayerInfoPanel player1Panel;
    private final PlayerInfoPanel player2Panel;
    private final IGame model;
    private Image backgroundImage;

    private JLabel timerLabel;
    private Timer timerForUI;

    public GamePanel(IGame model){
        this.model = model;

        // Set layout with gaps between regions
        setLayout(new BorderLayout(20, 20)); // Spacing between center and sides
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Internal padding from edges

        // Load background image
        try {
            backgroundImage = ImageIO.read(getClass().getClassLoader().getResource("background/background.jpg"));
        } catch (IOException | IllegalArgumentException e) {
            LogUtils.logDebug("Could not load background image: " + e.getMessage());
        }

        // Players info panels
        player1Panel = new PlayerInfoPanel(model.getPlayerById(0));
        player2Panel = new PlayerInfoPanel(model.getPlayerById(1));

        Color semiTransparent = new Color(255, 255, 255, 180);
        player1Panel.setBackground(semiTransparent);
        player2Panel.setBackground(semiTransparent);

        // Board
        IPlayerCursor c1 = new PlayerCursor(new Position(0,0), model.getPlayerById(0).getColor());
        IPlayerCursor c2 = new PlayerCursor(new Position(7,7),model.getPlayerById(1).getColor());

        boardPanel = new BoardPanel(model.getBoard(), c1, c2);
        boardPanel.setPreferredSize(new Dimension(700, 700));
        boardPanel.setOpaque(false);

        // Events
        boardPanel.setOnPlayer1Action((v) -> model.handleSelection(model.getPlayerById(0), c1.getPosition()));
        boardPanel.setOnPlayer2Action((v) -> model.handleSelection(model.getPlayerById(1), c2.getPosition()));

        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boardPanel.requestFocusInWindow();
            }
        });

        SwingUtilities.invokeLater(() -> boardPanel.requestFocusInWindow());

        // Layout
        add(player1Panel, BorderLayout.WEST);
        add(player2Panel, BorderLayout.EAST);
        add(boardPanel, BorderLayout.CENTER);

        // --- הוספת תצוגת טיימר מעל הלוח ---
        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setOpaque(false);
        add(timerLabel, BorderLayout.NORTH);

        timerForUI = new Timer(1000, e -> updateTimer());
        timerForUI.start();
        // ---------------------------------------

        LogUtils.logDebug("Initial game state setup");

        EventPublisher.getInstance().subscribe(EGameEvent.GAME_ENDED, this);
    }

    public void run() {
        model.run();
    }

    private void updateTimer() {
        long elapsedMillis = model.getElapsedTimeNano();
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
}
