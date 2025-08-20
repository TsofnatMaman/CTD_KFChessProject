import org.junit.jupiter.api.Test;
import sound.SoundManager;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SoundManagerTest {

    @Test
    void playSound_missingResource_doesNotThrow() {
        // sound file that does not exist
        assertDoesNotThrow(() -> SoundManager.playSound("this_file_does_not_exist_hopefully.wav"));
    }
}
