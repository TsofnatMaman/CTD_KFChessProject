package interfaces;

import java.awt.image.BufferedImage;

/**
 * Interface for graphics data operations for piece animation.
 */
public interface IGraphicsData {

    /** Resets the animation to the initial frame and state. */
    void reset();

    /**
     * Updates the animation frame based on elapsed time.
     * Typically called in the game loop or timer.
     * @param now Current time in nanoseconds
     */
    void update(long now);

    /** Returns the current frame number (1-based for UI). */
    int getCurrentNumFrame();

    /** Returns the total number of frames in the animation. */
    int getTotalFrames();

    /** Returns the animation speed in frames per second. */
    double getFramesPerSec();

    /** Returns the current frame image. */
    BufferedImage getCurrentFrame();

    /** Returns the current frame index (0-based in array). */
    int getCurrentFrameIdx();

    /** Sets the frames array for the animation. */
    void setFrames(BufferedImage[] frames);

    /** Sets the total number of frames. */
    void setTotalFrames(int totalFrames);

    /** Sets the last frame update time (for testing or manual control). */
    void setLastFrameTimeNanos(long nanos);
}
