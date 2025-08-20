package interfaces;

import java.awt.image.BufferedImage;

/**
 * Interface for graphics data operations for piece animation.
 */
public interface IGraphicsData {

    /**
     * Resets the animation to the initial frame and state.
     */
    void reset();

    /**
     * Updates the animation frame based on elapsed time.
     * @param now The current time in nanoseconds
     */
    void update(long now);

    /**
     * Gets the current frame index.
     * @return The current frame number
     */
    int getCurrentNumFrame();

    /**
     * Gets the total number of animation frames.
     * @return Total frames
     */
    int getTotalFrames();

    /**
     * Gets the animation speed in frames per second.
     * @return Frames per second
     */
    double getFramesPerSec();

    /**
     * Retrieves the current frame as an image.
     * @return Current frame as BufferedImage
     */
    BufferedImage getCurrentFrame();

    /**
     * Returns the index of the current frame.
     * @return Current frame index
     */
    int getCurrentFrameIdx();

    /**
     * Sets the frames array for the animation.
     * @param frames Array of BufferedImages
     */
    void setFrames(BufferedImage[] frames);

    /**
     * Sets the total number of frames for the animation.
     * @param totalFrames Total frame count
     */
    void setTotalFrames(int totalFrames);

    /**
     * Sets the last frame update time (used for testing purposes).
     * @param nanos Time in nanoseconds
     */
    void setLastFrameTimeNanos(long nanos);
}
