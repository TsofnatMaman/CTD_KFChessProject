package moves;

import pieces.EPieceType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the legal moves for a piece type.
 */
public class Moves {


    public static List<Move> createMovesList(EPieceType pieceType, int playerId) throws IOException {
        List<Move> moves = new ArrayList<>();
        moves = new ArrayList<>();

        String resourcePath = "pieces/" + pieceType.getVal() + "/moves" + playerId + ".txt";
        try (InputStream is = Moves.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[]parts = line.trim().split(":");
                    String[] steps = parts[0].split(",");
                    if (steps.length == 2) {
                        int dx = Integer.parseInt(steps[0]);
                        int dy = Integer.parseInt(steps[1]);
                        moves.add(new Move(dx, dy, parts.length<2?null: ECondition.valueOf(parts[1].toUpperCase())));
                    }
                }
            }
        }
        return moves;
    }
}
