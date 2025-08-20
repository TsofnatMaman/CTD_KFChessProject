package graphics;

import state.EState;
import pieces.EPieceType;
import utils.LogUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * Utility class for loading and caching piece sprite images.
 * Supports loading individual frames or all frames for a given piece type, player, and state.
 */
public class GraphicsLoader {

    /** Cache to avoid reloading the same image multiple times. */
    private static final Map<String, BufferedImage> cache = new HashMap<>();

    /**
     * Loads a single sprite image by piece type, player, state, and frame index (1-based).
     *
     * @param pieceType The type of piece
     * @param player    Player index (0 or 1)
     * @param stateName The state of the piece
     * @param frameIndex Frame number (1-based)
     * @return BufferedImage of the sprite, or null if failed
     */
    public static BufferedImage loadSprite(EPieceType pieceType, int player, EState stateName, int frameIndex) {
        String path = String.format("/pieces/%s/states/%s/sprites/sprites%d/%d.png",
                pieceType.getVal(), stateName, player, frameIndex);

        if (cache.containsKey(path)) {
            return cache.get(path);
        }

        try {
            BufferedImage image = ImageIO.read(
                    Objects.requireNonNull(GraphicsLoader.class.getResourceAsStream(path))
            );
            cache.put(path, image);
            return image;
        } catch (IOException | IllegalArgumentException | NullPointerException e) {
            LogUtils.logDebug("Failed to load sprite: " + path);
            return null;
        }
    }

    /**
     * Loads all sequential sprite frames for a given piece type, player, and state.
     * Continues until a frame file does not exist.
     *
     * @param pieceType The type of piece
     * @param player    Player index (0 or 1)
     * @param stateName The state of the piece
     * @return Array of BufferedImages containing all loaded frames
     * @throws RuntimeException if no frames were successfully loaded
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
