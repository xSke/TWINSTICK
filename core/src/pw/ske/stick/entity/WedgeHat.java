package pw.ske.stick.entity;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class WedgeHat extends Hat {
    public WedgeHat(Entity anchor, Vector2 offset) {
        super(anchor, offset);
    }

    @Override
    public void renderTransformed(ShapeRenderer sr) {
        sr.triangle(-0.4f, 0.4f, 0.4f, 0.4f, 0f, -0.4f);
        sr.rect(-0.4f, -0.4f, 0.8f, 0.1f);
    }
}
