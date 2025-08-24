package command;

import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import interfaces.IBoard;
import interfaces.IPiece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link JumpCommand} ensuring the proper events are published
 * depending on whether the jump succeeds or fails.
 */
class JumpCommandTest {

    private IBoard board;
    private IPiece piece;
    private EventPublisher publisher;

    @BeforeEach
    void setUp() {
        board = mock(IBoard.class);
        piece = mock(IPiece.class);
        when(piece.getPlayer()).thenReturn(1);
        publisher = EventPublisher.getInstance();
    }

    @Test
    void execute_whenJumpSucceeds_publishesPieceJump() {
        IEventListener jumpListener = mock(IEventListener.class);
        IEventListener illegalListener = mock(IEventListener.class);
        publisher.subscribe(EGameEvent.PIECE_JUMP, jumpListener);
        publisher.subscribe(EGameEvent.ILLEGAL_CMD, illegalListener);

        JumpCommand cmd = new JumpCommand(piece, board);
        cmd.execute();

        verify(board, times(1)).jump(piece);

        ArgumentCaptor<GameEvent> captor = ArgumentCaptor.forClass(GameEvent.class);
        verify(jumpListener, times(1)).onEvent(captor.capture());
        assertEquals(EGameEvent.PIECE_JUMP, captor.getValue().type());

        verify(illegalListener, never()).onEvent(any());

        publisher.unsubscribe(EGameEvent.PIECE_JUMP, jumpListener);
        publisher.unsubscribe(EGameEvent.ILLEGAL_CMD, illegalListener);
    }

    @Test
    void execute_whenJumpIllegal_publishesIllegalCmd() {
        IEventListener jumpListener = mock(IEventListener.class);
        IEventListener illegalListener = mock(IEventListener.class);
        publisher.subscribe(EGameEvent.PIECE_JUMP, jumpListener);
        publisher.subscribe(EGameEvent.ILLEGAL_CMD, illegalListener);

        doThrow(new IllegalCmdException("illegal"))
                .when(board).jump(piece);

        JumpCommand cmd = new JumpCommand(piece, board);
        cmd.execute();

        verify(board, times(1)).jump(piece);
        verify(jumpListener, never()).onEvent(any());

        ArgumentCaptor<GameEvent> captor = ArgumentCaptor.forClass(GameEvent.class);
        verify(illegalListener, times(1)).onEvent(captor.capture());
        assertEquals(EGameEvent.ILLEGAL_CMD, captor.getValue().type());

        publisher.unsubscribe(EGameEvent.PIECE_JUMP, jumpListener);
        publisher.unsubscribe(EGameEvent.ILLEGAL_CMD, illegalListener);
    }
}

