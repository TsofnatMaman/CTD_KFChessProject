package webSocket.client;

import pieces.Position;
import moves.Move;
import webSocket.server.dto.GameDelta;
import webSocket.server.dto.PieceDTO;

import java.util.List;

public class GameModel {
    private List<PieceDTO> pieces;
    private Position selectedPiece;
    private List<Position> legalMoves;
    private String status;
    private String winner;
    private long elapsedTime;

    public void updateFromDelta(GameDelta delta) {
        this.pieces = delta.getPieces();
        this.selectedPiece = delta.getSelectedPiece();
        this.legalMoves = delta.getLegalMoves();
        this.status = delta.getStatus();
        this.winner = delta.getWinner();
        this.elapsedTime = delta.getElapsedTime();
    }

    // גטרים (או שאתה יכול להשתמש ב-PropertyChangeListener לעדכון UI)
    public List<PieceDTO> getPieces() { return pieces; }
    public Position getSelectedPiece() { return selectedPiece; }
    public List<Position> getLegalMoves() { return legalMoves; }
    public String getStatus() { return status; }
    public String getWinner() { return winner; }
    public long getElapsedTime() { return elapsedTime; }
}
