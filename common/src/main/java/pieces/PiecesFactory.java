package pieces;

import board.BoardConfig;
import graphics.GraphicsLoader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import interfaces.AppLogger;
import state.*;
import interfaces.IGraphicsData;
import interfaces.IPhysicsData;
import interfaces.IState;
import utils.Slf4jAdapter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Factory for creating chess pieces by type, player ID, and board position.
 * Loads all states, physics, and graphics from resources.
 */
public class PiecesFactory {

    private static final AppLogger logger = new Slf4jAdapter(PiecesFactory.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates a Piece instance with fully initialized states, graphics, and physics.
     *
     * @param code      Piece type
     * @param playerId  Owner player ID
     * @param pos       Board position
     * @param config    Board configuration
     * @return Fully initialized Piece instance, or null if loading fails
     */
    public static Piece createPieceByCode(EPieceType code, int playerId, Position pos, BoardConfig config) {
        Map<EState, IState> states = new HashMap<>();
        String basePath = "pieces/" + code.getVal() + "/states/";

        try {
            ClassLoader cl = PiecesFactory.class.getClassLoader();
            Enumeration<URL> resources = cl.getResources(basePath);
            List<String> stateNames = new ArrayList<>();

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                if (url.getProtocol().equals("jar")) {
                    String path = url.getPath();
                    String jarPath = path.substring(5, path.indexOf("!"));
                    try (JarFile jar = new JarFile(jarPath)) {
                        jar.stream()
                                .map(JarEntry::getName)
                                .filter(name -> name.startsWith(basePath) && name.endsWith("config.json"))
                                .forEach(name -> {
                                    String[] parts = name.split("/");
                                    if (parts.length >= 4) stateNames.add(parts[3]);
                                });
                    }
                } else if (url.getProtocol().equals("file")) {
                    File dir = new File(url.toURI());
                    if (dir.exists() && dir.isDirectory()) {
                        for (File f : Objects.requireNonNull(dir.listFiles(File::isDirectory))) {
                            stateNames.add(f.getName());
                        }
                    }
                }
            }

            if (stateNames.isEmpty()) {
                logger.warn("No states found for piece: " + code.getVal());
                return null;
            }

            for (String stateNameStr : stateNames) {
                EState stateName = EState.getValueOf(stateNameStr);
                String configPath = "/" + basePath + stateNameStr + "/config.json";

                try (InputStream is = PiecesFactory.class.getResourceAsStream(configPath)) {
                    if (is == null) {
                        logger.debug("Missing config for state: " + stateNameStr);
                        continue;
                    }

                    JsonNode root = mapper.readTree(is);
                    IPhysicsData physics = mapper.treeToValue(root.path("physics"), PhysicsData.class);
                    IGraphicsData graphicsData = mapper.treeToValue(root.path("graphics"), GraphicsData.class);
                    BufferedImage[] sprites = GraphicsLoader.loadAllSprites(code, playerId, stateName);

                    if (sprites.length == 0) {
                        logger.debug("No sprites for state: " + stateNameStr);
                        continue;
                    }

                    graphicsData.setFrames(sprites);
                    graphicsData.setTotalFrames(sprites.length);

                    IState state = new State(stateName, pos, pos, config, physics, graphicsData);
                    states.put(stateName, state);
                }
            }

            if (states.isEmpty()) {
                logger.debug("No valid states loaded for piece: " + code.getVal());
                return null;
            }

            EState initialState = states.containsKey(EState.LONG_REST)
                    ? EState.LONG_REST
                    : states.keySet().iterator().next();

            StateMachine sm = new StateMachine(
                    states,
                    new TransitionTable("/" + basePath + "transitions.csv"),
                    initialState,
                    pos
            );

            return new Piece(code, playerId, sm, pos);

        } catch (Exception e) {
            String msg = "Exception in createPieceByCode: " + e.getMessage();
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
}