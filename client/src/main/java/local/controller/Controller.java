package local.controller;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import interfaces.IGame;
import interfaces.IPiece;
import local.view.BoardPanel;
import pieces.Position;
import utils.Utils;
import viewUtils.GamePanel;

import java.util.Collections;
import java.util.List;

/**
 * Controller class for handling player moves in a local KFChess game.
 *
 * <p>This class tracks piece selection and legal moves for two players,
 * and updates the BoardPanel accordingly.</p>
 */
public class Controller implements IEventListener {

    private final IGame model;
    private final GamePanel gamePanel;

    /** Selected positions for player 0 and 1 */
    private final Position[] selected = new Position[2];

    /** Legal moves for the currently selected piece for each player */
    private final List<Position>[] legalMoves = new List[2];

    /**
     * Constructs a Controller instance.
     *
     * @param game the game model
     * @param gamePanel the UI panel representing the game
     */
    public Controller(IGame game, GamePanel gamePanel) {
        this.model = game;
        this.gamePanel = gamePanel;
        legalMoves[0] = Collections.emptyList();
        legalMoves[1] = Collections.emptyList();

        EventPublisher.getInstance().subscribe(EGameEvent.GAME_ENDED, this);
        EventPublisher.getInstance().subscribe(EGameEvent.GAME_UPDATE, this);
        EventPublisher.getInstance().subscribe(EGameEvent.PIECE_END_MOVED, this);
    }

    /**
     * Handles a player move based on the selected position.
     * <p>
     * If no piece is currently selected, it attempts to select a piece at the given position.
     * If a piece is already selected, it checks if the target position is a legal move
     * and moves the piece. Afterwards, it resets the selection and updates the UI.
     * </p>
     *
     * @param playerId the ID of the player (0 or 1)
     * @param pos the position to select or move to
     */
    public void handlePlayerMove(int playerId, Position pos) {
        IPiece piece = model.getBoard().getPiece(pos);

        if (selected[playerId] == null) {
            // Select a piece if it belongs to the player and can act
            if (piece != null && piece.getPlayer() == playerId && piece.canAction()) {
                selected[playerId] = pos.copy();
                legalMoves[playerId] = model.getBoard().getLegalMoves(pos);
            }
        } else {
            // Reset selection and legal moves after a move or cancellation
            selected[playerId] = null;
            legalMoves[playerId] = Collections.emptyList();
        }

        BoardPanel boardPanel = (BoardPanel)gamePanel.getBoardPanel();

        // Update the UI with the current selection and legal moves
        if (playerId == 0) {
            boardPanel.setSelected1(selected[0]);
            boardPanel.setLegalMoves1(legalMoves[0]);
        } else {
            boardPanel.setSelected2(selected[1]);
            boardPanel.setLegalMoves2(legalMoves[1]);
        }

        model.handleSelection(playerId, pos);
        boardPanel.repaint();
    }

    @Override
    public void onEvent(GameEvent event) {
        switch (event.type()) {
            case GAME_ENDED -> gamePanel.onWin(model.win());
            case GAME_UPDATE -> {
                gamePanel.onGameUpdate();
                gamePanel.updateTimerLabel(Utils.formatElapsedTime(model.getElapsedMillis()));
            }
            case PIECE_END_MOVED -> {
                ((BoardPanel)gamePanel.getBoardPanel()).setLegalMoves1(model.getBoard().getLegalMoves(selected[0]));
                ((BoardPanel)gamePanel.getBoardPanel()).setLegalMoves2(model.getBoard().getLegalMoves(selected[1]));
            }
        }
    }
}
