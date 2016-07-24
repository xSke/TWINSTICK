package pw.ske.stick.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import pw.ske.stick.Assets;
import pw.ske.stick.GameScreen;

public class MaxHPPickup extends Entity {
    private float amount;
    private float timer;

    public MaxHPPickup(float amount) {
        this.amount = amount;

        Fixture fixture = rectCollision(0.5f, 0.5f);
        Filter filter = new Filter();
        filter.categoryBits = 0x10;
        filter.maskBits = 0x1;
        fixture.setFilterData(filter);
        fixture.setFriction(99999);

        Fixture sensor = rectCollision(0.4f, 0.4f);
        Filter filterSensor = new Filter();
        filterSensor.categoryBits = 0x10;
        filterSensor.maskBits = 0x2;
        sensor.setFilterData(filterSensor);
        sensor.setSensor(true);

        z = -2;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        timer += delta * 4;
    }

    @Override
    public void collide(Entity entity) {
        super.collide(entity);
        if (entity instanceof Player) {
            ((Player) entity).maxHealth += amount;
            GameScreen.i.setPlayerMaxHealth(GameScreen.i.getPlayer().maxHealth);
            Assets.uppy.play();
            destroy();
        } else if (entity instanceof BasicTile) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    getBody().setType(BodyDef.BodyType.StaticBody);
                }
            });
        }
    }

    @Override
    public void renderTransformed(ShapeRenderer sr) {
        super.renderTransformed(sr);
        sr.circle(0, 0, 0.5f + (float) Math.sin(timer) * 0.1f, 16);
    }
}
