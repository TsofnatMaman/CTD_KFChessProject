package command;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.listeners.ActionData;
import interfaces.*;

/**
 * Command representing a jump action for a piece on the board.
 * Implements the ICommand interface for execution.
 */
public class JumpCommand implements ICommand {

    /** The piece performing the jump. */
    private final IPiece piece;

    /** The board on which the jump is executed. */
    private final IBoard board;

    /**
     * Constructs a JumpCommand for the specified piece and board.
     *
     * @param piece the piece to jump
     * @param board the board instance
     */
    public JumpCommand(IPiece piece, IBoard board) {
        this.piece = piece;
        this.board = board;
    }

    /**
     * Executes the jump action if it is legal.
     * Publishes a PIECE_JUMP event upon successful execution.
     */
    @Override
    public void execute() {
        if (!board.isJumpLegal(piece)) return;

        board.jump(piece);

        EventPublisher.getInstance()
                .publish(EGameEvent.PIECE_JUMP,
                        new GameEvent(EGameEvent.PIECE_JUMP,
                                new ActionData(piece.getPlayer(), "piece " + piece + " jumping")));
    }
}
