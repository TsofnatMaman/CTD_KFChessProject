package local.view;

import constants.KeyConstants;
import viewUtils.board.CursorController;
import interfaces.IBoard;
import interfaces.IPlayerCursor;
import local.controller.Controller;
import pieces.Position;
import viewUtils.board.BaseBoardPanel;
import viewUtils.board.KeyManager;
import viewUtils.board.SelectionRenderer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;

public class BoardPanel extends BaseBoardPanel {

    private final IPlayerCursor cursor1;
    private final IPlayerCursor cursor2;

    private final KeyManager keyManager;

    private Position selected1 = null;
    private List<Position> legalMoves1 = Collections.emptyList();

    private Position selected2 = null;
    private List<Position> legalMoves2 = Collections.emptyList();

    private final Color SELECT_COLOR_P1 = new Color(255, 0, 0, 128);
    private final Color SELECT_COLOR_P2 = new Color(0, 0, 255, 128);

    private Controller controller;

    public BoardPanel(IBoard board, IPlayerCursor cursor1, IPlayerCursor cursor2) {
        super(board);
        this.cursor1 = cursor1;
        this.cursor2 = cursor2;

        keyManager = new KeyManager();

        initKeyBindings();
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String key = KeyConstants.fromKeyCode(e.getKeyCode());
                if (key != null) {
                    keyManager.handleKey(key);
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setSelected1(Position pos) { selected1 = pos; }
    public void setLegalMoves1(List<Position> moves) { legalMoves1 = moves; }
    public void setSelected2(Position pos) { selected2 = pos; }
    public void setLegalMoves2(List<Position> moves) { legalMoves2 = moves; }

    @Override
    protected void initKeyBindings() {
        // Controller for player 1 (arrows + Enter)
        CursorController.KeyBindings player1Keys = new CursorController.KeyBindings(KeyConstants.UP, KeyConstants.DOWN, KeyConstants.LEFT, KeyConstants.RIGHT, KeyConstants.ENTER);

        CursorController cursorController1 = new CursorController(cursor1, this, player1Keys, keyManager);
        cursorController1.setOnPlayerAction(pos -> {
            if (controller != null)
                controller.handlePlayerMove(0, pos);
        });

        // Controller for player 2 (WASD + Space)
        CursorController.KeyBindings player2Keys = new CursorController.KeyBindings(KeyConstants.W, KeyConstants.S, KeyConstants.A, KeyConstants.D, KeyConstants.SPACE);
        CursorController cursorController2 = new CursorController(cursor2, this, player2Keys, keyManager);
        cursorController2.setOnPlayerAction(pos -> {
            if (controller != null)
                controller.handlePlayerMove(1, pos);
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        cursor1.draw(g, getWidth(), getHeight());
        cursor2.draw(g, getWidth(), getHeight());

        Graphics2D g2 = (Graphics2D) g;
        SelectionRenderer.draw(g2, selected1, legalMoves1, SELECT_COLOR_P1, board.getCols(), board.getRows(), getWidth(), getHeight());
        SelectionRenderer.draw(g2, selected2, legalMoves2, SELECT_COLOR_P2, board.getCols(), board.getRows(), getWidth(), getHeight());
    }

}
