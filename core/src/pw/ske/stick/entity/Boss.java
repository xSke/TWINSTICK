package pw.ske.stick.entity;

import com.badlogic.gdx.Gdx;
import pw.ske.stick.Assets;
import pw.ske.stick.GameScreen;

public class Boss extends LivingEntity {
    private String name;
    private boolean shouldBoom = false;

    public Boss(String name) {
        this.name = name;
        damageShakeAmount = 0.3f;
        damagePauseAmount = 0.05f;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (dying && delta > 0.0001f && shouldBoom) {
            Assets.xplode.play();
            GameScreen.i.shake(1.5f);
            shouldBoom = false;
        }
    }

    @Override
    public void kill() {
        super.kill();
        GameScreen.i.pause(1f);
        GameScreen.i.shake(0.2f);
        Assets.shut.play();
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                shouldBoom = true;
            }
        });
    }

    @Override
    public void destroy() {
        super.destroy();
        GameScreen.i.setBossState(GameScreen.BossState.DEAD);
    }
}
