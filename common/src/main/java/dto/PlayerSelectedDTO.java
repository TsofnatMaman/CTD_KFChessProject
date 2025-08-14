package dto;

import pieces.Position;

public class PlayerSelected {
    /** The ID of the player making the selection. */
    private int playerId;
    /** The position selected by the player. */
    private Position selection;

    /**
     * Default constructor for serialization/deserialization frameworks (e.g., Jackson).
     */
    public PlayerSelected() {}

    /**
     * Constructs a PlayerSelected DTO.
     * @param playerId the ID of the player
     * @param selection the selected position
     */
    public PlayerSelected(int playerId, Position selection){
        this.playerId = playerId;
        this.selection = selection;
    }

    /**
     * Gets the player ID.
     * @return the player ID
     */
    public int getPlayerId() { return playerId; }
    /**
     * Sets the player ID.
     * @param playerId the player ID
     */
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    /**
     * Gets the selected position.
     * @return the selected position
     */
    public Position getSelection() { return selection; }
    /**
     * Sets the selected position.
     * @param selection the selected position
     */
    public void setSelection(Position selection) { this.selection = selection; }
}
