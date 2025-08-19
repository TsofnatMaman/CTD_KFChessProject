package command;

import board.BoardRulesEngine;
import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.listeners.ActionData;
import interfaces.ICommand;
import interfaces.IBoard;
import pieces.Position;
import utils.Utils;
import utils.LogUtils;

public class MoveCommand implements ICommand {
    private final Position from;
    private final Position to;
    private final IBoard board;

    public MoveCommand(Position from, Position to, IBoard board) {
        this.from = from;
        this.to = to;
        this.board = board;
    }

    @Override
    public void execute() {
        if (!BoardRulesEngine.isMoveLegal(board, from, to)) {
            String mes = "Illegal move from " + from + " to " + to;
            EventPublisher.getInstance()
                    .publish(EGameEvent.ILLEGAL_CMD,
                            new GameEvent(EGameEvent.ILLEGAL_CMD ,new ActionData(board.getPiece(from).getPlayer(), mes)));
            LogUtils.logDebug(mes);
            return;
        }
        String mes = Utils.getName(from) + " --> " + Utils.getName(to);
        EventPublisher.getInstance()
                .publish(EGameEvent.PIECE_START_MOVED,
                        new GameEvent(EGameEvent.PIECE_START_MOVED, new ActionData(board.getPiece(from).getPlayer(), mes)));
        LogUtils.logDebug(mes);
        board.move(from, to);
    }
}
