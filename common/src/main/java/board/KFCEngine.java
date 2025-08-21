package board;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.listeners.ActionData;
import interfaces.IBoard;
import interfaces.IBoardEngine;
import interfaces.IPiece;
import interfaces.IPlayer;
import moves.Data;
import pieces.EPieceType;
import pieces.Position;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * KFCEngine is the game rules engine handling:
 * - Move legality
 * - Jump legality
 * - Path checking
 * - Promotion
 * - Piece updates including capture handling
 */
public class KFCEngine implements IBoardEngine {

    /**
     * Checks if a move from one position to another is legal.
     *
     * @param board The game board
     * @param from  Starting position
     * @param to    Target position
     * @return true if the move is legal
     */
    @Override
    public boolean isMoveLegal(IBoard board, Position from, Position to) {
        if (!board.isInBounds(from) || !board.isInBounds(to)) return false;

        IPiece fromPiece = board.getPiece(from);
        if (fromPiece == null || !fromPiece.canAction()) return false;

        int dx = to.getRow() - from.getRow();
        int dy = to.getCol() - from.getCol();
        Data data = new Data(board, fromPiece, to);

        // Check if the move matches any allowed move for the piece
        boolean matchesMove = fromPiece.getMoves().stream()
                .anyMatch(m -> m.dx() == dx && m.dy() == dy &&
                        (m.condition() == null || Arrays.stream(m.condition())
                                .allMatch(c -> c.isCanMove(data))));

        if (!matchesMove) return false;

        // For pieces that cannot skip, ensure the path is clear
        if (!fromPiece.getType().isCanSkip() && !isPathClear(board, from, to)) return false;

        // Target square legality
        IPiece toPiece = board.getPiece(to);
        return (toPiece == null || fromPiece.getPlayer() != toPiece.getPlayer()) &&
                fromPiece.getPlayer() != board.getTarget(to);
    }

    /**
     * Checks if a piece can perform a jump.
     *
     * @param board The game board
     * @param pos   Position of the piece
     * @return true if piece can jump
     */
    @Override
    public boolean isJumpLegal(IBoard board, Position pos) {
        IPiece piece = board.getPiece(pos);
        return piece != null && piece.canAction();
    }

    /**
     * Returns all legal moves for a piece at a given position.
     *
     * @param board The game board
     * @param pos   Position of the piece
     * @return List of legal target positions
     */
    @Override
    public List<Position> getLegalMoves(IBoard board, Position pos) {
        IPiece piece = board.getPiece(pos);
        if (piece == null || piece.isCaptured()) return List.of();

        return piece.getMoves().stream()
                .map(m -> pos.add(m.dx(), m.dy()))
                .filter(to -> isMoveLegal(board, pos, to))
                .collect(Collectors.toList());
    }

    /**
     * Checks if the path between two positions is clear (no pieces blocking).
     *
     * @param board The game board
     * @param from  Starting position
     * @param to    Target position
     * @return true if path is clear
     */
    private boolean isPathClear(IBoard board, Position from, Position to) {
        int stepRow = Integer.signum(to.dy(from));
        int stepCol = Integer.signum(to.dx(from));
        Position current = from.add(stepRow, stepCol);

        while (!current.equals(to)) {
            if (board.hasPiece(current)) return false;
            current = current.add(stepRow, stepCol);
        }
        return true;
    }

    /**
     * Handles promotion if the piece reaches the last row.
     *
     * @param board  The game board
     * @param player The player owning the piece
     * @param piece  The piece to check for promotion
     * @param pos    Target position
     * @return Promoted piece or original piece
     */
    private IPiece handleIfPromotion(IBoard board, IPlayer player, IPiece piece, Position pos) {
        if (piece.getType() != EPieceType.P) return piece;

        int lastRow = piece.getPlayer() == 0 ? board.getRows() - 1 : 0;
        if (pos.getRow() == lastRow)
            return player.replacePToQ(piece, pos, board.getBoardConfig());

        return piece;
    }

    /**
     * Updates the piece state: handles finished actions, captures, promotions, and calls piece.update().
     *
     * @param board  The game board
     * @param player The player owning the piece
     * @param piece  The piece to update
     * @param now    Current time in nanoseconds
     */
    @Override
    public void handleUpdatePiece(IBoard board, IPlayer player, IPiece piece, long now) {
        if (piece.isCaptured()) return;

        // Only update if current action is finished
        if (piece.getCurrentState().isActionFinished(now)) {
            Position targetPos = piece.getCurrentState().getPhysics().getTargetPos();
            IPiece target = board.getPiece(targetPos);

            // Handle capture
            if (target != null && target != piece && !target.isCaptured()) {
                if (target.isCapturable())
                    board.getPlayers()[target.getPlayer()].markPieceCaptured(target);
                else
                    board.getPlayers()[piece.getPlayer()].markPieceCaptured(piece);

                EventPublisher.getInstance().publish(EGameEvent.PIECE_CAPTURED,
                        new GameEvent(EGameEvent.PIECE_CAPTURED,
                                new ActionData(piece.getPlayer(), null)));
            }

            // Move piece to target and handle promotion
            board.setGrid(targetPos, handleIfPromotion(board, player, piece, targetPos));
            board.setIsNoTarget(targetPos);
        }

    }
}
