package player;

import board.BoardConfig;
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
 * Factory responsible for creating Player instances based on board configuration
 * and the initial piece layout stored in LoadPieces.board.
 */
public class PlayerFactory {

    /**
     * Creates a single Player by reading the initial piece codes from LoadPieces.board
     * and materializing IPiece instances.
     *
     * @param id   Player ID (0 or 1 for current two-player setup)
     * @param name Player name
     * @param bc   Board configuration
     * @return IPlayer constructed with its initial pieces
     */
    public static IPlayer createPlayer(int id, String name, BoardConfig bc) {
        if (id < 0) throw new IllegalArgumentException("Player id must be >= 0");

        // Determine the player's color from the predefined palette
        Color[] colorPalette = constants.PlayerConstants.PLAYER_COLORS;
        Color playerColor = id < colorPalette.length ? colorPalette[id] : Color.WHITE;

        // List to hold all pieces for this player
        List<IPiece> playerPieces = new ArrayList<>();

        // Iterate over all starting rows assigned to this player
        for (int startRow : BoardConfig.rowsOfPlayer.get(id)) {
            for (int col = 0; col < constants.GameConstants.BOARD_COLS; col++) {

                // Get the string code for the piece at this position
                String pieceCodeStr = game.LoadPieces.board[startRow][col];
                if (pieceCodeStr == null || pieceCodeStr.isEmpty()) continue;

                // Extract the first character as the piece type
                String typeChar = String.valueOf(pieceCodeStr.charAt(0));
                EPieceType pieceType;

                try {
                    pieceType = EPieceType.valueOf(typeChar);
                } catch (IllegalArgumentException e) {
                    LogUtils.logDebug(
                            "Unknown piece code '" + typeChar + "' at row " + startRow + ", col " + col + " for player " + id
                    );
                    continue;
                }

                // Create the position object for the piece
                Position piecePosition = new Position(startRow, col);

                // Create the piece using the PiecesFactory
                IPiece piece = PiecesFactory.createPieceByCode(pieceType, id, piecePosition, bc);

                if (piece != null) {
                    playerPieces.add(piece);
                } else {
                    LogUtils.logDebug(
                            "Failed to create piece of type " + pieceType + " for player " + id + " at " + piecePosition
                    );
                }
            }
        }

        // Construct and return the Player instance
        return new Player(id, name, playerColor, playerPieces);
    }

    /**
     * Convenience helper to create multiple players given their names.
     *
     * @param names Array of player names; expected length >= 2
     * @param bc    Board configuration
     * @return Array of IPlayer instances
     */
    public static IPlayer[] createPlayers(String[] names, BoardConfig bc) {
        return java.util.stream.IntStream.range(0, names.length)
                .mapToObj(i -> createPlayer(i, names[i], bc))
                .toArray(IPlayer[]::new);
    }
}
