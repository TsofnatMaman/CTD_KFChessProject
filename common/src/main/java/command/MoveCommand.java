package command;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.listeners.ActionData;
import interfaces.ICommand;
import interfaces.IBoard;
import pieces.Position;
import utils.Utils;
import utils.LogUtils;

/**
 * Command representing a move action for a piece on the board.
 * <p>
 * Implements {@link ICommand} to encapsulate the move operation,
 * validating legality, performing the move, and triggering appropriate game events.
 * </p>
 */
public class MoveCommand implements ICommand {

    /** The starting position of the piece. */
    private final Position from;

    /** The destination position of the piece. */
    private final Position to;

    /** The board on which the move is executed. */
    private final IBoard board;

    /**
     * Constructs a new {@code MoveCommand} with specified positions and board.
     *
     * @param from  starting position of the piece
     * @param to    destination position of the piece
     * @param board the board instance on which the move will occur
     */
    public MoveCommand(Position from, Position to, IBoard board) {
        this.from = from;
        this.to = to;
        this.board = board;
    }

    /**
     * Executes the move action.
     * <p>
     * If the move is legal according to the board's rules engine:
     * <ul>
     *   <li>The piece is moved from {@code from} to {@code to}</li>
     *   <li>{@link EGameEvent#PIECE_START_MOVED} event is published</li>
     * </ul>
     * If the move is illegal:
     * <ul>
     *   <li>{@link EGameEvent#ILLEGAL_CMD} event is published</li>
     * </ul>
     * The move is also logged for debugging purposes.
     * </p>
     */
    @Override
    public void execute() {
        String message;

        if (board.getBoardRulesEngine().isMoveLegal(board, from, to)) {
            message = Utils.getName(from) + " --> " + Utils.getName(to);
            ActionData actionData = new ActionData(board.getPiece(from).getPlayer(), message);

            // Publish a successful move event
            EventPublisher.getInstance()
                    .publish(EGameEvent.PIECE_START_MOVED,
                            new GameEvent(EGameEvent.PIECE_START_MOVED, actionData));

            // Perform the move on the board
            board.move(from, to);
        } else {
            message = "Illegal move from " + from + " to " + to;

            // Publish an illegal move event
            EventPublisher.getInstance()
                    .publish(EGameEvent.ILLEGAL_CMD,
                            new GameEvent(EGameEvent.ILLEGAL_CMD,
                                    new ActionData(board.getPiece(from).getPlayer(), message)));
        }

        // Log the move or illegal attempt
        LogUtils.logDebug(message);
    }
}
