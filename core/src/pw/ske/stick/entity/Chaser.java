package pw.ske.stick.entity;

import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import pw.ske.stick.GameScreen;
import pw.ske.stick.util.SpringingContext1D;

public class Chaser extends LivingEntity {
    private SpringingContext1D xVel = new SpringingContext1D(4, 1);

    public Chaser() {
        damagePauseAmount = 0;
        size.getTarget().set(1f, 1f);
        Fixture fixture = rectCollision(1f, 1f);
        Filter filter = new Filter();
        filter.categoryBits = 0x40;
        filter.maskBits = 0x47;
        fixture.setFilterData(filter);
        damageOnTouch = 1;
        health = maxHealth = 2;
        getBody().setFixedRotation(true);
    }

    @Override
    protected void collideDamage(Entity toDamage) {
        if (!(toDamage instanceof Chaser)) super.collideDamage(toDamage);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        float direction = Math.signum(GameScreen.i.getPlayer().getBody().getPosition().x - getBody().getPosition().x);
        shear.target = 0.2f * direction;

        xVel.target = 10 * direction;
        xVel.update(delta);

        getBody().setLinearVelocity(xVel.value, getBody().getLinearVelocity().y);
    }
}
