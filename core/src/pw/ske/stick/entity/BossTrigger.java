package pw.ske.stick.entity;

import com.badlogic.gdx.math.Rectangle;
import pw.ske.stick.GameScreen;

public class BossTrigger extends Trigger {
    private float timer;

    public BossTrigger(Rectangle rect) {
        super(rect, 0x02);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        timer += delta;
    }

    @Override
    public void collide(Entity entity) {
        super.collide(entity);
        if (entity instanceof Player && GameScreen.i.getBossState() == GameScreen.BossState.PREFIGHT && timer > 1) {
            GameScreen.i.setBossState(GameScreen.BossState.FIGHTING);
        }
    }
}
