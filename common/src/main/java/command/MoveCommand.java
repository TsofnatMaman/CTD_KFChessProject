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
 * Implements the ICommand interface for execution.
 */
public class MoveCommand implements ICommand {

    /** Starting position of the piece. */
    private final Position from;

    /** Destination position of the piece. */
    private final Position to;

    /** The board on which the move is executed. */
    private final IBoard board;

    /**
     * Constructs a MoveCommand with specified from/to positions and board.
     *
     * @param from starting position
     * @param to destination position
     * @param board the board instance
     */
    public MoveCommand(Position from, Position to, IBoard board) {
        this.from = from;
        this.to = to;
        this.board = board;
    }

    /**
     * Executes the move action if legal.
     * Publishes PIECE_START_MOVED event on success, or ILLEGAL_CMD event on failure.
     */
    @Override
    public void execute() {
        String message;

        if(board.getBoardRulesEngine().isMoveLegal(board, from, to)) {
            message = Utils.getName(from) + " --> " + Utils.getName(to);
            ActionData actionData = new ActionData(board.getPiece(from).getPlayer(), message);
            EventPublisher.getInstance()
                    .publish(EGameEvent.PIECE_START_MOVED,
                            new GameEvent(EGameEvent.PIECE_START_MOVED,
                                    actionData));

            board.move(from, to);
        } else {
            message = "Illegal move from " + from + " to " + to;
            EventPublisher.getInstance()
                    .publish(EGameEvent.ILLEGAL_CMD,
                            new GameEvent(EGameEvent.ILLEGAL_CMD,
                                    new ActionData(board.getPiece(from).getPlayer(), message)));
        }

        LogUtils.logDebug(message);
    }
}
