package command;

import events.EventPublisher;
import events.GameEvent;
import events.listeners.ActionData;
import helps.ConvertPiecePositionToName;
import interfaces.*;
import pieces.Position;
import player.Player;
import utils.LogUtils;

/**
 * Command for moving a piece from one position to another on the board.
 */
public class MoveCommand implements ICommand {
    /** The starting position of the move. */
    private final Position from;
    /** The target position of the move. */
    private final Position to;
    /** The board on which the move is performed. */
    private final IBoard board;

    /**
     * Constructs a MoveCommand for moving a piece from one position to another.
     *
     * @param from The starting position
     * @param to The target position
     * @param board The board instance
     */
    public MoveCommand(Position from, Position to, IBoard board) {
        this.from = from;
        this.to = to;
        this.board = board;
    }

    /**
     * Executes the move command, moving the piece if the move is legal.
     * Logs the action and handles illegal moves.
     */
    @Override
    public void execute() {
        if (!board.isMoveLegal(from, to)) {
            String mes = "Illegal move from " + from + " to " + to;
            EventPublisher.getInstance()
                            .publish(GameEvent.PIECE_MOVED,
                                    new GameEvent(GameEvent.PIECE_MOVED ,new ActionData(board.getPlayerOf(board.getPiece(from)), mes)));
            LogUtils.logDebug(mes);
            return;
        }
        String mes = ConvertPiecePositionToName.getName(from) + " --> " + ConvertPiecePositionToName.getName(to);
        EventPublisher.getInstance()
                        .publish(GameEvent.PIECE_MOVED,
                                new GameEvent(GameEvent.PIECE_MOVED, new ActionData(board.getPlayerOf(board.getPiece(from)), mes)));
        LogUtils.logDebug(mes);
        board.move(from, to);
    }
}
