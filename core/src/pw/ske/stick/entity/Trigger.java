package pw.ske.stick.entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;

public class Trigger extends Entity {
    public Trigger(Rectangle rect, int mask) {
        getBody().setType(BodyDef.BodyType.StaticBody);
        Fixture fixture = rectCollision(rect.width, rect.height);
        Filter filter = new Filter();
        filter.categoryBits = 0x20;
        filter.maskBits = (short) mask;
        fixture.setFilterData(filter);
        fixture.setSensor(true);
        Vector2 v = rect.getCenter(new Vector2());
        getBody().setTransform(v, 0);
    }
}
