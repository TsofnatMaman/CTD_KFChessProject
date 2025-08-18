package state;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import interfaces.IGraphicsData;
import pieces.Position;

import java.awt.image.BufferedImage;

/**
 * Handles graphics data for piece animation.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraphicsData implements IGraphicsData {
    @JsonIgnore
    private BufferedImage[] frames;
    private int totalFrames;
    private int currentFrame;
    @JsonProperty("frames_per_sec")
    private double framesPerSec;
    private long lastFrameTimeNanos;

    public GraphicsData(){
        currentFrame=0;
        this.lastFrameTimeNanos = System.nanoTime();
    }

    /**
     * Constructs GraphicsData for piece animation.
     * @param frames Array of animation frames
     * @param framesPerSec Number of frames per second
     */
    public GraphicsData(BufferedImage[] frames, double framesPerSec) {
        this.frames = frames;
        this.totalFrames = frames.length;
        this.framesPerSec = framesPerSec;
        this.currentFrame = 0;
        this.lastFrameTimeNanos = System.nanoTime();
    }

    @Override
    public void reset() {
        // Reset only when switching to a new state
        this.currentFrame = 0;
        this.lastFrameTimeNanos = System.nanoTime();
    }

    /**
     * Updates the animation frame based on elapsed time.
     */
    @Override
    public void update(long now) {
        double elapsedSec = (now - lastFrameTimeNanos) / 1_000_000_000.0;

        if (elapsedSec >= 1.0 / framesPerSec) {
            currentFrame = (currentFrame+1)%totalFrames;
            lastFrameTimeNanos = now;
        }
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
     * Gets the current frame image.
     * @return The current frame as BufferedImage
     */
    @Override
    public BufferedImage getCurrentFrame() {
        return frames[currentFrame];
    }

    @Override
    public int getCurrentFrameIdx(){
        return currentFrame;
    }

    @Override
    public void setFrames(BufferedImage[] frames) {
        this.frames = frames;
    }

    @Override
    public void setTotalFrames(int totalFrames) {
        this.totalFrames = totalFrames;
    }
}
