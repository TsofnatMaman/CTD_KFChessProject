package state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class GraphicsDataTest {

    private GraphicsData graphicsData;
    private BufferedImage[] frames;

    @BeforeEach
    void setup() {
        // Create a set of frames for testing
        frames = new BufferedImage[5];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        }
        // Initialize GraphicsData with 2 frames per second
        graphicsData = new GraphicsData(frames, 2.0);
    }

    @Test
    void testResetResetsFrameAndTime() throws InterruptedException {
        // Force an update to advance frame
        graphicsData.update(System.nanoTime() + 1_000_000_000L);
        graphicsData.reset();
        // After reset, the current frame should be 0
        assertEquals(0, graphicsData.getCurrentNumFrame());
    }

    @Test
    void testUpdateAdvancesFrame() {
        // Create a small deterministic frame array
        BufferedImage frame1 = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        BufferedImage frame2 = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        GraphicsData gd = new GraphicsData(new BufferedImage[]{frame1, frame2}, 2); // 2 frames/sec

        // Set last frame time to 0 for deterministic test
        gd.setLastFrameTimeNanos(0);

        // Update before enough time has passed for frame change
        gd.update(400_000_000L); // 0.4 sec, less than 0.5 sec per frame
        assertEquals(0, gd.getCurrentNumFrame());

        // Update after enough time has passed
        gd.update(500_000_001L); // 0.5 sec + epsilon
        assertEquals(1, gd.getCurrentNumFrame());
    }

    @Test
    void testUpdateWrapsAround() {
        long startTime = System.nanoTime();
        // Update enough time to cycle through frames
        graphicsData.update(startTime + 3_000_000_000L);
        int frame = graphicsData.getCurrentNumFrame();
        // Ensure frame index is within bounds
        assertTrue(frame >= 0 && frame < graphicsData.getTotalFrames());
    }

    @Test
    void testGettersReturnCorrectValues() {
        // Validate getter methods
        assertEquals(frames.length, graphicsData.getTotalFrames());
        assertEquals(2.0, graphicsData.getFramesPerSec());
        assertEquals(frames[0], graphicsData.getCurrentFrame());
        assertEquals(0, graphicsData.getCurrentFrameIdx());
    }

    @Test
    void testSettersWork() {
        // Validate setter methods
        BufferedImage[] newFrames = new BufferedImage[3];
        graphicsData.setFrames(newFrames);
        graphicsData.setTotalFrames(newFrames.length);
        assertEquals(3, graphicsData.getTotalFrames());
    }
}
