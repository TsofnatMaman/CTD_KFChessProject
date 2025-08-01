package pieces;

import board.BoardConfig;
import interfaces.IState;
import interfaces.IPiece;
import interfaces.EState;
import moves.Move;
import moves.Moves;
import utils.LogUtils;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;

public class Piece implements IPiece {
    private final String id;
    private final EPieceType type;
    private final int playerId;
    private List<Move> moves;
    private final PieceTemplate template;
    private IState currentState;
    private EState currentStateName;
    private Position position;
    private boolean wasCaptured;

    public Piece(String id, EPieceType type, int playerId, PieceTemplate template, EState initialState, Position position) throws IOException {
        this.id = id;
        this.type = type;
        this.playerId = playerId;
        this.template = template;
        this.currentStateName = initialState;
        this.currentState = template.getState(initialState);
        this.position = position;

        this.moves = Moves.createMovesList(type, playerId);
    }

    public PieceTemplate getTemplate() {
        return template;
    }

    @Override
    public IState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(IState newState) {
        this.currentState = newState;
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
    public String getId() {
        return id;
    }

    @Override
    public EPieceType getType() {
        return type;
    }

    @Override
    public EState getCurrentStateName() {
        return currentStateName;
    }

    @Override
    public int getPlayer() {
        return BoardConfig.getPlayerOf(Integer.parseInt(this.getId().split(constants.PieceConstants.POSITION_SEPARATOR)[0]));
    }

    @Override
    public void setState(EState newStateName) {
        IState state = template.getState(newStateName);
        if (state != null && !newStateName.equals(currentStateName)) {
            currentStateName = newStateName;
            currentState = state;

            // Update state.startPos before reset
            currentState.reset(newStateName, position, position);
        } else if (state == null) {
            System.err.println("State '" + newStateName + "' not found!");
            LogUtils.logDebug("State '" + newStateName + "' not found!");
        }
    }

    @Override
    public void update() {
        currentState.update();

        if (currentState.isActionFinished()) {
            // Update logical position only after the action is finished
            position = new Position(currentState.getTargetRow(), currentState.getTargetCol());

            EState nextState = currentState.getPhysics().getNextStateWhenFinished();

            setState(nextState);
            return; // Stop to prevent transitioning state twice in one update
        }

        // Automatic transition if animation is finished
        if (currentState.getGraphics() != null && currentState.getGraphics().isAnimationFinished()) {
            EState nextState = currentState.getPhysics().getNextStateWhenFinished();
            setState(nextState);

        }
    }

    @Override
    public void move(Position to) {
        IState state = template.getState(EState.MOVE);
        if (state != null) {
            currentStateName = EState.MOVE;
            currentState = state;
            currentState.reset(EState.MOVE, position, to);
        } else {
            System.err.println("Missing 'move' state!");
            LogUtils.logDebug("Missing 'move' state!");
        }
    }

    @Override
    public void jump() {
        IState state = template.getState(EState.JUMP);
        if (state != null) {
            currentStateName = EState.JUMP;
            currentState = state;
            currentState.reset(EState.JUMP,position, position);
        } else {
            System.err.println("Missing 'jump' state!");
            LogUtils.logDebug("Missing 'jump' state!");
        }
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
        return currentState.getCurrentPosition();
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
    public boolean canMoveOver(){
        return currentStateName.isCanMoveOver();
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
    public boolean equals(Object obj) {
        return id.equals(((Piece)obj).id);
    }

    @Override
    public Position getIdAsPosition(){
        String[] pos = getId().split(constants.PieceConstants.POSITION_SEPARATOR);
        return new Position(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
    }
}