package state;

import board.BoardConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pieces.Position;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class PhysicsDataTest {

    private PhysicsData physics;
    private BoardConfig bc;
    private Position startPos;
    private Position targetPos;
    private long startTime;

    @BeforeEach
    void setup() {
        // Use a very small speed to make tests deterministic
        physics = new PhysicsData(0.001);

        // Initialize a simple BoardConfig
        bc = new BoardConfig(
                new Dimension(100, 100), // physics dimension
                new Dimension(10, 10),   // grid dimension
                new Dimension(800, 800)  // screen dimension
        );

        startPos = new Position(0, 0);
        targetPos = new Position(1, 0); // one cell to the right

        startTime = 0;
        // Reset PhysicsData with start and target positions
        physics.reset(EState.IDLE, startPos, targetPos, bc, startTime);
    }

    @Test
    void testIsActionFinishedTrueAfterEnoughTime() {
        // Calculate the distance in physics units
        double dx = targetPos.dx(startPos) * (bc.physicsDimension().getWidth() / bc.gridDimension().getWidth());
        // Compute time needed to reach target
        long enoughTime = (long) ((dx / physics.getSpeedMetersPerSec()) * 1_000_000_000L);

        physics.update(enoughTime);

        // After enough time, action should be finished
        assertTrue(physics.isActionFinished(enoughTime), "Should be finished after enough time");
    }
}
