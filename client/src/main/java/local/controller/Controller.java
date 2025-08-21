package local.controller;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import interfaces.IGame;
import interfaces.IPiece;
import local.view.BoardPanel;
import pieces.Position;
import sound.EventSoundListener;
import utils.Utils;
import viewUtils.game.GamePanel;

import java.util.Collections;
import java.util.List;

/**
 * Controller class responsible for handling player moves and interactions
 * in a local KFChess game. It listens to game events, manages piece selections,
 * legal moves, and updates the {@link BoardPanel} and {@link GamePanel}.
 */
public class Controller implements IEventListener {

    /** Reference to the game model */
    private final IGame model;

    /** Reference to the game UI panel */
    private final GamePanel gamePanel;

    /** Selected positions for player 0 and player 1 */
    private final Position[] selected = new Position[2];

    /** Legal moves for the currently selected piece of each player */
    @SuppressWarnings("unchecked") // Safe because array is only used with List<Position>
    private final List<Position>[] legalMoves = new List[2];

    /**
     * Constructs a new {@code Controller} instance and subscribes to relevant game events.
     *
     * @param game the game model
     * @param gamePanel the UI panel representing the game
     */
    public Controller(IGame game, GamePanel gamePanel) {
        this.model = game;
        this.gamePanel = gamePanel;

        // Initialize legal moves as empty lists for both players
        legalMoves[0] = Collections.emptyList();
        legalMoves[1] = Collections.emptyList();

        // Subscribe to relevant game events
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_ENDED, this);
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_UPDATE, this);
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_END_MOVED, this);

        // Initialize sound listener for event-driven sounds
        new EventSoundListener();
    }

    /**
     * Handles a move attempt from a player.
     * <p>
     * If no piece is currently selected, this method tries to select a piece
     * at the given position (if it belongs to the player and can act).
     * If a piece is already selected, it attempts to move it or reset the selection.
     * Afterwards, it updates the UI with the current selection and legal moves.
     * </p>
     *
     * @param playerId the ID of the player (0 or 1)
     * @param pos the board position being selected or moved to
     */
    public void handlePlayerMove(int playerId, Position pos) {
        IPiece piece = model.getBoard().getPiece(pos);

        if (selected[playerId] == null) {
            // Selecting a piece
            if (piece != null && piece.getPlayer() == playerId && piece.canAction()) {
                selected[playerId] = pos.copy();
                legalMoves[playerId] = model.getBoard().getLegalMoves(pos);
            }
        } else {
            // Resetting selection after a move or cancellation
            selected[playerId] = null;
            legalMoves[playerId] = Collections.emptyList();
        }

        // Update board panel with current selection and legal moves
        BoardPanel boardPanel = (BoardPanel) gamePanel.getBoardPanel();
        if (playerId == 0) {
            boardPanel.setSelected1(selected[0]);
            boardPanel.setLegalMoves1(legalMoves[0]);
        } else {
            boardPanel.setSelected2(selected[1]);
            boardPanel.setLegalMoves2(legalMoves[1]);
        }

        // Notify the model and repaint UI
        model.handleSelection(playerId, pos);
        boardPanel.repaint();
    }

    /**
     * Handles subscribed game events and updates the UI accordingly.
     *
     * @param event the game event received
     */
    @Override
    public void onEvent(GameEvent event) {
        switch (event.type()) {
            case GAME_ENDED ->
                    gamePanel.onWin(model.win());

            case GAME_UPDATE -> {
                gamePanel.onGameUpdate();
                gamePanel.updateTimerLabel(Utils.formatElapsedTime(model.getElapsedMillis()));
            }

            case PIECE_END_MOVED -> {
                // Update legal moves for both players after a piece has finished moving
                BoardPanel boardPanel = (BoardPanel) gamePanel.getBoardPanel();
                boardPanel.setLegalMoves1(model.getBoard().getLegalMoves(selected[0]));
                boardPanel.setLegalMoves2(model.getBoard().getLegalMoves(selected[1]));
            }
        }
    }
}
