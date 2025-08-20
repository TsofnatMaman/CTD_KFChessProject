package command;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.listeners.ActionData;
import interfaces.*;

/**
 * Command for performing a jump action with a piece on the board.
 */
public class JumpCommand implements ICommand {

    /** The piece to perform the jump action. */
    private final IPiece p;
    /** The board on which the jump is performed. */
    private final IBoard board;

    /**
     * Constructs a JumpCommand for the given piece and board.
     *
     * @param p The piece to jump
     * @param board The board instance
     */
    public JumpCommand(IPiece p, IBoard board){
        this.p = p;
        this.board = board;
    }

    /**
     * Executes the jump command if the jump is legal.
     */
    @Override
    public void execute() {
        if(!board.isJumpLegal(p))
            return;
        board.jump(p);
        EventPublisher.getInstance()
                .publish(EGameEvent.PIECE_JUMP,
                        new GameEvent(EGameEvent.PIECE_JUMP, new ActionData(p.getPlayer(), "pieces " +p+" jumping")));
    }
}
