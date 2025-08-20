package pieces;

import board.BoardConfig;
import graphics.GraphicsLoader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import state.*;
import interfaces.IGraphicsData;
import interfaces.IPhysicsData;
import interfaces.IState;
import utils.LogUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Factory for creating chess pieces by type, player ID, and position.
 * Loads all states, physics, and graphics for the piece from resources.
 */
public class PiecesFactory {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates a Piece instance with fully initialized states and graphics.
     *
     * @param code      Piece type
     * @param playerId  Owner player ID
     * @param pos       Position on the board
     * @param config    Board configuration
     * @return Piece instance or null if loading failed
     */
    public static Piece createPieceByCode(EPieceType code, int playerId, Position pos, BoardConfig config) {
        Map<EState, IState> states = new HashMap<>();
        String basePath = "/pieces/" + code.getVal() + "/states/";

        try {
            // Load state directories
            URL dirURL = PiecesFactory.class.getResource(basePath);
            if (dirURL == null || !"file".equals(dirURL.getProtocol())) {
                LogUtils.logDebug("Cannot load states from: " + basePath);
                return null;
            }

            File[] stateDirs = new File(dirURL.toURI()).listFiles(File::isDirectory);
            if (stateDirs == null || stateDirs.length == 0) {
                LogUtils.logDebug("No state folders found for piece: " + code.getVal());
                return null;
            }

            // Load each state
            for (File stateFolder : stateDirs) {
                EState stateName = EState.getValueOf(stateFolder.getName());
                String configPath = basePath + stateName + "/config.json";

                try (InputStream is = PiecesFactory.class.getResourceAsStream(configPath)) {
                    if (is == null) {
                        LogUtils.logDebug("Missing config for state: " + stateName);
                        continue;
                    }

                    JsonNode root = mapper.readTree(is);

                    // Load physics
                    IPhysicsData physics = mapper.treeToValue(root.path("physics"), PhysicsData.class);

                    // Load graphics
                    IGraphicsData graphicsData = mapper.treeToValue(root.path("graphics"), GraphicsData.class);
                    BufferedImage[] sprites = GraphicsLoader.loadAllSprites(code, playerId, stateName);

                    if (sprites.length == 0) {
                        LogUtils.logDebug("No sprites for state: " + stateName);
                        continue;
                    }

                    graphicsData.setFrames(sprites);
                    graphicsData.setTotalFrames(sprites.length);

                    // Create state
                    IState state = new State(stateName, pos, pos, config, physics, graphicsData);
                    states.put(stateName, state);
                }
            }

            if (states.isEmpty()) {
                LogUtils.logDebug("No valid states loaded for piece: " + code.getVal());
                return null;
            }

            // Choose initial state
            EState initialState = states.containsKey(EState.LONG_REST) ? EState.LONG_REST
                    : states.keySet().iterator().next();

            // Initialize state machine
            StateMachine sm = new StateMachine(states, new TransitionTable(basePath + "transitions.csv"), initialState, pos);

            return new Piece(code, playerId, sm, pos);

        } catch (Exception e) {
            String mes = "Exception in createPieceByCode: " + e.getMessage();
            LogUtils.logDebug(mes);
            throw new RuntimeException(mes, e);
        }
    }
}
