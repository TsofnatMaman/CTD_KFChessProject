package endpoint.controller;

import dto.*;
import endpoint.view.BoardPanel;
import game.Game;
import interfaces.IGame;
import interfaces.IPlayer;
import interfaces.IPlayerCursor;
import player.PlayerCursor;
import pieces.Position;
import viewUtils.GamePanel;
import viewUtils.PlayerInfoPanel;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class for creating Game and GamePanel instances without static state.
 */
public class GameHelper {

    private final int playerId;

    public GameHelper(int playerId) {
        this.playerId = playerId;
    }

    /**
     * Creates the Game model from the GameDTO.
     */
    public IGame createGame(GameDTO dto) {
        IPlayer[] players = Arrays.stream(dto.getPlayers())
                .map(p -> PlayerDTO.to(p, dto.getBoardConfig()))
                .toArray(IPlayer[]::new);

        return new Game(dto.getBoardConfig(), players);
    }

    /**
     * Creates the GamePanel (UI) from the Game model and the PlayerActionHandler.
     */
    public IGameUI createGamePanel(IGame model, PlayerActionHandler actionHandler) {
        IPlayerCursor cursor = new PlayerCursor(new Position(0,0), model.getPlayerById(playerId).getColor());

        BoardPanel boardPanel = new BoardPanel(model.getBoard(), cursor);
        boardPanel.setOnPlayerAction(actionHandler::handlePlayerSelection);

        List<PlayerInfoPanel> pips = Arrays.stream(model.getPlayers())
                .map(this::createPlayerInfoPanel)
                .collect(Collectors.toList());

        return new GamePanel(boardPanel, pips);
    }

    private PlayerInfoPanel createPlayerInfoPanel(IPlayer player) {
        PlayerInfoPanel panel = new PlayerInfoPanel(player);
        panel.setBackground(new Color(255, 255, 255, 180));
        return panel;
    }
}
