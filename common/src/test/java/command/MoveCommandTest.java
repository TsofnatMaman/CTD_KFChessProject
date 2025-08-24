package command;

import events.EGameEvent;
import events.EventPublisher;
import events.IEventListener;
import interfaces.IBoard;
import interfaces.IBoardEngine;
import interfaces.IPiece;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pieces.Position;

import static org.mockito.Mockito.*;

class MoveCommandTest {

    private IBoard board;
    private IBoardEngine engine;
    private IPiece piece;
    private EventPublisher publisher;
    private Position from;
    private Position to;
    private IEventListener movedListener;
    private IEventListener illegalListener;

    @BeforeEach
    void setUp() {
        board = mock(IBoard.class);
        engine = mock(IBoardEngine.class);
        piece = mock(IPiece.class);
        publisher = EventPublisher.getInstance();

        from = new Position(0, 0);
        to = new Position(1, 1);

        when(board.getBoardRulesEngine()).thenReturn(engine);
        when(board.getPiece(from)).thenReturn(piece);
        when(piece.getPlayer()).thenReturn(0);

        movedListener = mock(IEventListener.class);
        illegalListener = mock(IEventListener.class);

        publisher.subscribe(EGameEvent.PIECE_START_MOVED, movedListener);
        publisher.subscribe(EGameEvent.ILLEGAL_CMD, illegalListener);
    }

    @AfterEach
    void tearDown() {
        publisher.unsubscribe(EGameEvent.PIECE_START_MOVED, movedListener);
        publisher.unsubscribe(EGameEvent.ILLEGAL_CMD, illegalListener);
    }

    @Test
    void executePublishesPieceStartMovedWhenLegal() {
        when(engine.isMoveLegal(board, from, to)).thenReturn(true);

        MoveCommand command = new MoveCommand(from, to, board);
        command.execute();

        verify(board, times(1)).move(from, to);
        verify(movedListener, times(1)).onEvent(any());
        verify(illegalListener, never()).onEvent(any());
    }

    @Test
    void executePublishesIllegalCmdWhenIllegal() {
        when(engine.isMoveLegal(board, from, to)).thenReturn(false);

        MoveCommand command = new MoveCommand(from, to, board);
        command.execute();

        verify(board, never()).move(any(Position.class), any(Position.class));
        verify(illegalListener, times(1)).onEvent(any());
        verify(movedListener, never()).onEvent(any());
    }
}