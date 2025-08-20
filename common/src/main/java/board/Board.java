package board;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.listeners.ActionData;
import interfaces.*;
import moves.Data;
import moves.Move;
import pieces.EPieceType;
import pieces.Position;

import java.util.Arrays;
import java.util.List;

/**
 * Represents the chess game board and manages piece placement, movement, and game logic.
 * <p>
 * This class provides methods for checking legal moves, updating the board state,
 * handling captures, and interacting with players and pieces.
 */
public class Board implements IBoard {

    /**
     * 2D array representing the board grid with pieces.
     * Each cell contains a reference to an IPiece or null if empty.
     */
    private final IPiece[][] boardGrid;

    /**
     * 2D array indicating which player is targeting each cell. -1 means no target.
     */
    private final int[][] isTarget;

    /**
     * Array of players in the game.
     */
    public final IPlayer[] players;

    /**
     * Board configuration object.
     */
    public final BoardConfig boardConfig;

    /**
     * Constructs the board with the given configuration and players.
     *
     * @param bc      Board configuration
     * @param players Array of players
     */
    public Board(BoardConfig bc, IPlayer[] players) {
        this.boardConfig = bc;
        this.boardGrid = new IPiece[(int) bc.gridDimension().getWidth()][(int) bc.gridDimension().getHeight()];
        this.isTarget = new int[boardGrid.length][boardGrid[0].length];
        for (int[] row : isTarget) {
            Arrays.fill(row, -1);
        }
        this.players = players;
        initializeFromPlayers();
    }

    /**
     * Initializes the board grid with the pieces from each player.
     */
    private void initializeFromPlayers() {
        for (IPlayer p : players) {
            for (IPiece piece : p.getPieces()) {
                boardGrid[piece.getPos().getRow()][piece.getPos().getCol()] = piece;
            }
        }
    }

    /**
     * Checks if there is a piece at the specified row and column.
     *
     * @param row row index
     * @param col column index
     * @return true if a piece exists
     */
    private boolean hasPiece(int row, int col) {
        return isInBounds(row, col) && boardGrid[row][col] != null;
    }

    @Override
    public boolean hasPiece(Position pos) {
        return hasPiece(pos.getRow(), pos.getCol());
    }

    @Override
    public boolean hasPieceOrIsTarget(Position pos) {
        return hasPiece(pos) || isTarget[pos.getRow()][pos.getCol()] != -1;
    }

    /**
     * Gets the piece at the specified row and column.
     *
     * @param row row index
     * @param col column index
     * @return the piece at the position or null
     */
    private IPiece getPiece(int row, int col) {
        return isInBounds(row, col) ? boardGrid[row][col] : null;
    }

    @Override
    public IPiece getPiece(Position pos) {
        return getPiece(pos.getRow(), pos.getCol());
    }

    private int getPlayerOf(int row) {
        return BoardConfig.getPlayerOf(row);
    }

    @Override
    public int getPlayerOf(Position pos) {
        return getPlayerOf(pos.getRow());
    }

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
     * Updates all pieces, handles captures and board state.
     */
    public void updateAll() {
        long now = System.nanoTime();

        for (IPlayer player : players) {
            for (IPiece piece : player.getPieces()) {
                if (piece.isCaptured()) continue;

                if (piece.getCurrentState().isActionFinished(now)) {
                    Position targetPos = piece.getCurrentState().getPhysics().getTargetPos();
                    IPiece target = boardGrid[targetPos.getRow()][targetPos.getCol()];

                    if (target != null && target != piece && !target.isCaptured()) {
                        if (target.canCapturable())
                            players[target.getPlayer()].markPieceCaptured(target);
                        else
                            players[piece.getPlayer()].markPieceCaptured(piece);

                        EventPublisher.getInstance()
                                .publish(EGameEvent.PIECE_CAPTURED,
                                        new GameEvent(EGameEvent.PIECE_CAPTURED,
                                                new ActionData(piece.getPlayer(), null)));
                    }

                    boardGrid[targetPos.getRow()][targetPos.getCol()] = piece;
                    isTarget[targetPos.getRow()][targetPos.getCol()] = -1;

                    if (piece.getType() == EPieceType.P &&
                            (targetPos.getRow() == 0 || targetPos.getRow() == boardConfig.gridDimension().getWidth() - 1)) {
                        boardGrid[targetPos.getRow()][targetPos.getCol()] =
                                player.replacePToQ(piece, targetPos.copy(), boardConfig);
                    }
                }

                piece.update(now);
            }
        }
    }

    private boolean isInBounds(int r, int c) {
        return boardConfig.isInBounds(r, c);
    }

    public boolean isInBounds(Position p) {
        return isInBounds(p.getRow(), p.getCol());
    }

    @Override
    public boolean isMoveLegal(Position from, Position to) {
        IPiece fromPiece = getPiece(from);
        if (fromPiece == null || !fromPiece.canAction()) return false;

        List<Move> moves = fromPiece.getMoves();
        int dx = to.getRow() - from.getRow();
        int dy = to.getCol() - from.getCol();
        Data data = new Data(this, fromPiece, to);

        boolean isLegal = moves.stream()
                .anyMatch(m -> m.getDx() == dx && m.getDy() == dy &&
                        (m.getCondition() == null || Arrays.stream(m.getCondition())
                                .allMatch(c -> c.isCanMove(data))));

        if (!isLegal) return false;

        if (!fromPiece.getType().isCanSkip() && !isPathClear(from, to)) return false;

        IPiece toPiece = getPiece(to);
        return (toPiece == null || fromPiece.getPlayer() != toPiece.getPlayer()) &&
                fromPiece.getPlayer() != isTarget[to.getRow()][to.getCol()];
    }

    private boolean isPathClear(Position from, Position to) {
        int dRow = Integer.signum(to.dy(from));
        int dCol = Integer.signum(to.dx(from));
        Position current = from.add(dRow, dCol);

        while (!current.equals(to)) {
            if (hasPiece(current)) return false;
            current = current.add(dRow, dCol);
        }
        return true;
    }

    @Override
    public boolean isJumpLegal(IPiece p) {
        return p.canAction();
    }

    @Override
    public void jump(IPiece p) {
        if (p != null) p.jump();
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
        if (!isInBounds(selectedPosition)) return List.of();
        IPiece piece = getPiece(selectedPosition);
        if (piece == null || piece.isCaptured()) return List.of();

        return piece.getMoves().stream()
                .filter(move -> BoardRulesEngine.isMoveLegal(this, selectedPosition,
                        selectedPosition.add(move.getDx(), move.getDy())))
                .map(move -> selectedPosition.add(move.getDx(), move.getDy()))
                .toList();
    }
}
