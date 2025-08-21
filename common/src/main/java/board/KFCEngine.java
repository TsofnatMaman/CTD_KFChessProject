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
 * Rules engine: all move, jump, and promotion legality checks.
 */
public class KFCEngine implements IBoardEngine {

    @Override
    public boolean isMoveLegal(IBoard board, Position from, Position to) {
        if(!board.isInBounds(from) || !board.isInBounds(to)) return false;

        IPiece fromPiece = board.getPiece(from);
        if (fromPiece == null || !fromPiece.canAction()) return false;

        int dx = to.getRow() - from.getRow();
        int dy = to.getCol() - from.getCol();
        Data data = new Data(board, fromPiece, to);

        boolean matchesMove = fromPiece.getMoves().stream()
                .anyMatch(m -> m.dx() == dx && m.dy() == dy &&
                        (m.condition() == null || Arrays.stream(m.condition())
                                .allMatch(c -> c.isCanMove(data))));

        if (!matchesMove) return false;

        if (!fromPiece.getType().isCanSkip() && !isPathClear(board, from, to)) return false;

        IPiece toPiece = board.getPiece(to);
        return (toPiece == null || fromPiece.getPlayer() != toPiece.getPlayer()) &&
                fromPiece.getPlayer() != board.getTarget(to);
    }

    @Override
    public boolean isJumpLegal(IBoard board, Position pos) {
        IPiece piece = board.getPiece(pos);
        return piece != null && piece.canAction();
    }

    @Override
    public List<Position> getLegalMoves(IBoard board, Position pos) {
        IPiece piece = board.getPiece(pos);
        if (piece == null || piece.isCaptured()) return List.of();

        return piece.getMoves().stream()
                .map(m -> pos.add(m.dx(), m.dy()))
                .filter(to -> isMoveLegal(board, pos, to))
                .collect(Collectors.toList());
    }

    private boolean isPathClear(IBoard board, Position from, Position to) {
        int dRow = Integer.signum(to.dy(from));
        int dCol = Integer.signum(to.dx(from));
        Position current = from.add(dRow, dCol);

        while (!current.equals(to)) {
            if (board.hasPiece(current)) return false;
            current = current.add(dRow, dCol);
        }
        return true;
    }

    private IPiece handleIfPromotion(IBoard board, IPlayer player, IPiece piece, Position pos) {
        if (piece.getType() != EPieceType.P) return piece;
        int lastRow = piece.getPlayer() == 0 ? board.getRows() - 1 : 0;
        if (pos.getRow() == lastRow)
            return player.replacePToQ(piece, pos, board.getBoardConfig());

        return piece;
    }

    @Override
    public void handleUpdatePiece(IBoard board, IPlayer player, IPiece piece, long now){
        if (piece.isCaptured()) return;

        if (piece.getCurrentState().isActionFinished(now)) {
            Position targetPos = piece.getCurrentState().getPhysics().getTargetPos();
            IPiece target = board.getPiece(targetPos);

            // Handle capture
            if (target != null && target != piece && !target.isCaptured()) {
                if (target.canCapturable())
                    board.getPlayers()[target.getPlayer()].markPieceCaptured(target);
                else
                    board.getPlayers()[piece.getPlayer()].markPieceCaptured(piece);

                EventPublisher.getInstance().publish(EGameEvent.PIECE_CAPTURED,
                        new GameEvent(EGameEvent.PIECE_CAPTURED,
                                new ActionData(piece.getPlayer(), null)));
            }

            // Move piece
            // Check promotion
            board.setGrid(targetPos, handleIfPromotion(board, player, piece, targetPos));
            board.setIsNoTarget(targetPos);
        }
        piece.update(now);
    }
}
