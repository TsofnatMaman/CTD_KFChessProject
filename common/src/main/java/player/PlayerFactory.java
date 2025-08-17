package player;

import board.BoardConfig;
import constants.PieceConstants;
import interfaces.IPiece;
import interfaces.IPlayer;
import pieces.EPieceType;
import pieces.PiecesFactory;
import pieces.Position;
import utils.LogUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory responsible for creating Player instances based on board configuration and the initial piece layout.
 */
public class PlayerFactory {

    /**
     * Creates a single Player by reading the initial piece codes from LoadPieces and materializing IPiece instances.
     *
     * @param id       Player ID (must be 0 or 1 for current two-player setup)
     * @param name     Player name
     * @param bc       Board configuration
     * @return IPlayer constructed with its initial pieces
     */
    public static IPlayer createPlayer(int id, String name, BoardConfig bc) {
        if (id < 0) throw new IllegalArgumentException("Player id must be >= 0");
        // Defensive guard in case more colors are added later
        Color[] palette = constants.PlayerConstants.PLAYER_COLORS;
        Color color = id < palette.length ? palette[id] : Color.WHITE;

        List<interfaces.IPiece> pieces = new ArrayList<>();

        // For each starting row assigned to this player, instantiate pieces from LoadPieces.board
        for (int row : BoardConfig.rowsOfPlayer.get(id)) {
            for (int col = 0; col < constants.GameConstants.BOARD_COLS; col++) {
                String pieceCodeStr = game.LoadPieces.board[row][col];
                if (pieceCodeStr == null || pieceCodeStr.isEmpty()) continue;

                // The original code used the first char as the piece type, e.g. "B" from "BB"
                String codeChar = String.valueOf(pieceCodeStr.charAt(0));
                EPieceType type;
                try {
                    type = EPieceType.valueOf(codeChar);
                } catch (IllegalArgumentException e) {
                    LogUtils.logDebug("Unknown piece code '" + codeChar + "' at " + row + "," + col + " for player " + id);
                    continue;
                }

                Position pos = new Position(row, col);
                IPiece piece = PiecesFactory.createPieceByCode(
                        type,
                        id,
                        pos,
                        bc
                );
                if (piece != null) {
                    pieces.add(piece);
                } else {
                    LogUtils.logDebug("Failed to create piece of type " + type + " for player " + id + " at " + pos);
                }
            }
        }

        return new Player(id, name, color, pieces);
    }

    /**
     * Convenience helper to create both players given their names.
     *
     * @param names array of player names; expected length >= 2, uses first two entries for players 0 and 1
     * @param bc    Board configuration
     * @return array of two IPlayer instances
     */
    public static IPlayer[] createPlayers(String[] names, BoardConfig bc) {
        return java.util.stream.IntStream.range(0, names.length)
                .mapToObj(i -> createPlayer(i, names[i], bc))
                .toArray(IPlayer[]::new);
    }
}
