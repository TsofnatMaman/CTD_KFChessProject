package sound;

import utils.LogUtils;

import javax.sound.sampled.*;
import java.net.URL;

/**
 * Utility class to play sound effects from resource files.
 */
public class SoundManager {

    /**
     * Plays a sound from the "sounds" resources folder.
     * If the file is not found, logs a debug message.
     *
     * @param fileName The name of the sound file (e.g., "PIECE_CAPTURED.wav")
     */
    public static void playSound(String fileName) {
        try {
            // Locate the sound resource in the classpath
            URL soundURL = SoundManager.class.getClassLoader().getResource("sounds/" + fileName);
            if (soundURL == null) {
                LogUtils.logDebug("Sound file not found: " + fileName);
                return;
            }

            // Open the audio input stream from the resource
            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL)) {
                Clip clip = AudioSystem.getClip();

                // Close the clip automatically after playback ends
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });

                // Open and start playing the sound
                clip.open(audioInputStream);
                clip.start();

            }

        } catch (Exception e) {
            LogUtils.logDebug("Error playing sound '" + fileName + "': " + e.getMessage());
        }
    }
}
