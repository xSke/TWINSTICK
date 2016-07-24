package pw.ske.stick.entity;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class TopHat extends Hat {
    public TopHat(Entity anchor, Vector2 offset) {
        super(anchor, offset);
    }

    @Override
    public void renderTransformed(ShapeRenderer sr) {
        sr.rect(-0.3f, -0.3f, 0.6f, 0.6f);
        sr.rect(-0.5f, -0.4f, 1f, 0.1f);
    }
}
