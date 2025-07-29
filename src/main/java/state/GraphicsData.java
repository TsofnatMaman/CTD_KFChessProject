package state;

import interfaces.EState;
import interfaces.IGraphicsData;
import pieces.Position;

import java.awt.image.BufferedImage;

/**
 * Handles graphics data for piece animation.
 */
public class GraphicsData implements IGraphicsData {
    private BufferedImage[] frames;
    private int totalFrames;
    private int currentFrame;
    private double framesPerSec;
    private boolean isLoop;
    private long lastFrameTimeNanos;

    /**
     * Constructs GraphicsData for piece animation.
     * @param frames Array of animation frames
     * @param framesPerSec Number of frames per second
     * @param isLoop Whether the animation should loop
     */
    public GraphicsData(BufferedImage[] frames, double framesPerSec, boolean isLoop) {
        this.frames = frames;
        this.totalFrames = frames.length;
        this.framesPerSec = framesPerSec;
        this.isLoop = isLoop;
        this.currentFrame = 0;
        this.lastFrameTimeNanos = System.nanoTime();
    }

    /**
     * Resets the animation to the first frame.
     * @param state The new state
     * @param to The target position
     */
    @Override
    public void reset(EState state, Position to) {
        // Reset only when switching to a new state
        this.currentFrame = 0;
        this.lastFrameTimeNanos = System.nanoTime();
    }

    /**
     * Updates the animation frame based on elapsed time.
     */
    @Override
    public void update() {
        long now = System.nanoTime();
        double elapsedSec = (now - lastFrameTimeNanos) / 1_000_000_000.0;

        if (elapsedSec >= 1.0 / framesPerSec) {
            currentFrame++;
            lastFrameTimeNanos = now;

            if (currentFrame >= totalFrames) {
                currentFrame = isLoop ? 0 : totalFrames - 1;
            }
        }
    }

    /**
     * Checks if the animation has finished (for non-looping states).
     * @return true if finished, false otherwise
     */
    @Override
    public boolean isAnimationFinished() {
        return !isLoop && currentFrame >= totalFrames - 1;
    }

    /**
     * Gets the current frame number.
     * @return The current frame index
     */
    @Override
    public int getCurrentNumFrame() {
        return currentFrame;
    }

    /**
     * Gets the total number of frames.
     * @return The total number of frames
     */
    @Override
    public int getTotalFrames() {
        return totalFrames;
    }

    /**
     * Gets the frames per second for the animation.
     * @return Frames per second
     */
    @Override
    public double getFramesPerSec() {
        return framesPerSec;
    }

    /**
     * Returns true if the animation is looping.
     * @return true if looping, false otherwise
     */
    @Override
    public boolean isLoop() {
        return isLoop;
    }

    /**
     * Gets the current frame image.
     * @return The current frame as BufferedImage
     */
    @Override
    public BufferedImage getCurrentFrame() {
        return frames[currentFrame];
    }
}
