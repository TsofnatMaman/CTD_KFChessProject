package board;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import interfaces.*;
import moves.Data;
import moves.Move;
import pieces.EPieceType;
import pieces.Position;

import java.util.Arrays;
import java.util.List;

/**
 * Represents the game board and manages piece placement and movement.
 */
public class Board implements IBoard {
    /** 2D array representing the board grid with pieces. */
    private final IPiece[][] boardGrid;
    private int[][]isTarget;
    /** Array of players in the game. */
    public final IPlayer[] players;
    /** Board configuration object. */
    public final BoardConfig boardConfig;

    /**
     * Constructs the board with the given configuration and players.
     * Initializes the board grid with the pieces from each player.
     *
     * @param bc Board configuration
     * @param players Array of players
     */
    public Board(BoardConfig bc, IPlayer[] players) {
        boardConfig = bc;
        this.boardGrid = new IPiece[bc.gridDimension.getX()][bc.gridDimension.getY()];
        this.isTarget = new int[boardGrid.length][boardGrid[0].length];
        for (int[] row : isTarget) {
            Arrays.fill(row, -1);
        }
        this.players = players;

        initializeFromPlayers();
    }

    private void initializeFromPlayers(){
        for (IPlayer p : players) {
            for (IPiece piece : p.getPieces()) {
                boardGrid[piece.getPos().getRow()][piece.getPos().getCol()] = piece;
            }
        }
    }

    /**
     * Places a piece on the board at its logical position.
     * @param piece The piece to place
     */
    @Override
    public void placePiece(IPiece piece) {
        int row = piece.getRow();
        int col = piece.getCol();
        if (isInBounds(row, col)) {
            boardGrid[row][col] = piece;
        } else {
            throw new IllegalArgumentException("Invalid position row=" + row + ", col=" + col);
        }
    }

    /**
     * Checks if there is a piece at the specified row and column.
     */
    private boolean hasPiece(int row, int col) {
        return isInBounds(row, col) && boardGrid[row][col] != null;
    }

    @Override
    public boolean hasPiece(Position pos){
        return hasPiece(pos.getRow(), pos.getCol());
    }

    @Override
    public boolean hasPieceOrIsTarget(Position pos){
        return hasPiece(pos) || isTarget[pos.getRow()][pos.getCol()] != -1;
    }

    /**
     * Gets the piece at the specified row and column.
     */
    @Override
    public IPiece getPiece(int row, int col) {
        if (!isInBounds(row, col))
            return null;
        return boardGrid[row][col];
    }

    /**
     * Gets the piece at the specified position.
     */
    @Override
    public IPiece getPiece(Position pos) {
        return getPiece(pos.getRow(), pos.getCol());
    }

    /**
     * Returns the player index for a given row.
     */
    @Override
    public int getPlayerOf(int row) {
        return BoardConfig.getPlayerOf(row);
    }

    /**
     * Returns the player index for a given position.
     */
    @Override
    public int getPlayerOf(Position pos){
        return getPlayerOf(pos.getRow());
    }

    /**
     * Moves a piece from one position to another.
     */
    @Override
    public void move(Position from, Position to) {
        IPiece piece = boardGrid[from.getRow()][from.getCol()];
        isTarget[to.getRow()][to.getCol()] = getPiece(from).getPlayer();
        boardGrid[from.getRow()][from.getCol()] = null;

        if (piece != null) {
            piece.move(to);
        }
    }

    /**
     * Updates all pieces and handles captures and board state.
     * This method resets previous positions, updates piece states,
     * and handles captures before and after movement.
     */
    public void updateAll() {
        for (IPlayer player : players) {
            for (int i=0; i<player.getPieces().size(); i++) {
                IPiece piece = player.getPieces().get(i);
                if (piece.isCaptured()) continue;

                if (piece.getCurrentState().isActionFinished()) {
                    Position targetPos = piece.getCurrentState().getPhysics().getTargetPos();
                    IPiece target = boardGrid[targetPos.getRow()][targetPos.getCol()];

                    if (target != null && target != piece && !target.isCaptured()) {
                        if (target.canCapturable())
                            players[target.getPlayer()].markPieceCaptured(target);
                        else
                            players[piece.getPlayer()].markPieceCaptured(piece);
                        EventPublisher.getInstance().publish(EGameEvent.PIECE_CAPTURED, new GameEvent(EGameEvent.PIECE_CAPTURED, null));
                    }

                    boardGrid[targetPos.getRow()][targetPos.getCol()] = piece;
                    isTarget[targetPos.getRow()][targetPos.getCol()] = -1;

                    if(piece.getType() == EPieceType.P && (targetPos.getRow() == 0 || targetPos.getRow() == boardConfig.gridDimension.getX()-1)) {
                        boardGrid[targetPos.getRow()][targetPos.getCol()] = player.replacePToQ(piece, targetPos.copy(), boardConfig);
                    }
                }

                piece.update();
            }
        }
    }

