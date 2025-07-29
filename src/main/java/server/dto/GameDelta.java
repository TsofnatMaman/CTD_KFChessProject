package server.dto;

import interfaces.IBoard;
import interfaces.IGame;
import interfaces.IPiece;
import interfaces.IPlayer;
import moves.Move;
import pieces.Position;

import java.util.ArrayList;
import java.util.List;

public class GameDelta {
    private final List<PieceDTO> pieces;
    private final int playerId;          // מי הלקוח שיקבל את המידע
    private final String status;         // RUNNING / ENDED
    private final String winner;         // מי ניצח, אם קיים
    private final long elapsedTime;

    // פרטים עבור הבחירה של השחקן שמקבל את המידע
    private final Position selectedPiece;   // כלי שנבחר (null אם לא נבחר)
    private final List<Position> legalMoves;    // מהלכים חוקיים לכלי שנבחר

    // בעתיד ניתן להוסיף הודעות, תור שחקן וכדומה
    // כרגע אין להם שדות במחלקה זו

    public GameDelta(
            List<PieceDTO> pieces,
            int playerId,
            String status,
            String winner,
            long elapsedTime,
            Position selectedPiece,
            List<Position> legalMoves
    ) {
        this.pieces = pieces;
        this.playerId = playerId;
        this.status = status;
        this.winner = winner;
        this.elapsedTime = elapsedTime;
        this.selectedPiece = selectedPiece;
        this.legalMoves = legalMoves;
    }

    public List<PieceDTO> getPieces() {
        return pieces;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getStatus() {
        return status;
    }

    public String getWinner() {
        return winner;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public List<Position> getLegalMoves() {
        return legalMoves;
    }

    public Position getSelectedPiece() {
        return selectedPiece;
    }

    // Static factory method to create GameDelta from the current game state and player id
    public static GameDelta fromGame(IGame game, int playerId) {
        IPlayer player = game.getPlayerById(playerId);
        IBoard board = game.getBoard();

        List<PieceDTO> pieceDTOs = new ArrayList<>();
        for (IPlayer plyr : board.getPlayers()) {
            for (IPiece piece : plyr.getPieces()) {
                pieceDTOs.add(new PieceDTO(
                        piece.getId(),
                        piece.getPos().getRow(),
                        piece.getPos().getCol(),
                        piece.getType().name(),
                        piece.getPlayer(),
                        piece.getCurrentStateName().toString(),
                        piece.getCurrentState().getGraphics().getCurrentFrameIdx()
                ));
            }
        }

        List<Position> legalMoves = new ArrayList<>();
        Position selectedPiece = null;

        if (player.getPendingFrom() != null) {
            selectedPiece = player.getPendingFrom();
            // אם יש כלי נבחר, נספוג את המהלכים החוקיים שלו
            legalMoves.addAll(board.getLegalMoves(player.getPendingFrom()));
        }

        String status = (game.win() == null) ? "RUNNING" : "ENDED";
        String winner = (game.win() == null) ? null : game.win().getName();

        return new GameDelta(
                pieceDTOs,
                playerId,
                status,
                winner,
                game.getElapsedTimeMillis(),
                selectedPiece,
                legalMoves
        );
    }
}
