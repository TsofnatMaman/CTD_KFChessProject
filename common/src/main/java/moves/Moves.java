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
 */
public class Moves {


    public static List<Move> createMovesList(EPieceType pieceType, int playerId) throws IOException {
        List<Move> moves = new ArrayList<>();
        moves = new ArrayList<>();

        // Extracted resource path to PieceConstants
        String resourcePath = constants.PieceConstants.PIECE_MOVES_PATH_PREFIX + pieceType.getVal() + constants.PieceConstants.PIECE_MOVES_PATH_SUFFIX + playerId + constants.PieceConstants.PIECE_MOVES_PATH_EXT;
        try (InputStream is = Moves.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException(utils.ConfigLoader.getMessage("resource.not.found", "Resource not found: ") + resourcePath); // extracted to messages.properties
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[]parts = line.trim().split(constants.PieceConstants.CONDITION_SEPARATOR); // extracted separator for conditions
                    String[] steps = parts[0].split(constants.PieceConstants.POSITION_SEPARATOR); // extracted separator for positions
                    if (steps.length == 2) {
                        int dx = Integer.parseInt(steps[0]);
                        int dy = Integer.parseInt(steps[1]);
                        String[]conditions = parts.length<2?null:parts[1].split(constants.PieceConstants.POSITION_SEPARATOR); // extracted separator for conditions
                        moves.add(new Move(dx, dy, conditions == null?null: Arrays.stream(conditions)
                                .map(c -> ECondition.valueOf(c.toUpperCase()))
                                .toArray(ECondition[]::new)));
                    }
                }
            }
        }
        return moves;
    }
}
