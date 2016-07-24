package pw.ske.stick.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import pw.ske.stick.Assets;
import pw.ske.stick.GameScreen;
import pw.ske.stick.Palette;
import pw.ske.stick.util.SpringingContext2D;

public class GodBoss extends Boss {
    private final Fixture fixture;
    private BossState state = BossState.WAITING;
    private SpringingContext2D posSpring = new SpringingContext2D(8, 1);
    private float timer;
    private float timer2;

    public GodBoss() {
        super("MEEP");

        fixture = rectCollision(2, 2);
        maxHealth = health = 30;
        damageOnTouch = 1;
        Filter filter = new Filter();
        filter.categoryBits = 0x80;
        filter.maskBits = 0x7;
        fixture.setFilterData(filter);

        size.getTarget().set(2, 2);

        getBody().setType(BodyDef.BodyType.DynamicBody);
        getBody().setAwake(false);

        WedgeHat wh = new WedgeHat(this, new Vector2(0, 1.45f));
        GameScreen.i.getEntities().add(wh);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        boolean spring = false;

        if (state == BossState.CHARGING) {
            float last = timer;
            timer += delta;
            posSpring.getTarget().set(29, 18);
            if (timer > 1) {
                spring = true;
                if (last <= 1){
                    Assets.ding.play();
                }
            }

            if (getBody().getPosition().dst(posSpring.getTarget()) < 0.5f) {
                state = BossState.PISSED;
                size.getVelocity().set(50, 50);
                getBody().setType(BodyDef.BodyType.DynamicBody);
                getBody().setLinearVelocity(0, 0);
                timer = 0;
                spring = false;
            }
        } else if (state == BossState.PISSED) {
            timer += delta;
            spring = true;

            getBody().setAngularVelocity(5);

            if (timer > 2) {
                state = BossState.STOMP;
                getBody().setAngularVelocity(0);
                getBody().setTransform(getBody().getPosition(), 0);
                getBody().setFixedRotation(true);
                timer = 0;
            }
        } else if (state == BossState.WALLS) {
            if (timer == 0) {
                BossSweep s = new BossSweep(13.5f);
                GameScreen.i.getEntities().add(s);
            }
            timer += delta;
        } else if (state == BossState.STOMP) {
            float lastmod1 = timer % 1;
            float last = timer;
            timer += delta;

            float mod1 = timer % 1;

            spring = true;
            getBody().setAngularVelocity(0);
            posSpring.setFrequency(2);
            if (mod1 < 0.6f) {
                posSpring.getTarget().set(GameScreen.i.getPlayer().getBody().getPosition().x + GameScreen.i.getPlayer().getBody().getLinearVelocity().x * 0.4f, 20);
            } else {
                posSpring.getTarget().y = GameScreen.i.getPlayer().getBody().getPosition().y - 2;
            }

            if (lastmod1 <= 0.6f && mod1 > 0.6f) {
                Assets.downy.play();
                size.getVelocity().set(-50, 50);
            }

            if (last == 0 || (lastmod1 > 0.5f && mod1 < 0.5f)) {
                Assets.shut.play();
                size.getVelocity().set(-50, 50);
            }

            if (timer > 3) {
                state = BossState.CHASE;
                Assets.boop.play();
                timer = 0;
            }
        } else if (state == BossState.CHASE) {
            spring = true;
            getBody().setAngularVelocity(5);
            posSpring.getTarget().set(GameScreen.i.getPlayer().getBody().getPosition());
            posSpring.setFrequency(1);
            timer += delta;
            if (timer > 3) {
                state = BossState.SPIN;
                size.getVelocity().set(50, 50);
                Assets.uppy.play();
                timer = 0;
            }
        } else if (state == BossState.SPIN) {
            getBody().setAngularVelocity(10);
            color = Palette.FOREGROUND;

            spring = true;
            posSpring.getTarget().set(29, 18);

            timer += delta;
            if (timer > 0.5f) {
                timer2 += delta;
                while (timer2 > 0.03f) {
                    Assets.trow.play();
                    timer2 -= 0.03f;

                    Bullet b = new Bullet();
                    b.getBody().setTransform(getBody().getPosition(), 0);
                    b.getBody().setGravityScale(0);
                    b.getBody().setLinearVelocity(new Vector2(25, 0).rotateRad(getBody().getAngle()));

                    GameScreen.i.getEntities().add(b);
                }
            }

            if (timer > 3.5f) {
                timer = 0;
                timer2 = 0;
                state = BossState.BACKFORTH;
                getBody().setAngularVelocity(0);
                color = Palette.HIGHLIGHT;
                size.getVelocity().set(50, 50);
            }
        } else if (state == BossState.BACKFORTH) {
            getBody().setAngularVelocity(0);
            getBody().setTransform(getBody().getPosition(), 0);
            float last = timer;
            timer += delta;

            fixture.setSensor(true);
            posSpring.setFrequency(2.5f);

            spring = true;
            if (MathUtils.floor(last * 1.5f) != MathUtils.floor(timer * 1.5f)) {
                Assets.boop.play();
                boolean dir = MathUtils.floor(last * 2) % 2 == 0;

                posSpring.getTarget().y = GameScreen.i.getPlayer().getBody().getPosition().y;
                posSpring.getTarget().x = dir ? 5 : 55;

                size.getVelocity().set(50, -50);
            }

            if (timer > 3) {
                timer = 0;
                state = BossState.SHOOTAT;
                fixture.setSensor(false);
            }
        } else if (state == BossState.SHOOTAT) {
            spring = true;
            fixture.setSensor(true);

            float angleRad = GameScreen.i.getPlayer().getBody().getPosition().sub(getBody().getPosition()).angleRad();
            getBody().setTransform(getBody().getPosition(), angleRad);

            timer2 += delta;
            while (timer2 > 0.15f) {
                Assets.shut.play(0.7f);
                timer2 -= 0.15f;

                Bullet b = new Bullet();
                b.getBody().setTransform(getBody().getPosition(), 0);
                b.getBody().setGravityScale(0);
                b.getBody().setLinearVelocity(new Vector2(25, 0).rotateRad(angleRad));

                GameScreen.i.getEntities().add(b);
            }

            posSpring.getTarget().set(GameScreen.i.getPlayer().getBody().getPosition());
            posSpring.setFrequency(0.25f);
            timer += delta;
            if (timer > 3) {
                state = BossState.STOMP;
                size.getVelocity().set(50, 50);
                timer = 0;
                fixture.setSensor(false);
            }
        }

        if (spring) {
            posSpring.getPosition().set(getBody().getPosition());
            posSpring.update(delta);
            getBody().setLinearVelocity(posSpring.getVelocity());
        }

        if (getBody().getPosition().y < 6) {
            getBody().setTransform(getBody().getPosition().x, 7, getBody().getAngle());
        }
    }

