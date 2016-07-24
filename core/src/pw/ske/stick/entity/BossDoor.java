package pw.ske.stick.entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import pw.ske.stick.Assets;
import pw.ske.stick.GameScreen;
import pw.ske.stick.Palette;

public class BossDoor extends Entity {
    private final Fixture fixture;
    private Rectangle rect;
    private boolean closed;

    public BossDoor(Rectangle rect) {
        this.rect = rect;

        getBody().setType(BodyDef.BodyType.StaticBody);
        fixture = rectCollision(rect.width, rect.height);
        Vector2 v = rect.getCenter(new Vector2());
        getBody().setTransform(v, 0);

        color = Palette.FOREGROUND;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        boolean newClosed = GameScreen.i.getBossState() == GameScreen.BossState.FIGHTING;
        if (newClosed) {
            fixture.setSensor(false);
            size.getTarget().set(rect.width, rect.height);
        } else {
            fixture.setSensor(true);
            size.getTarget().set(rect.width, 0);
        }

        if (!closed && newClosed) {
            Assets.shut.play();
        }
        closed = newClosed;
    }
}
