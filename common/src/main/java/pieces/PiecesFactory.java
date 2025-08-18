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
 * Factory for creating pieces by code and position.
 */
public class PiecesFactory {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates a piece by its code, position, and board configuration.
     * Loads all states and graphics for the piece from resources.
     * @param code Piece code
     * @param pos Position on the board
     * @param config Board configuration
     * @return Piece instance or null if failed
     */
    public static Piece createPieceByCode(EPieceType code, int playerId, Position pos, BoardConfig config) {
        double TILE_SIZE = config.tileSize;

        Map<EState, IState> states = new HashMap<>();
        String basePath = "/pieces/" + code.getVal() + "/states/";

        try {
            URL dirURL = PiecesFactory.class.getResource(basePath);
            if (dirURL == null || !dirURL.getProtocol().equals("file")) {
                LogUtils.logDebug("Cannot load states from: " + basePath);
                return null;
            }

            File statesDir = new File(dirURL.toURI());
            File[] subdirs = statesDir.listFiles(File::isDirectory);
            if (subdirs == null) return null;

            // Load each state
            for (File stateFolder : subdirs) {
                EState stateName = EState.getValueOf(stateFolder.getName());
                String configPath = basePath + stateName + "/config.json";
                InputStream is = PiecesFactory.class.getResourceAsStream(configPath);
                if (is == null) {
                    LogUtils.logDebug("Missing config for state: " + stateName);
                    continue;
                }

                JsonNode root = mapper.readTree(is);
                JsonNode physicsNode = root.path("physics");

                IPhysicsData physics = mapper.treeToValue(physicsNode, PhysicsData.class);

                JsonNode graphicsNode = root.path("graphics");
                IGraphicsData graphicsData = mapper.treeToValue(graphicsNode, GraphicsData.class);

                BufferedImage[] sprites = GraphicsLoader.loadAllSprites(code, playerId, stateName);
                if (sprites.length == 0) {
                    LogUtils.logDebug("No sprites for state: " + stateName);
                    continue;
                }

                graphicsData.setFrames(sprites);

                IState state = new State(stateName, pos, pos, TILE_SIZE, physics, graphicsData);
                states.put(stateName, state);
            }

            if (states.isEmpty()) {
                LogUtils.logDebug("No states loaded for piece: " + code.getVal());
                return null;
            }

            // Create template from loaded states
            PieceTemplate template = new PieceTemplate(code, states);

            // Decide initial state: prefer LONG_REST if present, else first key
            EState initialState = states.containsKey(EState.LONG_REST) ? EState.LONG_REST
                    : states.keySet().iterator().next();


            StateMachine sm = new StateMachine(states, new TransitionTable(basePath+"transitions.csv"), initialState);

            // Create and return piece with template
            return new Piece(code, playerId, sm, pos);

        } catch (Exception e) {
            String mes = "Exception in createPieceByCode: " + e.getMessage();
            LogUtils.logDebug(mes);
            throw new RuntimeException(mes, e);
        }
    }

}
