package pw.ske.stick.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import pw.ske.stick.GameScreen;
import pw.ske.stick.Palette;
import pw.ske.stick.util.SpringingContext1D;
import pw.ske.stick.util.SpringingContext2D;

public class Entity implements Comparable<Entity> {
    protected Body body;
    protected Color color = Palette.HIGHLIGHT;
    protected SpringingContext2D size;
    protected SpringingContext1D shear;
    protected float z;
    protected boolean dying;
    protected float damageOnTouch;
    protected float maxKillTimer = 0.3f;
    private Affine2 _tmpAff = new Affine2();
    private float killTimer;
    private boolean hasDestroyedBody;

    public Entity() {
        body = GameScreen.i.getWorld().createBody(new BodyDef());
        body.setUserData(this);
        body.setType(BodyDef.BodyType.DynamicBody);
        size = new SpringingContext2D(4, 0.15f);
        shear = new SpringingContext1D(4, 0.1f);
    }

    public Fixture rectCollision(float w, float h) {
        return rectCollision(w, h, 0, 0);
    }

    public Fixture rectCollision(float w, float h, float x, float y) {
        PolygonShape s = new PolygonShape();
        s.setAsBox(w / 2f, h / 2f, new Vector2(x, y), 0);
        Fixture fixture = body.createFixture(s, 1f);
        s.dispose();
        return fixture;
    }

    public void update(float delta) {
        shear.update(delta);
        size.update(delta);
        size.getPosition().x = Math.max(size.getPosition().x, 0);
        size.getPosition().y = Math.max(size.getPosition().y, 0);

        if (dying) {
            killTimer -= delta;
            size.getVelocity().set(size.getTarget()).scl(10);
            if (killTimer < 0.6f) {
                size.getTarget().set(0, 0);
            }

            if (killTimer < 0f) {
                destroy();
            }
        }
    }

    public void destroy() {
        if (!GameScreen.i.getIsResetting()) GameScreen.i.getEntities().removeValue(Entity.this, true);

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (!hasDestroyedBody) {
                    body.setUserData(null);
                    GameScreen.i.getWorld().destroyBody(body);
                    hasDestroyedBody = true;
                }
            }
        });
    }

    public void render() {
        ShapeRenderer sr = GameScreen.i.getShapeRenderer();

        _tmpAff.idt();
        _tmpAff.translate(body.getPosition().x, body.getPosition().y);
        _tmpAff.rotate(body.getAngle() * MathUtils.radiansToDegrees);
        _tmpAff.shear(shear.value, 0);
        sr.getTransformMatrix().set(_tmpAff);
        sr.updateMatrices();

        sr.setColor(color);
        renderTransformed(sr);
    }

    public void renderTransformed(ShapeRenderer sr) {
        sr.rect(-size.getPosition().x / 2f, -size.getPosition().y / 2f, size.getPosition().x, size.getPosition().y);
    }

    public Body getBody() {
        return body;
    }

    @Override
    public int compareTo(Entity o) {
        return Float.compare(z, o.z);
    }

    public void kill() {
        dying = true;
        killTimer = maxKillTimer;
        size.getTarget().scl(1.5f);
        GameScreen.i.pause(0.05f);
    }

    public void collide(Entity entity) {
        if (damageOnTouch > 0 && entity instanceof LivingEntity && !dying) {
            collideDamage(entity);
        }
    }

    protected void collideDamage(Entity toDamage) {
        ((LivingEntity) toDamage).damage(damageOnTouch, getBody().getPosition());
    }
}
