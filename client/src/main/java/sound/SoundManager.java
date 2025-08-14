package sound;

import utils.LogUtils;

import javax.sound.sampled.*;
import java.net.URL;

public class SoundManager {

    public static void playSound(String fileName) {
        try {
            URL soundURL = SoundManager.class.getClassLoader().getResource("sounds/" + fileName);
            if (soundURL == null) {
                LogUtils.logDebug("Sound file not found: " + fileName);
                return;
            }

            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL)) {
                Clip clip = AudioSystem.getClip();

                // סגירה אוטומטית אחרי סיום הניגון
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });

                clip.open(audioInputStream);
                clip.start(); // מתחיל לנגן
            }

        } catch (Exception e) {
            LogUtils.logDebug("Error playing endpoint.sound " + fileName + ": " + e.getMessage());
        }
    }
}
