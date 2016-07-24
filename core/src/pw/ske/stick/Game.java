package pw.ske.stick;

public class Game extends com.badlogic.gdx.Game {
    public static Game i;

    public static void start() {
        i.setScreen(new GameScreen());
    }

    @Override
    public void create() {
        i = this;
        setScreen(new MenuScreen());
    }

    public static void theend() {
        i.setScreen(new EndScreen());
    }
}
