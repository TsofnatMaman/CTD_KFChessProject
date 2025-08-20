package endpoint.controller;

import endpoint.view.BoardPanel;
import interfaces.IPiece;
import pieces.Position;

/**
 * Handles player interactions with the board.
 */
public class PlayerActionHandler {

    private Position selected = null;
    private ClientState clientState = ClientState.WAIT_SELECTING_PIECE;
    private final GameController controller;

    public PlayerActionHandler(GameController controller) {
        this.controller = controller;
    }

    public void handlePlayerSelection(Position pos) {
        IPiece p = controller.getModel().getBoard().getPiece(pos);
        BoardPanel boardPanel = (BoardPanel) controller.getGamePanel().getBoardPanel();

        switch (clientState) {
            case WAIT_SELECTING_PIECE -> {
                if (p == null || p.isCaptured() || p.getPlayer() != controller.getPlayerId() || !p.canAction()) return;
                boardPanel.setSelected(pos.copy());
                boardPanel.setLegalMoves(controller.getModel().getBoard().getLegalMoves(pos));
                boardPanel.repaint();
                selected = pos.copy();
                clientState = ClientState.WAIT_SELECTING_TARGET;
            }
            case WAIT_SELECTING_TARGET -> {
                boardPanel.clearSelection();
                selected = null;
                clientState = ClientState.WAIT_SELECTING_PIECE;
            }
        }

        try { controller.sendPlayerSelection(pos); } catch (Exception e) { throw new RuntimeException(e); }
    }

    public void refreshLegalMoves() {
        if (selected != null)
            ((BoardPanel) controller.getGamePanel().getBoardPanel())
                    .setLegalMoves(controller.getModel().getBoard().getLegalMoves(selected));
    }
}
