package pw.ske.stick.entity;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import pw.ske.stick.Assets;
import pw.ske.stick.GameScreen;
import pw.ske.stick.Palette;

public class BasicBoss extends Boss {

    private final Fixture fixture;
    private final Hat hat;
    private int pissedHealth = 20;
    private BossState state = BossState.WAITING;
    private float timer;
    private boolean hasAudioed;

    public BasicBoss() {
        super("Bill");
        color = Palette.FOREGROUND;
        health = maxHealth = 40;
        damageOnTouch = 1;

        getBody().setFixedRotation(true);
        size.getTarget().set(1, 1);
        fixture = rectCollision(3, 3);
        Filter filter = new Filter();
        filter.categoryBits = 0x80;
        filter.maskBits = 0x7;
        fixture.setFilterData(filter);
        fixture.setDensity(999);

        hat = new TopHat(this, new Vector2(0, 0.7f));
        GameScreen.i.getEntities().add(hat);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (state == BossState.CHARGING) {
            body.setLinearVelocity(0, 0);
            timer += delta;
            size.getTarget().set(timer / 2f, timer / 2f);

            if (timer >= 4) {
                size.getTarget().set(3, 3);
                hat.offset.getTarget().set(0, 2f);
                color = Palette.HIGHLIGHT;
                if (!hasAudioed) Assets.ding.play();
                hasAudioed = true;
            }

            if (timer >= 5) {
                hasAudioed = false;
                state = BossState.BACKFORTH;
                body.setType(BodyDef.BodyType.DynamicBody);
                timer = 0;
            }
        } else if (state == BossState.BACKFORTH) {
            timer += delta;
            getBody().setType(BodyDef.BodyType.DynamicBody);


            float direction;
            direction = (MathUtils.floor(timer % 2) == 0) ? -1 : 1;

            getBody().setLinearVelocity(direction * (health < pissedHealth ? 50 : 40), getBody().getLinearVelocity().y);

            shear.target = 0.4f * direction;
            if (timer >= 6) {
                state = BossState.JUMP;
                shear.target = 0;
                timer = 0;
                Assets.uppy.play();
                hasAudioed = false;
            } else {
                if (direction == 1 == hasAudioed) {
                    Assets.boop.play();
                    GameScreen.i.shake(0.4f);
                }
                hasAudioed = direction != 1;
            }

            if (health < pissedHealth) {
                float dd = (GameScreen.i.getPlayer().getBody().getPosition().y + 0.5f) - getBody().getPosition().y;
                float yDir = Math.signum(dd);
                if (Math.abs(dd) > 0.2f) {
                    getBody().setLinearVelocity(getBody().getLinearVelocity().x, yDir * 8);
                }
            }
        } else if (state == BossState.JUMP) {
            timer += delta;

            if (timer < 0.5f && getBody().getPosition().y < 20) {
                getBody().setLinearVelocity(0, 40);
                fixture.setSensor(true);
                getBody().setType(BodyDef.BodyType.KinematicBody);
                size.getTarget().set(2f, 4f);
            } else {
                size.getTarget().set(3f, 3f);
                float dd = GameScreen.i.getPlayer().getBody().getPosition().x - getBody().getPosition().x;
                float direction = Math.signum(dd);

                shear.target = 0.4f * direction;

                if (Math.abs(dd) > 0.5f) getBody().setLinearVelocity(direction * (health < pissedHealth ? 20 : 10), 0);

                if (timer >= (health < pissedHealth ? 2 : 4)) {
                    shear.target = 0;
                    state = BossState.DOWN;
                    getBody().setType(BodyDef.BodyType.DynamicBody);
                    getBody().setLinearVelocity(0, -50);
                    fixture.setSensor(false);
                    size.getVelocity().set(50, -50);
                    Assets.downy.play();
                }
            }
        } else if (state == BossState.LAND) {
            timer += delta;
            if (timer > 1f) {
                timer = 0;
                state = BossState.BACKFORTH;
            }
        }
    }

    @Override
    public void damage(float amount, Vector2 source) {
        if (state != BossState.CHARGING) super.damage(amount, source);

        if (state == BossState.WAITING) {
            state = BossState.CHARGING;
            Assets.crackleup.play();
        }

        if (health < pissedHealth && (health + amount) >= pissedHealth) {
            color = Palette.HIGHLIGHT2;
            state = BossState.BACKFORTH;
            fixture.setSensor(false);
        }

        if (state != BossState.CHARGING) size.getVelocity().set(amount, -amount).scl(30);
    }

    @Override
    public void kill() {
        super.kill();

        for (int i = 0; i < 10; i++) {
            HPPickup hpp = new HPPickup(0.5f);
            hpp.getBody().setTransform(getBody().getPosition(), 0);
            hpp.getBody().setLinearVelocity(new Vector2(0, 50).rotate(MathUtils.random(360)));
            GameScreen.i.getEntities().add(hpp);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        hat.offset.getTarget().set(0, 0.7f);

        /*for (int i = 0; i < 10; i++) {
            DomeHat hpp = new DomeHat(this);
            hpp.getBody().setTransform(getBody().getPosition(), 0);
            hpp.getBody().setLinearVelocity(new Vector2(0, 50).rotate(MathUtils.random(360)));
            GameScreen.i.getEntities().add(hpp);
            Assets.xplode.play(0.3f);
        }*/

        MaxHPPickup mh = new MaxHPPickup(3);
        mh.getBody().setTransform(getBody().getPosition(), 0);
        GameScreen.i.getEntities().add(mh);
    }

    @Override
    public void collide(Entity entity) {
        super.collide(entity);

        if ((entity instanceof Player || entity instanceof BasicTile) && state == BossState.DOWN) {
            timer = 0;
            state = BossState.LAND;
            size.getVelocity().set(100, -100);
            Assets.hit.play();
        }
    }

    @Override
    public void renderTransformed(ShapeRenderer sr) {
        sr.triangle(-size.getPosition().x / 2, -size.getPosition().y / 2, size.getPosition().x / 2, -size.getPosition().y / 2, 0, size.getPosition().y / 2);
    }

    private enum BossState {
        WAITING,
        CHARGING,
        BACKFORTH,
        JUMP,
        DOWN,
        LAND
    }
}
