package server.dto;

public class PieceDTO {
    private final String id;
    private final int row;
    private final int col;
    private final String type;
    private final int player;
    private final String state;
    private final int frame;

    public PieceDTO(String id, int row, int col, String type, int player, String state, int frame) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.type = type;
        this.player = player;
        this.state = state;
        this.frame = frame;
    }

    public String getId() { return id; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public String getType() { return type; }
    public int getPlayer() { return player; }
    public String getState() { return state; }
    public int getFrame() { return frame; }
}
