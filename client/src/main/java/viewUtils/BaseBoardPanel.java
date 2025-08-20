package viewUtils;

import dto.PieceView;
import events.EGameEvent;
import events.EventPublisher;
import events.GameEvent;
import events.IEventListener;
import game.IBoardView;
import interfaces.IBoard;
import utils.LogUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Pure UI panel: displays board, pieces, cursor, selection and legal moves.
 * Receives all updates from external sources (controller).
 */
public abstract class BaseBoardPanel extends JPanel implements IBoardView, IEventListener {

    protected BufferedImage boardImage;
    protected final IBoard board;

    public BaseBoardPanel(IBoard board) {
        this.board = board;

        setPreferredSize(board.getBoardConfig().panelDimension());

        setFocusable(true);

        loadBoardImage();

        EventPublisher.getInstance().subscribe(EGameEvent.GAME_UPDATE, this);
    }

    public void initKeyBindings() {}

    private void loadBoardImage() {
        try {
            URL imageUrl = getClass().getClassLoader().getResource("board/board.png");
            if (imageUrl != null) {
                boardImage = ImageIO.read(imageUrl);
            } else {
                LogUtils.logDebug("Board image not found in resources!");
            }
        } catch (IOException e) {
            LogUtils.logDebug("Exception loading board image: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (boardImage != null) {
            g.drawImage(boardImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        if(board != null)
            BoardRenderer.draw(g, PieceView.toPieceViews(board), board.getBoardConfig());
    }

    public void update() {
        repaint();
    }

    @Override
    public void onEvent(GameEvent event) {
        repaint();
    }
}
