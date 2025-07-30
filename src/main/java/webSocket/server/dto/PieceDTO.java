package webSocket.server.dto;

import board.BoardConfig;
import board.Dimension;
import interfaces.PiecesFactory;
import pieces.EPieceType;
import pieces.Position;
import interfaces.IPiece;


public class PieceDTO{
    public int row;
    public int col;
    public String type;     // לדוגמה: "P", "Q"
    public int playerId;
    public boolean captured;

    public IPiece toPiece() {
        Position pos = new Position(row, col);
        EPieceType pieceType = EPieceType.valueOf(type); // דורש enum מתאים
        return PiecesFactory.createPieceByCode(pieceType, playerId, pos, new BoardConfig(new Dimension(8), new Dimension(64*8)));
    }

    public static PieceDTO from(IPiece piece){
        PieceDTO ps = new PieceDTO();
        ps.row = piece.getRow();
        ps.col = piece.getCol();
        ps.type = piece.getType().toString();
        ps.playerId = piece.getPlayer();
        ps.captured = piece.isCaptured();
        return ps;
    }
}
