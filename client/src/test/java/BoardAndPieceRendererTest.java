import board.BoardConfig;
import dto.PieceView;
import org.junit.jupiter.api.Test;
import viewUtils.board.BoardRenderer;
import viewUtils.board.PieceRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BoardAndPieceRendererTest {

    @Test
    void pieceRenderer_drawsPieceOntoGraphicsBuffer() {
        // Prepare target image (board render surface)
        BufferedImage target = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = target.createGraphics();

        // Create a solid colored frame for the piece (so we can detect pixels)
        BufferedImage frame = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D fg = frame.createGraphics();
        fg.setColor(new Color(255, 0, 0, 255)); // fully opaque red
        fg.fillRect(0, 0, 10, 10);
        fg.dispose();

        // Create a PieceView with x,y inside canvas
        PieceView p = new PieceView(frame, 20.0, 30.0);

        // Draw the piece scaled to square 40x40
        assertDoesNotThrow(() -> PieceRenderer.draw(g, p, 40, 40));
        g.dispose();

        // The pixel at piece location should now have non-zero alpha
        int px = target.getRGB(20, 30);
        int alpha = (px >> 24) & 0xff;
        assertTrue(alpha > 0, "Expected non-transparent pixel where piece was drawn");
    }

    @Test
    void boardRenderer_drawsMultiplePiecesWithoutException() {
        // Mock BoardConfig
        BoardConfig mockConfig = mock(BoardConfig.class);
        when(mockConfig.panelDimension()).thenReturn(new Dimension(500, 500));
        when(mockConfig.gridDimension()).thenReturn(new Dimension(50, 50));

        // Mock Graphics
        Graphics g = mock(Graphics.class);

        // Example PieceViews
        PieceView p1 = mock(PieceView.class);
        PieceView p2 = mock(PieceView.class);

        // Test that draw() does not throw
        assertDoesNotThrow(() -> {
            BoardRenderer.draw(g, List.of(p1, p2), mockConfig);
        });
    }
}
