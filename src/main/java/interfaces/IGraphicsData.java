package interfaces;

import pieces.Position;

import java.awt.image.BufferedImage;

/**
 * Interface for graphics data operations for piece animation.
 */
public interface IGraphicsData {

    /**
     * Resets the animation to the first frame.
     * @param state The new state
     * @param to The target position
     */
    void reset(EState state, Position to);

    /**
     * Updates the animation frame based on elapsed time.
     */
    void update();

    /**
     * Checks if the animation has finished (for non-looping states).
     * @return true if finished, false otherwise
     */
    boolean isAnimationFinished();

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
     * Returns true if the animation is looping.
     * @return true if looping, false otherwise
     */
    boolean isLoop();

    /**
     * Gets the current frame image.
     * @return The current frame as BufferedImage
     */
    BufferedImage getCurrentFrame();
}
