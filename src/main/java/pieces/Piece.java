package pieces;

import board.BoardConfig;
import interfaces.*;
import moves.Move;
import moves.Moves;
import utils.LogUtils;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Piece implements IPiece {
    private final String id;
    private final EPieceType type;
    private Map<EState, IState> states;
    private EState currentStateName;
    private IState currentState;

    private List<Move> moves;

    private Position pos;

    private boolean wasCaptured = false;

    public Piece(String id, EPieceType type, int playerId, Map<EState, IState> states, EState initialState, Position pos) throws IOException {
        this.id = id;
        this.states = states;
        this.currentStateName = initialState;
        this.currentState = states.get(initialState);
        this.pos = pos;

        this.type = type;

        moves = Moves.createMovesList(type, playerId);
    }

    @Override
    public int getPlayer() {
        return BoardConfig.getPlayerOf(Integer.parseInt(this.getId().split(",")[0]));
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
    public void setState(EState newStateName) {
        if (states.containsKey(newStateName) && !newStateName.equals(currentStateName)) {
            currentStateName = newStateName;
            currentState = states.get(newStateName);

            // Update state.startPos before reset
            currentState.reset(newStateName, pos, pos);
        } else if (!states.containsKey(newStateName)) {
            System.err.println("State '" + newStateName + "' not found!");
            LogUtils.logDebug("State '" + newStateName + "' not found!");
        }
    }


    @Override
    public IState getCurrentState() {
        return currentState;
    }

    @Override
    public void update() {
        currentState.update();

        if (currentState.isActionFinished()) {
            // Update logical position only after the action is finished
            pos = new Position(currentState.getTargetRow(), currentState.getTargetCol());

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
        if (states.containsKey(EState.MOVE)) {
            currentStateName = EState.MOVE;
            currentState = states.get(EState.MOVE);
            currentState.reset(EState.MOVE, pos, to);
        } else {
            System.err.println("Missing 'move' state!");
            LogUtils.logDebug("Missing 'move' state!");
        }
    }

    @Override
    public void jump() {
        if (states.containsKey(EState.JUMP)) {
            currentStateName = EState.JUMP;
            currentState = states.get(EState.JUMP);
            currentState.reset(EState.JUMP,pos, pos);
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
        return pos.getRow();
    }

    @Override
    public int getCol() {
        return pos.getCol();
    }

    @Override
    public EState getCurrentStateName() {
        return currentStateName;
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
    public Map<EState, IState> getStates() {
        return states;
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
        return pos;
    }

    @Override
    public boolean equals(Object obj) {
        return id.equals(((Piece)obj).id);
    }
}
