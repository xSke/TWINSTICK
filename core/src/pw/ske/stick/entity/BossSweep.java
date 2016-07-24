package pw.ske.stick.entity;

import com.badlogic.gdx.physics.box2d.BodyDef;
import pw.ske.stick.Palette;
import pw.ske.stick.util.SpringingContext1D;

public class BossSweep extends Entity {
    private final float y;
    private float timer;
    private SpringingContext1D pos = new SpringingContext1D(4, 1);
    private boolean hurt;

    public BossSweep(float y) {
        this.y = y;
        size.getPosition().set(size.getTarget().set(50, 15));
        rectCollision(50, 15).setSensor(true);
        damageOnTouch = 2;

        getBody().setType(BodyDef.BodyType.KinematicBody);

        color = Palette.HIGHLIGHT2;
        pos.target = -24;
        pos.value = -24;

        z = -1;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        timer += delta;

        pos.target = -23 + timer;

        if (timer > 3) {
            pos.target = -3;
        }

        if (timer > 3.5f) {
            pos.target = -25;
        }

        if (timer > 5) {
            destroy();
        }

        pos.update(delta);
        getBody().setTransform(pos.value, y, 0);

        /*if (getBody().getPosition().x < xLimit) {
            getBody().setLinearVelocity(120, 0);
        } else {
            getBody().setLinearVelocity(0, 0);
            timer += delta;

            if (timer > 2) {
                destroy();
            }
        }*/
    }
}
