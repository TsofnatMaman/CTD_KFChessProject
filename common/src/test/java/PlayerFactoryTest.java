import board.BoardConfig;
import board.Dimension;
import interfaces.IPlayer;
import org.junit.jupiter.api.Test;
import player.PlayerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerFactoryTest {

    @Test
    void testCreatePlayers_returnsTwoPlayersWithCorrectIds() {
        BoardConfig config = new BoardConfig(new Dimension(8), new Dimension(1,1), new Dimension(640));
        String[] names = {"Alice", "Bob"};

        IPlayer[] players = PlayerFactory.createPlayers(names, config);

        assertEquals(2, players.length);
        assertEquals(0, players[0].getId());
        assertEquals(1, players[1].getId());
        assertEquals("Alice", players[0].getName());
        assertEquals("Bob", players[1].getName());
    }
}