package pw.ske.stick.entity;

import com.badlogic.gdx.math.Vector2;
import pw.ske.stick.Assets;
import pw.ske.stick.GameScreen;

public class LivingEntity extends Entity {
    protected float health;
    protected float maxHealth;
    protected float invincibilityTime;
    protected float damageShakeAmount = 0.01f;
    protected float damagePauseAmount = 0.05f;
    private float cooldown;

    public LivingEntity() {
    }

    public void damage(float amount, Vector2 source) {
        if (cooldown > 0) return;

        health -= amount;
        size.getVelocity().set(amount, -amount).scl(10);

        Vector2 delta = getBody().getPosition().sub(source);
        delta.nor().scl(10);
        getBody().setLinearVelocity(getBody().getLinearVelocity().add(delta));

        cooldown = invincibilityTime;
        Assets.hurt.play();

        GameScreen.i.shake(damageShakeAmount);
        GameScreen.i.pause(damagePauseAmount);
    }

    @Override
    public void render() {
        boolean render = true;

        if (cooldown > 0) {
            if (cooldown % 0.15 < 0.075) render = false;
        }

        if (render) super.render();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        cooldown -= delta;
        if (health <= 0 && !dying) {
            kill();
        }
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }
}
