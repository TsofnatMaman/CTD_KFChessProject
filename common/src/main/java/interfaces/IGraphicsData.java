package interfaces;

import pieces.Position;
import state.EState;

import java.awt.image.BufferedImage;

/**
 * Interface for graphics data operations for piece animation.
 */
public interface IGraphicsData {

    void reset();

    /**
     * Updates the animation frame based on elapsed time.
     */
    void update(long now);

    /**
     * Gets the current frame number.
     * @return The current frame index
     */
    int getCurrentNumFrame();

    /**
     * Gets the total number of frames.
     * @return The total number of frames
     */
    int getTotalFrames();

    /**
     * Gets the frames per second for the animation.
     * @return Frames per second
     */
    double getFramesPerSec();

    /**
     * Gets the current frame image.
     * @return The current frame as BufferedImage
     */
    BufferedImage getCurrentFrame();

    int getCurrentFrameIdx();

    void setFrames(BufferedImage[] frames);

    void setTotalFrames(int totalFrames);
}
