package webSocket.server.dto;

public class PieceDTO {
    private String id;
    private int row;
    private int col;
    private String type;  // למשל "K" ל-king, "Q" ל-queen וכו'
    private int playerId;
    private String stateName; // סטטוס כמו "active", "captured" וכו'
    private int frameIndex;   // אינדקס פריים לתצוגה גרפית

    // ריק למיפוי JSON
    public PieceDTO() {}

    public PieceDTO(int row, int col, String type, int playerId, String stateName, int frameIndex) {
        this.id = row+","+col;
        this.row = row;
        this.col = col;
        this.type = type;
        this.playerId = playerId;
        this.stateName = stateName;
        this.frameIndex = frameIndex;
    }

    public String getId() { return id; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public String getType() { return type; }
    public int getPlayerId() { return playerId; }
    public String getStateName() { return stateName; }
    public int getFrameIndex() { return frameIndex; }

    public void setId(String id) { this.id = id; }
    public void setRow(int row) { this.row = row; }
    public void setCol(int col) { this.col = col; }
    public void setType(String type) { this.type = type; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public void setStateName(String stateName) { this.stateName = stateName; }
    public void setFrameIndex(int frameIndex) { this.frameIndex = frameIndex; }
}
