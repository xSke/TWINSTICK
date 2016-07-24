package pw.ske.stick.entity;

import com.badlogic.gdx.math.Rectangle;
import pw.ske.stick.GameScreen;

public class LoadMapTrigger extends Trigger {
    private final String map;
    private float timer;

    public LoadMapTrigger(Rectangle rect, String map) {
        super(rect, 0x02);
        this.map = map;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        timer += delta;
    }

    @Override
    public void collide(Entity entity) {
        super.collide(entity);
        if (entity instanceof Player && timer > 1) {
            GameScreen.i.resetTimer(map);
        }
    }
}
