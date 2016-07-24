package pw.ske.stick.entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import pw.ske.stick.Palette;

public class BasicTile extends Entity {
    public BasicTile(Rectangle rect) {
        size.getPosition().set(size.getTarget().set(rect.width, rect.height));
        color = Palette.FOREGROUND;
        getBody().setType(BodyDef.BodyType.StaticBody);
        getBody().setTransform(rect.x + rect.width / 2f, rect.y + rect.height / 2f, 0);
        rectEdgeCollision(rect.width, rect.height);
    }

    private void rectEdgeCollision(float w, float h) {
        EdgeShape shape = new EdgeShape();

        shape.set(-w / 2, -h / 2, w / 2, -h / 2);
        getBody().createFixture(shape, 0).setFriction(0);

        shape.set(-w / 2, h / 2, w / 2, h / 2);
        getBody().createFixture(shape, 0).setFriction(0);

        shape.set(-w / 2, -h / 2, -w / 2, h / 2);
        getBody().createFixture(shape, 0).setFriction(0);

        shape.set(w / 2, -h / 2, w / 2, h / 2);
        getBody().createFixture(shape, 0).setFriction(0);

        shape.dispose();
    }
}
