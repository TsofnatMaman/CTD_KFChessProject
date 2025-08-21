package state;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import interfaces.IGraphicsData;

import java.awt.image.BufferedImage;

/**
 * Manages graphics and animation data for a piece.
 * Handles frame updates, timing, and provides the current frame for rendering.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraphicsData implements IGraphicsData {

    /** Array of animation frames (not serialized). */
    @JsonIgnore
    private BufferedImage[] frames;

    /** Total number of frames in the animation. */
    private int totalFrames;

    /** Current frame index. */
    private int currentFrame;

    /** Frames per second for animation. */
    @JsonProperty("frames_per_sec")
    private double framesPerSec;

    /** Timestamp in nanoseconds of the last frame update. */
    private long lastFrameTimeNanos;

    /** Default constructor initializing frame index and timestamp. */
    public GraphicsData() {
        this.currentFrame = 0;
        this.lastFrameTimeNanos = System.nanoTime();
    }

    /**
     * Constructs GraphicsData with specified frames and frame rate.
     *
     * @param frames Array of BufferedImage frames
     * @param framesPerSec Animation speed in frames per second
     */
    public GraphicsData(BufferedImage[] frames, double framesPerSec) {
        this.frames = frames;
        this.totalFrames = frames.length;
        this.framesPerSec = framesPerSec;
        this.currentFrame = 0;
        this.lastFrameTimeNanos = System.nanoTime();
    }

    /** Resets animation to first frame and updates timestamp. */
    @Override
    public void reset() {
        this.currentFrame = 0;
        this.lastFrameTimeNanos = System.nanoTime();
    }

    /**
     * Updates the animation based on elapsed time.
     *
     * @param now Current time in nanoseconds
     */
    @Override
    public void update(long now) {
        double elapsedSec = (now - lastFrameTimeNanos) / 1_000_000_000.0;
        if (elapsedSec >= 1.0 / framesPerSec) {
            currentFrame = (currentFrame + 1) % totalFrames;
            lastFrameTimeNanos = now;
        }
    }

    /** Returns the current frame index. */
    @Override
    public int getCurrentNumFrame() {
        return currentFrame;
    }

    /** Returns the total number of frames. */
    @Override
    public int getTotalFrames() {
        return totalFrames;
    }

    /** Returns the animation frame rate. */
    @Override
    public double getFramesPerSec() {
        return framesPerSec;
    }

    /** Returns the current frame image. */
    @Override
    public BufferedImage getCurrentFrame() {
        return frames[currentFrame];
    }

    /** Returns the current frame index (duplicate method). */
    @Override
    public int getCurrentFrameIdx() {
        return currentFrame;
    }

    /** Sets the frames array for animation. */
    @Override
    public void setFrames(BufferedImage[] frames) {
        this.frames = frames;
    }

    /** Sets the total number of frames. */
    @Override
    public void setTotalFrames(int totalFrames) {
        this.totalFrames = totalFrames;
    }

    /** Sets the last frame update timestamp in nanoseconds. */
    @Override
    public void setLastFrameTimeNanos(long nanos) {
        this.lastFrameTimeNanos = nanos;
    }
}
