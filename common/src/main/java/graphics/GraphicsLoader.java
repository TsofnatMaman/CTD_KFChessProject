package graphics;

import interfaces.AppLogger;
import state.EState;
import pieces.EPieceType;
import utils.Slf4jAdapter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * Utility class for loading and caching piece sprite images.
 * Supports loading individual frames or all frames for a piece type, player, and state.
 */
public class GraphicsLoader {

    private static final AppLogger logger = new Slf4jAdapter(GraphicsLoader.class);

    /** Cache to prevent reloading the same image multiple times. */
    private static final Map<String, BufferedImage> cache = new HashMap<>();

    /**
     * Loads a single sprite image for a given piece type, player, state, and frame index (1-based).
     *
     * @param pieceType  Piece type
     * @param player     Player index (0 or 1)
     * @param stateName  Piece state
     * @param frameIndex Frame index (1-based)
     * @return BufferedImage of the sprite, or null if failed
     */
    public static BufferedImage loadSprite(EPieceType pieceType, int player, EState stateName, int frameIndex) {
        String path = String.format("/pieces/%s/states/%s/sprites/sprites%d/%d.png",
                pieceType.getVal(), stateName, player, frameIndex);

        if (cache.containsKey(path)) return cache.get(path);

        try {
            BufferedImage image = ImageIO.read(
                    Objects.requireNonNull(GraphicsLoader.class.getResourceAsStream(path))
            );
            cache.put(path, image);
            return image;
        } catch (IOException | IllegalArgumentException | NullPointerException e) {
            logger.error("Failed to load sprite: " + path, e);
            return null;
        }
    }

    /**
     * Loads all sequential sprite frames for a piece type, player, and state.
     * Stops when a frame file is missing.
     *
     * @param pieceType Piece type
     * @param player    Player index
     * @param stateName Piece state
     * @return Array of BufferedImages containing all frames
     * @throws RuntimeException if no frames are loaded
     */
    public static BufferedImage[] loadAllSprites(EPieceType pieceType, int player, EState stateName) {
        List<BufferedImage> sprites = new ArrayList<>();
        int frameIndex = 1;

        while (true) {
            BufferedImage sprite = loadSprite(pieceType, player, stateName, frameIndex);
            if (sprite == null) break;
            sprites.add(sprite);
            frameIndex++;
        }

        if (sprites.isEmpty()) {
            throw new RuntimeException("Failed to load piece images for: "
                    + pieceType + " player: " + player + " state: " + stateName);
        }

        return sprites.toArray(new BufferedImage[0]);
    }
}
