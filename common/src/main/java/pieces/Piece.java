package pieces;

import interfaces.IState;
import interfaces.IPiece;
import moves.Move;
import moves.Moves;
import state.StateMachine;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class Piece implements IPiece {
    private final EPieceType type;
    private final int playerId;
    private List<Move> moves;
    private final StateMachine fsm;
    private Position position;
    private boolean wasCaptured;
    private boolean isFirstMove;

    public Piece(EPieceType type, int playerId, StateMachine sm, Position position) throws IOException {
        this.type = type;
        this.playerId = playerId;
        this.position = position;
        this.isFirstMove = true;
        this.fsm = sm;

        this.moves = Moves.createMovesList(type, playerId);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getPlayerId() {
        return playerId;
    }

    @Override
    public EPieceType getType() {
        return type;
    }

    @Override
    public int getPlayer() {
        return playerId;
    }

    @Override
    public void update() {
        Optional<EPieceEvent> event = fsm.update();

        if (event.isPresent() && event.get() == EPieceEvent.DONE) {
            // Update logical position only after the action is finished
            setPosition(fsm.getCurrentState().getPhysics().getTargetPos());//TODO:maybe copy
        }
    }

    @Override
    public void move(Position to) {
        fsm.onEvent(EPieceEvent.MOVE, position, to);
        setFirstMove(false);
    }

    @Override
    public void jump() {
        fsm.onEvent(EPieceEvent.JUMP);
    }

    @Override
    public boolean isCaptured() {
        return wasCaptured;
    }

    @Override
    public void markCaptured() {
        this.wasCaptured = true;
    }

    @Override
    public int getRow() {
        return position.getRow();
    }

    @Override
    public int getCol() {
        return position.getCol();
    }

    @Override
    public Point2D.Double getCurrentPixelPosition() {
        return fsm.getCurrentState().getCurrentPosition();
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
    public boolean canCapturable(){
        return fsm.getCurrentState().getName().isCanCapturable();
    }

    @Override
    public String toString() {
        return type.toString()+getPlayer();
    }

    @Override
    public Position getPos() {
        return position;
    }

    @Override
    public boolean isFirstMove() {
        return isFirstMove;
    }

    @Override
    public void setFirstMove(boolean firstMove) {
        isFirstMove = firstMove;
    }

    @Override
    public IState getCurrentState(){
        return fsm.getCurrentState();
    }

    @Override
    public boolean canAction(){
        return fsm.getCurrentState().getName().isCanAction();
    }
}