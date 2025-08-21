package pieces;

import interfaces.IState;
import interfaces.IPiece;
import moves.Move;
import moves.Moves;
import state.StateMachine;

import java.io.IOException;
import java.util.List;

/**
 * Represents a chess piece on the board.
 * Maintains type, owner, position, and its state machine for animations and actions.
 */
public class Piece implements IPiece {

    private final EPieceType type;
    private final int playerId;
    private List<Move> moves;
    private final StateMachine fsm;
    private Position position;
    private boolean wasCaptured;
    private boolean isFirstMove;

    /**
     * Constructs a chess piece.
     *
     * @param type      Piece type
     * @param playerId  Owner player ID
     * @param sm        State machine controlling animations and actions
     * @param position  Initial board position
     * @throws IOException if move definitions cannot be loaded
     */
    public Piece(EPieceType type, int playerId, StateMachine sm, Position position) throws IOException {
        this.type = type;
        this.playerId = playerId;
        this.position = position;
        this.isFirstMove = true;
        this.fsm = sm;
        this.moves = Moves.createMovesList(type, playerId);
    }

    /* --- Getters and setters --- */

    @Override
    public Position getPos() {
        return position;
    }

    private void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public int getPlayer() {
        return playerId;
    }

    @Override
    public EPieceType getType() {
        return type;
    }

    @Override
    public List<Move> getMoves() {
        return moves;
    }

    @Override
    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    @Override
    public boolean isFirstMove() {
        return isFirstMove;
    }

    private void setFirstMove() {
        this.isFirstMove = false;
    }

    @Override
    public boolean isCaptured() {
        return wasCaptured;
    }

    @Override
    public void markCaptured() {
        this.wasCaptured = true;
    }

    /* --- State and action methods --- */

    @Override
    public void update(long now) {
        // Update position only after the current action is finished
        if (fsm.getCurrentState().isActionFinished(now)) {
            setPosition(fsm.getCurrentState().getPhysics().getTargetPos());
        }
        fsm.update(now);
    }

    @Override
    public void move(Position to) {
        fsm.onEvent(EPieceEvent.MOVE, position, to.copy());
        setFirstMove();
    }

    @Override
    public void jump() {
        fsm.onEvent(EPieceEvent.JUMP);
    }

    @Override
    public IState getCurrentState() {
        return fsm.getCurrentState();
    }

    @Override
    public boolean canAction() {
        return fsm.getCurrentState().getName().isCanAction();
    }

    @Override
    public boolean isCapturable() {
        return fsm.getCurrentState().getName().isCanCapturable();
    }

    @Override
    public String toString() {
        return type.toString() + playerId;
    }
}
