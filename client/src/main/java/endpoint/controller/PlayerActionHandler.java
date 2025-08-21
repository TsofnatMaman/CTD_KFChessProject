package endpoint.controller;

import endpoint.view.BoardPanel;
import interfaces.IPiece;
import pieces.Position;

/**
 * Handles player interactions with the board.
 * <p>
 * Manages piece selection, target selection, and updates legal moves on the UI.
 * Maintains the current client state to determine whether a piece or a target position is being selected.
 * </p>
 */
public class PlayerActionHandler {

    /** Currently selected piece position, or null if none is selected */
    private Position selected = null;

    /** Current state of the client (selecting piece or target) */
    private ClientState clientState = ClientState.WAIT_SELECTING_PIECE;

    /** Reference to the main game controller */
    private final GameController controller;

    /**
     * Constructs a PlayerActionHandler for a given GameController.
     *
     * @param controller the game controller
     */
    public PlayerActionHandler(GameController controller) {
        this.controller = controller;
    }

    /**
     * Handles a player selecting a position on the board.
     * <p>
     * Depending on the current client state, either selects a piece, displays its legal moves,
     * or resets the selection if selecting a target.
     * </p>
     *
     * @param pos the position selected by the player
     */
    public void handlePlayerSelection(Position pos) {
        IPiece p = controller.getModel().getBoard().getPiece(pos);
        BoardPanel boardPanel = (BoardPanel) controller.getGamePanel().getBoardPanel();

        switch (clientState) {
            case WAIT_SELECTING_PIECE -> {
                // Validate the selected piece
                if (p == null || p.isCaptured() || p.getPlayer() != controller.getPlayerId() || !p.canAction()) {
                    return;
                }

                // Highlight the piece and display legal moves
                boardPanel.setSelected(pos.copy());
                boardPanel.setLegalMoves(controller.getModel().getBoard().getLegalMoves(pos));
                boardPanel.repaint();

                selected = pos.copy();
                clientState = ClientState.WAIT_SELECTING_TARGET;
            }

            case WAIT_SELECTING_TARGET -> {
                // Clear selection if selecting a target
                boardPanel.clearSelection();
                selected = null;
                clientState = ClientState.WAIT_SELECTING_PIECE;
            }
        }

        // Send the player's selection to the server
        try {
            controller.sendPlayerSelection(pos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Refreshes the legal moves for the currently selected piece.
     * <p>
     * Called after a move has completed to update the UI.
     * </p>
     */
    public void refreshLegalMoves() {
        if (selected != null) {
            ((BoardPanel) controller.getGamePanel().getBoardPanel())
                    .setLegalMoves(controller.getModel().getBoard().getLegalMoves(selected));
        }
    }
}
