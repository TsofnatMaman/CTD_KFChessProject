package viewUtils.game;

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
 * JPanel that displays information for a single player, including:
 * <ul>
 *     <li>Player name</li>
 *     <li>Current score</li>
 *     <li>List of moves performed</li>
 * </ul>
 * <p>
 * The panel also listens to game events and updates its UI accordingly.
 * </p>
 */
public class PlayerInfoPanel extends JPanel implements IEventListener {

    /** The player whose information is displayed. */
    private final IPlayer player;

    /** JLabel displaying the player's current score. */
    private final JLabel scoreLabel;

    /** Text area displaying the moves performed by the player. */
    private final JTextArea movesArea;

    /**
     * Constructs a PlayerInfoPanel for a given player.
     *
     * @param player the player whose info will be displayed
     */
    public PlayerInfoPanel(IPlayer player) {
        this.player = player;

        // Set layout and preferred size
        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(200, 0));

        // Name label setup
        JLabel nameLabel = new JLabel(player.getName());
        nameLabel.setOpaque(true);
        nameLabel.setBackground(PlayerConstants.PIECES_COLOR[player.getId()]);
        nameLabel.setForeground(PlayerConstants.PLAYER_COLORS[player.getId()]);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Score label setup
        scoreLabel = new JLabel("Score: " + player.getScore());

        // Moves area setup
        movesArea = new JTextArea(10, 15);
        movesArea.setEditable(false);
        movesArea.setLineWrap(true);
        movesArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(movesArea);
        movesArea.setFocusable(false);

        // Top panel contains name and score
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(nameLabel);
        topPanel.add(scoreLabel);


        // Add components to main panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Subscribe to relevant game events
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_START_MOVED, this);
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_JUMP, this);
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_STARTED, this);
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_CAPTURED, this);
    }

    /**
     * Handles subscribed game events and updates the UI accordingly.
     *
     * @param event the game event
     */
    @Override
    public void onEvent(GameEvent event) {
        switch (event.type()) {
            case PIECE_START_MOVED, PIECE_JUMP -> {
                ActionData data = (ActionData) event.data();
                if (player.getId() == data.playerId())
                    addMove(data.message());
            }
            case PIECE_CAPTURED -> setScore(player.getScore());
        }
    }

    /**
     * Updates the displayed score for the player.
     *
     * @param score the new score
     */
    public void setScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    /**
     * Adds a move description to the moves text area.
     *
     * @param move move description string
     */
    public void addMove(String move) {
        movesArea.append(move + "\n");
    }

    /**
     * Clears all moves from the moves text area.
     */
    public void clearMoves() {
        movesArea.setText("");
    }
}
