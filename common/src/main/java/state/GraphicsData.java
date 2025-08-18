package state;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import interfaces.IGraphicsData;

import java.awt.image.BufferedImage;

/**
 * Represents the graphics data used for piece animation, including frame management and timing.
 * This class handles the animation state for graphical objects, such as chess pieces.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraphicsData implements IGraphicsData {
    /**
     * The array of animation frames (images). Not serialized.
     */
    @JsonIgnore
    private BufferedImage[] frames;

    /**
     * The total number of frames in the animation.
     */
    private int totalFrames;

    /**
     * The current frame index being displayed.
     */
    private int currentFrame;

    /**
     * The number of frames to display per second.
     */
    @JsonProperty("frames_per_sec")
    private double framesPerSec;

    /**
     * The timestamp (in nanoseconds) of the last frame update.
     */
    private long lastFrameTimeNanos;


    /**
     * Default constructor. Initializes the animation to the first frame and sets the last frame time to now.
     */
    public GraphicsData(){
        currentFrame=0;
        this.lastFrameTimeNanos = System.nanoTime();
    }

    /**
     * Constructs a GraphicsData object for piece animation.
     *
     * @param frames       Array of animation frames (BufferedImage)
     * @param framesPerSec Number of frames to display per second
     */
    public GraphicsData(BufferedImage[] frames, double framesPerSec) {
        this.frames = frames;
        this.totalFrames = frames.length;
        this.framesPerSec = framesPerSec;
        this.currentFrame = 0;
        this.lastFrameTimeNanos = System.nanoTime();
    }

    /**
     * Resets the animation to the first frame and updates the last frame time.
     * Should be called when switching to a new animation state.
     */
    @Override
    public void reset() {
        this.currentFrame = 0;
        this.lastFrameTimeNanos = System.nanoTime();
    }

    /**
     * Updates the animation frame based on the elapsed time since the last update.
     *
     * @param now The current time in nanoseconds
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
     * Returns the current frame number (index).
     *
     * @return The current frame index
     */
    @Override
    public int getCurrentNumFrame() {
        return currentFrame;
    }

    /**
     * Returns the total number of frames in the animation.
     *
     * @return The total number of frames
     */
    @Override
    public int getTotalFrames() {
        return totalFrames;
    }

    /**
     * Returns the number of frames per second for the animation.
     *
     * @return Frames per second
     */
    @Override
    public double getFramesPerSec() {
        return framesPerSec;
    }

    /**
     * Returns the current frame image as a BufferedImage.
     *
     * @return The current frame as BufferedImage
     */
    @Override
    public BufferedImage getCurrentFrame() {
        return frames[currentFrame];
    }

    /**
     * Returns the current frame index.
     *
     * @return The current frame index
     */
    @Override
    public int getCurrentFrameIdx(){
        return currentFrame;
    }

    /**
     * Sets the array of animation frames.
     *
     * @param frames The array of BufferedImage frames
     */
    @Override
    public void setFrames(BufferedImage[] frames) {
        this.frames = frames;
    }

    /**
     * Sets the total number of frames in the animation.
     *
     * @param totalFrames The total number of frames
     */
    @Override
    public void setTotalFrames(int totalFrames) {
        this.totalFrames = totalFrames;
    }
}
