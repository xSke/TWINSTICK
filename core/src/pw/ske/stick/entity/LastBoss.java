package pw.ske.stick.entity;

import com.badlogic.gdx.physics.box2d.BodyDef;
import pw.ske.stick.GameScreen;

public class LastBoss extends Boss {
    private BossState state = BossState.WAITING;
    private float timer;

    public LastBoss() {
        super("KING BOSSANOID");
        getBody().setType(BodyDef.BodyType.KinematicBody);
        maxHealth = health = 1;

        rectCollision(1, 1);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (state == BossState.WAITING) {
            if (GameScreen.i.getBossState() == GameScreen.BossState.FIGHTING) {
                state = BossState.COMING;
                timer = 0;
            }
        } else if (state == BossState.COMING) {
            timer += delta;

            if (timer > 0.5f) {
                size.getTarget().set(1, 1);
            }
        }
    }

    public enum BossState {
        WAITING,
        COMING
    }
}
