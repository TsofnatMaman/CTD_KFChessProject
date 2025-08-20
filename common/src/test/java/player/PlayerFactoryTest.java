package player;

import board.BoardConfig;
import org.junit.jupiter.api.Test;
import interfaces.IPlayer;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test PlayerFactory.createPlayers returns 2 players with expected ids and names.
 */
class PlayerFactoryTest {

    @Test
    void createPlayers_returnsPlayersWithIdsAndNames() {
        BoardConfig bc = new BoardConfig(new Dimension(8,8), new Dimension(400,400), new Dimension(500,500));
        String[] names = new String[] { "Alice", "Bob" };
        IPlayer[] players = PlayerFactory.createPlayers(names, bc);

        assertNotNull(players);
        assertEquals(2, players.length);
        assertEquals("Alice", players[0].getName());
        assertEquals("Bob", players[1].getName());
        assertEquals(0, players[0].getId());
        assertEquals(1, players[1].getId());
    }
}
