import org.junit.jupiter.api.Test;
import pieces.Position;
import viewUtils.board.SelectionRenderer;

import java.awt.*;
import java.awt.Graphics2D;
import java.util.List;

import static org.mockito.Mockito.*;

class SelectionRendererTest {

    @Test
    void drawRendersSelectionAndMoves() {
        Graphics2D g2 = mock(Graphics2D.class);
        Position selected = new Position(1, 2);
        List<Position> moves = List.of(new Position(3, 3), new Position(4, 5));
        Color color = Color.GREEN;

        SelectionRenderer.draw(g2, selected, moves, color, 8, 8, 400, 400);

        verify(g2).setColor(color);
        int cellW = 400 / 8;
        int cellH = 400 / 8;
        verify(g2).fillRect(2 * cellW, 1 * cellH, cellW, cellH);
        verify(g2).fillOval(3 * cellW + cellW / 4, 3 * cellH + cellH / 4, cellW / 2, cellH / 2);
        verify(g2).fillOval(5 * cellW + cellW / 4, 4 * cellH + cellH / 4, cellW / 2, cellH / 2);
    }

    @Test
    void drawWithNullSelectionDoesNothing() {
        Graphics2D g2 = mock(Graphics2D.class);
        SelectionRenderer.draw(g2, null, List.of(new Position(1, 1)), Color.RED, 8, 8, 400, 400);
        verifyNoInteractions(g2);
    }
}