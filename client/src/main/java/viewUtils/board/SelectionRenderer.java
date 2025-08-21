package viewUtils.board;

import pieces.Position;

import java.awt.*;
import java.util.List;

public class SelectionRenderer {
    public static void draw(Graphics2D g2, Position selected, List<Position> moves, Color color, int cols, int rows, int width, int height) {
        if (selected == null) return;
        g2.setColor(color);

        int cellW = width / cols;
        int cellH = height / rows;

        g2.fillRect(selected.getCol() * cellW, selected.getRow() * cellH, cellW, cellH);
        for (Position move : moves) {
            int x = move.getCol() * cellW + cellW/4;
            int y = move.getRow() * cellH + cellH/4;
            int w = cellW/2, h = cellH/2;
            g2.fillOval(x, y, w, h);
        }
    }
}