    /**
     * Checks if the specified row and column are within board bounds.
     */
    @Override
    public boolean isInBounds(int r, int c) {
        return boardConfig.isInBounds(r,c);
    }

    /**
     * Checks if the specified position is within board bounds.
     */
    public boolean isInBounds(Position p){
        return isInBounds(p.getRow(), p.getCol());
    }

    /**
     * Checks if a move from one position to another is legal.
     */
    @Override
    public boolean isMoveLegal(Position from, Position to) {
        IPiece fromPiece = getPiece(from);
        if (fromPiece == null)
            return false;

        // Check resting states first
        if (!fromPiece.canAction())
            return false;

        // Check if the move is in the legal move list
        List<Move> moves = fromPiece.getMoves();

        int dx = to.getRow() - from.getRow();
        int dy = to.getCol() - from.getCol();

        Data data = new Data(this, fromPiece, to);
        boolean isLegal = moves.stream().anyMatch(m -> m.getDx() == dx && m.getDy() == dy && (m.getCondition() == null || Arrays.stream(m.getCondition()).allMatch(c->c.isCanMove(data))));

        if (!isLegal)
            return false;

        // Check path clearance (except knights)
        if (!fromPiece.getType().isCanSkip() && !isPathClear(from, to)) {
            isPathClear(from, to);
            return false;
        }

        // Check if capturing own piece
        IPiece toPiece = getPiece(to);
        return (toPiece == null || fromPiece.getPlayer() != toPiece.getPlayer()) && fromPiece.getPlayer() != isTarget[to.getRow()][to.getCol()];
    }

    /**
     * Checks if the path between two positions is clear for movement.
     */
    @Override
    public boolean isPathClear(Position from, Position to) {
        int dRow = Integer.signum(to.dy(from));
        int dCol = Integer.signum(to.dx(from));

        Position current = from.add(dRow, dCol);

        while (!current.equals(to)) {
            if (hasPiece(current))
                return false;
            current = current.add(dRow, dCol);
        }

        return true;
    }

    /**
     * Checks if a jump action is legal for the given piece.
     */
    @Override
    public boolean isJumpLegal(IPiece p) {
        return p.canAction();
    }

    /**
     * Performs a jump action for the given piece.
     */
    @Override
    public void jump(IPiece p) {
        if (p == null) return;
        p.jump();
    }

    /**
     * Returns the array of players.
     */
    @Override
    public IPlayer[] getPlayers() {
        return players;
    }

    /**
     * Returns the number of columns on the board.
     */
    @Override
    public int getCOLS() {
        return boardConfig.gridDimension.getY();
    }

    /**
     * Returns the number of rows on the board.
     */
    @Override
    public int getROWS() {
        return boardConfig.gridDimension.getX();
    }

    /**
     * Returns the board configuration.
     */
    @Override
    public BoardConfig getBoardConfig() {
        return boardConfig;
    }

    @Override
    public List<Position> getLegalMoves(Position selectedPosition){
        if (!isInBounds(selectedPosition)) {
            return List.of();
        }

        IPiece piece = getPiece(selectedPosition);
        if (piece == null || piece.isCaptured()) {
            return List.of();
        }

        return piece.getMoves().stream()
                .filter(move -> BoardRulesEngine.isMoveLegal(this, selectedPosition, selectedPosition.add(move.getDx(), move.getDy())))
                .map(move -> selectedPosition.add(move.getDx(), move.getDy()))
                .toList();
    }

}