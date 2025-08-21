package endpoint.controller;

import dto.*;
import endpoint.view.BoardPanel;
import game.GameFactory;
import interfaces.IGame;
import interfaces.IPlayer;
import interfaces.IPlayerCursor;
import player.PlayerCursor;
import pieces.Position;
import viewUtils.game.GamePanel;
import viewUtils.game.PlayerInfoPanel;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class for creating Game and GamePanel instances without relying on static state.
 * <p>
 * Provides methods to initialize the game model and its associated UI panel for a specific player.
 * </p>
 */
public class GameHelper {

    /** The ID of the current player */
    private final int playerId;

    /**
     * Constructs a GameHelper for the given player ID.
     *
     * @param playerId the ID of the current player
     */
    public GameHelper(int playerId) {
        this.playerId = playerId;
    }

    /**
     * Creates the Game model from a GameDTO.
     *
     * @param dto the GameDTO containing initial game state
     * @return a new IGame instance
     */
    public IGame createGame(GameDTO dto) {
        IPlayer[] players = Arrays.stream(dto.getPlayers())
                .map(p -> PlayerDTO.to(p, dto.getBoardConfig()))
                .toArray(IPlayer[]::new);

        return GameFactory.createNewGame(dto.getBoardConfig(), players);
    }

    /**
     * Creates the GamePanel (UI) from the Game model and a PlayerActionHandler.
     *
     * @param model         the game model
     * @param actionHandler the handler for player actions
     * @return a new IGameUI instance
     */
    public IGameUI createGamePanel(IGame model, PlayerActionHandler actionHandler) {
        // Create a cursor for the current player
        IPlayerCursor cursor = new PlayerCursor(
                new Position(0, 0),
                model.getPlayerById(playerId).getColor()
        );

        // Initialize the board panel
        BoardPanel boardPanel = new BoardPanel(model.getBoard(), cursor);
        boardPanel.setOnPlayerAction(actionHandler::handlePlayerSelection);

        // Create PlayerInfoPanels for all players
        List<PlayerInfoPanel> pips = Arrays.stream(model.getPlayers())
                .map(this::createPlayerInfoPanel)
                .collect(Collectors.toList());

        return new GamePanel(boardPanel, pips);
    }

    /**
     * Creates a PlayerInfoPanel for a given player with a semi-transparent background.
     *
     * @param player the player for whom to create the panel
     * @return a PlayerInfoPanel instance
     */
    private PlayerInfoPanel createPlayerInfoPanel(IPlayer player) {
        PlayerInfoPanel panel = new PlayerInfoPanel(player);
        panel.setBackground(new Color(255, 255, 255, 180)); // semi-transparent white
        return panel;
    }
}
