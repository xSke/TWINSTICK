package pw.ske.stick.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import pw.ske.stick.Game;
import pw.ske.stick.GameScreen;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "TWIN STICK";
        config.width = 1280;
        config.height = 720;
        config.resizable = false;
        config.samples = 8;
        new LwjglApplication(new Game(), config);
    }
}
