package controller;

import endpoint.view.BoardPanel;
import interfaces.IPlayer;

public interface IGameUI{
    void onGameUpdate();
    void onWin(IPlayer winner);
    void updateTimerLabel(String text);

    BoardPanel getBoardPanel();
}
