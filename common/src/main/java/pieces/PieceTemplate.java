package pieces;

import interfaces.EState;
import interfaces.IState;
import java.io.Serializable;
import java.util.Map;

public class PieceTemplate implements Serializable {
    private final EPieceType type;
    private final Map<EState, IState> stateMap;

    public PieceTemplate(EPieceType type, Map<EState, IState> stateMap) {
        this.type = type;
        this.stateMap = stateMap;
    }

    public EPieceType getType() {
        return type;
    }

    public IState getState(EState state) {
        return stateMap.getOrDefault(state, null);
    }
}