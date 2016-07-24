package pw.ske.stick.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import pw.ske.stick.Assets;
import pw.ske.stick.GameScreen;

public class Stick extends Entity {
    private final Fixture fixture;
    private final Fixture sensor;
    private int stick;
    private Player player;
    private float flightTimer;
    private float homingTimer;
    private StickState state = StickState.HELD;

    public Stick(int stick, Color color, Player player) {
        this.stick = stick;
        this.player = player;
        damageOnTouch = 1;
        this.color = color;
        size.getTarget().set(size.getPosition().set(1, 0.15f));
        fixture = rectCollision(1, 0.2f);
        sensor = rectCollision(0.9f, 0.15f);
        sensor.setSensor(true);
        Filter filterSensor = new Filter();
        filterSensor.categoryBits = 0x04;
        filterSensor.maskBits = 0xc3;
        sensor.setFilterData(filterSensor);

        Filter filter = new Filter();
        filter.categoryBits = 0x04;
        filter.maskBits = 0x01;
        fixture.setFilterData(filter);

        z = 1;
    }

    public StickState getState() {
        return state;
    }

    public void throwStick(float angle) {
        state = StickState.FLIGHT;

        getBody().setTransform(player.getBody().getPosition(), angle * MathUtils.degreesToRadians);
        getBody().setAngularVelocity(-13);
        getBody().setLinearVelocity(new Vector2(55, 0).rotate(angle));

        flightTimer = 5;
        Assets.trow.play();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (state == StickState.HELD) {
            float angle = player.getMouseAngle();
            if (player.getHeldStick() == this) angle += 10;
            getBody().setTransform(player.getBody().getPosition().add(0.2f, 0.2f), angle * MathUtils.degreesToRadians);
            getBody().setLinearVelocity(0, 0);
        } else if (state == StickState.FLIGHT) {
            flightTimer -= delta;

            if (flightTimer <= 0) {
                returnStick();
            }

            if (GameScreen.i.getCollisionTracker().getCollisions().containsKey(fixture) && flightTimer < 4.9f) {
                returnStick();
            }

            if (getBody().getPosition().y < -5) returnStick();
        } else if (state == StickState.RETURN) {
            homingTimer -= delta;
            if (homingTimer <= 0) {
                float homingSpeed = 50;
                if (GameScreen.i.getBoss() != null && GameScreen.i.getBoss() instanceof RunBoss) homingSpeed = 100;
                getBody().setLinearVelocity(player.getBody().getPosition().sub(getBody().getPosition()).nor().scl(homingSpeed));
            }
            getBody().setAngularVelocity(5);

            if (GameScreen.i.getCollisionTracker().getCollisions().containsKey(sensor)) {
                for (Fixture coll : GameScreen.i.getCollisionTracker().getCollisions().get(sensor)) {
                    if (coll.getBody().getUserData() == player) {
                        state = StickState.HELD;
                    }
                }
            }
        }

        fixture.setSensor(state == StickState.RETURN);

        if (player.dying && !dying) kill();
    }

    private void returnStick() {
        state = StickState.RETURN;
        homingTimer = 0.5f;
        getBody().setLinearVelocity(0, 40);
    }

    @Override
    protected void collideDamage(Entity toDamage) {
        if (toDamage != player && state != StickState.HELD) super.collideDamage(toDamage);
    }

    public enum StickState {
        HELD,
        FLIGHT,
        RETURN
    }
}