    @Override
    public void damage(float amount, Vector2 source) {
        if (state != BossState.CHARGING && state != BossState.SPIN) super.damage(amount, source);

        if (state == BossState.WAITING) {
            state = BossState.CHARGING;

            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    getBody().setType(BodyDef.BodyType.DynamicBody);
                }
            });
        }
    }

    @Override
    public void kill() {
        super.kill();
        for (int i = 0; i < 15; i++) {
            HPPickup hp = new HPPickup(2);
            hp.getBody().setTransform(getBody().getPosition(), 0);
            hp.getBody().setLinearVelocity(MathUtils.random(-50, 50), MathUtils.random(-50, 50));
            GameScreen.i.getEntities().add(hp);

            MaxHPPickup mp = new MaxHPPickup(10);
            mp.getBody().setTransform(getBody().getPosition(), 0);
            mp.getBody().setLinearVelocity(MathUtils.random(-50, 50), MathUtils.random(-50, 50));
            GameScreen.i.getEntities().add(mp);
        }
    }

    @Override
    public void renderTransformed(ShapeRenderer sr) {
        sr.ellipse(-size.getPosition().x / 2, -size.getPosition().y / 2, size.getPosition().x, size.getPosition().y, 64);
    }

    public enum BossState {
        WAITING,
        CHARGING,
        PISSED,
        WALLS,
        STOMP,
        CHASE,
        SPIN,
        BACKFORTH,
        SHOOTAT
    }
}
