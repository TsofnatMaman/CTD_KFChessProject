package viewUtils;

import constants.PlayerConstants;
import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import events.listeners.ActionData;
import interfaces.IPlayer;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for displaying player information such as name, score, and moves.
 */
public class PlayerInfoPanel extends JPanel implements IEventListener {
    private final IPlayer player;
    private final JLabel scoreLabel;
    private final JTextArea movesArea;

    /**
     * Constructs the player info panel and initializes UI components.
     */
    public PlayerInfoPanel(IPlayer player) {

        this.player = player;

        setLayout(new BorderLayout(5,5));
        setPreferredSize(new Dimension(200, 0));

        JLabel nameLabel = new JLabel(player.getName());
        nameLabel.setOpaque(true); // Important so the color is also shown as background
        nameLabel.setBackground(PlayerConstants.PIECES_COLOR[player.getId()]);
        nameLabel.setForeground(PlayerConstants.PLAYER_COLORS[player.getId()]); // You can choose a contrasting text color
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER); // Looks better centered

        scoreLabel = new JLabel("Score: "+player.getScore());

        movesArea = new JTextArea(10, 15);
        movesArea.setEditable(false);
        movesArea.setLineWrap(true);
        movesArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(movesArea);

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(nameLabel);
        topPanel.add(scoreLabel);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // MovesLogger.subscribe(this, player.getId()); --> onEvent: addMove(((ActionData)event.data).message);

        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_START_MOVED, this);
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_JUMP, this);
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_STARTED, this);
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_CAPTURED, this);
    }

    @Override
    public void onEvent(GameEvent event) {
        if(event.data() instanceof ActionData) {
            if (player.getId() == ((ActionData) event.data()).playerId())
                addMove(((ActionData) event.data()).message());

            if (event.type().equals(EGameEvent.PIECE_CAPTURED))
                setScore(player.getScore());
        }

    }

    /**
     * Sets the player's score.
     */
    public void setScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    /**
     * Adds a move to the moves area for display.
     * @param move The move to add.
     */
    public void addMove(String move) {
        movesArea.append(move + "\n");
    }

    /**
     * Clears all moves from the moves area.
     */
    public void clearMoves() {
        movesArea.setText("");
    }
}