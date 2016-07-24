package pw.ske.stick.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;

public class Bullet extends Entity {
    private float timer;

    public Bullet() {
        Fixture fixture = rectCollision(0.3f, 0.3f);
        Filter filter = new Filter();
        filter.categoryBits = 0x100;
        filter.maskBits = 0x7;
        fixture.setFilterData(filter);
        size.getTarget().set(0.3f, 0.3f);
        z = -1;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        timer += delta;
        if (timer > 2) {
            destroy();
        }
    }

    @Override
    public void collide(final Entity entity) {
        super.collide(entity);

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (entity instanceof Player) {
                    ((Player) entity).damage(1f, getBody().getPosition());
                    destroy();
                } else if (entity instanceof BasicTile) {
                    destroy();
                }
            }
        });
    }

    @Override
    public void renderTransformed(ShapeRenderer sr) {
        sr.circle(0, 0, 0.3f, 16);
    }
}
