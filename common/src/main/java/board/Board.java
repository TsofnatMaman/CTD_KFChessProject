package board;

import command.IllegalCmdException;
import interfaces.*;
import pieces.Position;

import java.util.Arrays;
import java.util.List;

/**
 * Board manages the state of pieces, players, and targets,
 * but does not contain move legality logic.
 */
public class Board implements IBoard {

    private final IPiece[][] boardGrid;
    private final int[][] isTarget;
    public final IPlayer[] players;
    public final BoardConfig boardConfig;
    private final IBoardEngine boardRulesEngine;
    public final int IS_NO_TARGET = -1;

    public Board(BoardConfig bc, IBoardEngine rulesEngine, IPlayer[] players) {
        this.boardConfig = bc;
        this.boardRulesEngine = rulesEngine;
        this.players = players;

        this.boardGrid = new IPiece[(int) bc.gridDimension().getWidth()][(int) bc.gridDimension().getHeight()];
        this.isTarget = new int[boardGrid.length][boardGrid[0].length];
        for (int[] row : isTarget) Arrays.fill(row, IS_NO_TARGET);

        initializeFromPlayers();
    }

    private void initializeFromPlayers() {
        for (IPlayer p : players) {
            for (IPiece piece : p.getPieces()) {
                boardGrid[piece.getPos().getRow()][piece.getPos().getCol()] = piece;
            }
        }
    }

    @Override
    public boolean hasPiece(Position pos) {
        return isInBounds(pos) && boardGrid[pos.getRow()][pos.getCol()] != null;
    }

    @Override
    public boolean hasPieceOrIsTarget(Position pos) {
        return hasPiece(pos) || isTarget[pos.getRow()][pos.getCol()] != IS_NO_TARGET;
    }

    @Override
    public IPiece getPiece(Position pos) {
        return isInBounds(pos) ? boardGrid[pos.getRow()][pos.getCol()] : null;
    }

    @Override
    public int getPlayerOf(Position pos) {
        return BoardConfig.getPlayerOf(pos.getRow());
    }

    @Override
    public void move(Position from, Position to) {
        if (!boardRulesEngine.isMoveLegal(this, from, to)) throw new IllegalCmdException("Move invalid from "+from+" to "+to);

        IPiece piece = getPiece(from);
        boardGrid[from.getRow()][from.getCol()] = null;
        isTarget[to.getRow()][to.getCol()] = piece.getPlayer();
        piece.move(to);
    }

    @Override
    public void jump(IPiece piece) {
        if(!boardRulesEngine.isJumpLegal(this, piece.getPos())) throw new IllegalCmdException(piece.toString());
        piece.jump();
    }

    @Override
    public void updateAll() {
        long now = System.nanoTime();

        for (IPlayer player : players) {
            for (IPiece piece : player.getPieces()) {
                boardRulesEngine.handleUpdatePiece(this, player, piece, now);
            }
        }
    }


    @Override
    public boolean isInBounds(Position p) {
        return boardConfig.isInBounds(p.getRow(), p.getCol());
    }

    @Override
    public IPlayer[] getPlayers() {
        return players;
    }

    @Override
    public int getCols() {
        return (int) boardConfig.gridDimension().getHeight();
    }

    @Override
    public int getRows() {
        return (int) boardConfig.gridDimension().getWidth();
    }

    @Override
    public BoardConfig getBoardConfig() {
        return boardConfig;
    }

    @Override
    public List<Position> getLegalMoves(Position selectedPosition) {
        if (selectedPosition == null || !isInBounds(selectedPosition)) return List.of();
        return boardRulesEngine.getLegalMoves(this, selectedPosition);
    }

    @Override
    public int getTarget(Position pos) {
        return isTarget[pos.getRow()][pos.getCol()];
    }

    @Override
    public void setGrid(Position pos, IPiece piece){
        boardGrid[pos.getRow()][pos.getCol()] = piece;
    }

    @Override
    public void setIsNoTarget(Position pos){
        isTarget[pos.getRow()][pos.getCol()] = IS_NO_TARGET;
    }

    @Override
    public IBoardEngine getBoardRulesEngine() {
        return boardRulesEngine;
    }
}