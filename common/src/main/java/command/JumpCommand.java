package command;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.listeners.ActionData;
import interfaces.*;

/**
 * Command representing a jump action for a piece on the board.
 * <p>
 * Implements {@link ICommand} to encapsulate the jump operation,
 * allowing it to be executed, and triggering the appropriate game events.
 * </p>
 */
public class JumpCommand implements ICommand {

    /** The piece that will perform the jump. */
    private final IPiece piece;

    /** The board on which the jump will be executed. */
    private final IBoard board;

    /**
     * Constructs a new {@code JumpCommand} for a specific piece on a board.
     *
     * @param piece the piece to perform the jump
     * @param board the board instance where the jump occurs
     */
    public JumpCommand(IPiece piece, IBoard board) {
        this.piece = piece;
        this.board = board;
    }

    /**
     * Executes the jump action.
     * <p>
     * If the jump is legal, the {@link EGameEvent#PIECE_JUMP} event is published.
     * If the jump is illegal, the {@link EGameEvent#ILLEGAL_CMD} event is published
     * instead, with details about the offending piece and player.
     * </p>
     */
    @Override
    public void execute() {
        try {
            // Attempt to perform the jump on the board
            board.jump(piece);

            // Publish a successful jump event
            EventPublisher.getInstance()
                    .publish(EGameEvent.PIECE_JUMP,
                            new GameEvent(EGameEvent.PIECE_JUMP,
                                    new ActionData(piece.getPlayer(), "piece " + piece + " jumping")));
        } catch (IllegalCmdException e) {
            // Publish an event for an illegal jump attempt
            String message = "Illegal jump " + piece;
            EventPublisher.getInstance()
                    .publish(EGameEvent.ILLEGAL_CMD,
                            new GameEvent(EGameEvent.ILLEGAL_CMD,
                                    new ActionData(piece.getPlayer(), message)));
        }
    }
}
