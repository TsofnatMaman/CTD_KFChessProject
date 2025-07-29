package game;

import com.fasterxml.jackson.databind.ObjectMapper;
import interfaces.IBoard;
import interfaces.IGame;
import interfaces.IPiece;
import interfaces.IPlayer;

import java.util.ArrayList;
import java.util.List;

public class GameStateDTO {
    public List<PieceDTO> pieces = new ArrayList<>();
    public List<PlayerDTO> players = new ArrayList<>();

    public static class PieceDTO {
        public String id;
        public String type;
        public int player;
        public int row;
        public int col;
        public String state;
        public int animationFrame;

        public PieceDTO(String id, String type, int player, int row, int col, String state, int animationFrame) {
            this.id = id;
            this.type = type;
            this.player = player;
            this.row = row;
            this.col = col;
            this.state = state;
            this.animationFrame = animationFrame;
        }
    }

    public static class PlayerDTO {
        public int id;
        public String name;
        public int score;
        public List<String> moves; // אפשר לשמור את ההיסטוריה של המהלכים

        public PlayerDTO(int id, String name, int score, List<String> moves) {
            this.id = id;
            this.name = name;
            this.score = score;
            this.moves = moves;
        }
    }

    public static GameStateDTO fromGame(IGame game) {
        GameStateDTO dto = new GameStateDTO();

        IBoard board = game.getBoard();

        // הוספת הכלים
        for (IPlayer player : board.getPlayers()) {
            for (IPiece piece : player.getPieces()) {
                if (piece.isCaptured()) continue;
                dto.pieces.add(new PieceDTO(
                        piece.getId(),
                        piece.getType().getVal(),
                        piece.getPlayer(),
                        piece.getRow(),
                        piece.getCol(),
                        piece.getCurrentStateName().toString(),
                        piece.getCurrentState().getGraphics() != null ? piece.getCurrentState().getGraphics().getCurrentFrameIdx() : 0
                ));
            }
        }

        // הוספת השחקנים
        for (IPlayer player : game.getPlayers()) {
            dto.players.add(new PlayerDTO(
                    player.getId(),
                    player.getName(),
                    player.getScore(),
                    player.getMovesHistory() // נניח שיש שיטה שמחזירה רשימת מהלכים כ-String
            ));
        }

        return dto;
    }

    public String toJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }
}
