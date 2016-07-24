package pw.ske.stick.entity;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class DomeHat extends Hat {
    public DomeHat(Entity anchor, Vector2 offset) {
        super(anchor, offset);
    }

    @Override
    public void renderTransformed(ShapeRenderer sr) {
        sr.arc(0, 0, 0.4f, 0, 180, 16);
        sr.rect(-0.5f, -0.1f, 1f, 0.1f);
    }
}
