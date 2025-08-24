package sound;

import interfaces.AppLogger;
import utils.Slf4jAdapter;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

/**
 * Utility class responsible for playing sound effects from resource files.
 * <p>
 * Looks for sound files in the {@code /sounds/} directory inside the classpath
 * and plays them asynchronously using the Java Sound API.
 * </p>
 */
public class SoundManager {

    private static final AppLogger logger = new Slf4jAdapter(SoundManager.class);

    /**
     * Plays a sound file from the {@code /sounds/} resources folder.
     * <p>
     * The method:
     * <ol>
     *     <li>Searches for the file in the {@code sounds} package.</li>
     *     <li>Opens an {@link AudioInputStream} from the resource.</li>
     *     <li>Initializes a {@link Clip} and starts playback.</li>
     *     <li>Closes the clip automatically once playback ends.</li>
     * </ol>
     * </p>
     *
     * @param fileName the name of the sound file (e.g., {@code "PIECE_CAPTURED.wav"})
     */
    public static void playSound(String fileName) {
        try {
            // Locate the sound resource in the classpath
            URL soundURL = SoundManager.class
                    .getClassLoader()
                    .getResource("sounds/" + fileName);

            if (soundURL == null) {
                logger.warn("Sound file not found: " + fileName);
                return;
            }

            // Open the audio input stream from the resource
            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL)) {
                Clip clip = AudioSystem.getClip();

                // Automatically close the clip after playback ends
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });

                // Load audio data into the clip and start playback
                clip.open(audioInputStream);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException e) {
                throw e;
            }

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            logger.error("Error playing sound '" + fileName + "': ", e);
        }
    }
}
