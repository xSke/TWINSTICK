package pw.ske.stick.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import pw.ske.stick.Assets;
import pw.ske.stick.Palette;
import pw.ske.stick.util.SpringingContext2D;

public class Hat extends Entity {
    protected SpringingContext2D offset;
    private Entity anchor;
    private Entity baseAnchor;

    public Hat(Entity anchor, Vector2 offset) {
        //offset.x = MathUtils.random(-0.1f, 0.1f);
        this.anchor = anchor;

        color = Palette.FOREGROUND;

        baseAnchor = anchor;
        this.offset = new SpringingContext2D(8, 1);
        this.offset.getTarget().set(offset);

        while (baseAnchor instanceof Hat) {
            baseAnchor = ((Hat) baseAnchor).anchor;
        }

        Fixture fixture = rectCollision(0.5f, 0.5f);
        Filter filter = new Filter();
        filter.categoryBits = 0x8;
        filter.maskBits = 0x1;
        fixture.setFilterData(filter);

        Fixture sensor = rectCollision(0.2f, 0.2f);
        Filter filterSensor = new Filter();
        filter.categoryBits = 0x8;
        filter.maskBits = 0x2;
        sensor.setSensor(true);
        sensor.setFilterData(filterSensor);
        z = 2;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        offset.update(delta);

        if (anchor != null) {
            getBody().setLinearVelocity(0, 0);
            if (anchor.dying) {
                destroy();
                //anchor = null;
                //baseAnchor = null;
            } else {
                final Vector2 pos = anchor.getBody().getPosition().cpy();
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        getBody().setTransform(pos.add(offset.getPosition()), 0);
                    }
                });
            }
        }
    }

    @Override
    public void collide(Entity entity) {
        super.collide(entity);
        if (anchor == null && entity instanceof Player) {
            Assets.ding.play();
            Array<Hat> playerHats = ((Player) entity).getHats();

            baseAnchor = entity;

            if (playerHats.size == 0) {
                anchor = entity;
            } else {
                anchor = playerHats.get(playerHats.size - 1);
            }

            playerHats.add(this);
            z = playerHats.size;
        }
    }
}
