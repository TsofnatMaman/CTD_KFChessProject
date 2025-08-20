package moves;

import pieces.EPieceType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the legal moves for a piece type.
 * Loads moves from piece-specific resource files.
 */
public class Moves {

    /**
     * Creates a list of moves for a given piece type and player.
     *
     * @param pieceType The type of piece
     * @param playerId  The player ID (used to differentiate move files)
     * @return List of legal Move objects
     * @throws IOException If the move file cannot be read
     */
    public static List<Move> createMovesList(EPieceType pieceType, int playerId) throws IOException {
        List<Move> moves = new ArrayList<>();

        // Build resource path from constants
        String resourcePath = constants.PieceConstants.PIECE_MOVES_PATH_PREFIX +
                pieceType.getVal() +
                constants.PieceConstants.PIECE_MOVES_PATH_SUFFIX +
                playerId +
                constants.PieceConstants.PIECE_MOVES_PATH_EXT;

        try (InputStream is = Moves.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException(
                        utils.ConfigLoader.getMessage("resource.not.found", "Resource not found: ") + resourcePath
                );
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.trim().split(constants.PieceConstants.CONDITION_SEPARATOR);
                    String[] steps = parts[0].split(constants.PieceConstants.POSITION_SEPARATOR);

                    if (steps.length == 2) {
                        int dx = Integer.parseInt(steps[0]);
                        int dy = Integer.parseInt(steps[1]);

                        String[] conditionsStr = parts.length < 2 ? null : parts[1].split(constants.PieceConstants.POSITION_SEPARATOR);
                        ECondition[] conditions = conditionsStr == null ? null :
                                Arrays.stream(conditionsStr)
                                        .map(c -> ECondition.valueOf(c.toUpperCase()))
                                        .toArray(ECondition[]::new);

                        moves.add(new Move(dx, dy, conditions));
                    }
                }
            }
        }

        return moves;
    }
}
