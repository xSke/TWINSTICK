package pw.ske.stick.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import pw.ske.stick.Assets;
import pw.ske.stick.GameScreen;
import pw.ske.stick.Palette;
import pw.ske.stick.util.SpringingContext2D;

public class OtherBoss extends Boss {
    private final Fixture fixture;
    private BossState state = BossState.WAITING;
    private float timer;
    private SpringingContext2D centerSpring = new SpringingContext2D(1, 1);
    private Array<LivingEntity> offspring = new Array<LivingEntity>();
    private int upCount = 2;
    private float levelCenter = 40;

    public OtherBoss() {
        super("Qubo");

        health = maxHealth = 50;
        size.getTarget().set(2, 2);
        fixture = rectCollision(2, 2);
        Filter filter = new Filter();
        filter.categoryBits = 0x80;
        filter.maskBits = 0x7;
        fixture.setFilterData(filter);
        fixture.setDensity(9999);
        damageOnTouch = 1.5f;

        DomeHat wh = new DomeHat(this, new Vector2(0, 1.2f));
        GameScreen.i.getEntities().add(wh);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (state == BossState.WAITING) {
            getBody().setType(BodyDef.BodyType.KinematicBody);
            getBody().setLinearVelocity(0, 0);
            if (GameScreen.i.getBossState() == GameScreen.BossState.FIGHTING) {
                state = BossState.DROPPING;
                upCount = 2;
            }
        } else if (state == BossState.DROPPING) {
            timer += delta;

            centerSpring.getPosition().set(getBody().getPosition());
            centerSpring.getTarget().set(GameScreen.i.getPlayer().getBody().getPosition().x, 20);
            centerSpring.update(delta);
            getBody().setLinearVelocity(centerSpring.getVelocity());


            if (timer > 1) {
                getBody().setType(BodyDef.BodyType.DynamicBody);
                getBody().setLinearVelocity(0, -300);
                size.getVelocity().set(-50, 50);
                state = BossState.FALL;
                fixture.setSensor(false);
            }
        } else if (state == BossState.CENTER) {
            float last = timer;
            timer += delta;
            getBody().setType(BodyDef.BodyType.DynamicBody);

            if (timer > 1) {
                if (last <= 1) {
                    Assets.downy.play();
                }
                centerSpring.getPosition().set(getBody().getPosition());
                centerSpring.update(delta);
                getBody().setLinearVelocity(centerSpring.getVelocity());
            }

            getBody().setGravityScale(0);
            if (getBody().getPosition().dst(centerSpring.getTarget()) < 1f) {
                color = Palette.FOREGROUND;
                size.getVelocity().set(-50, -50);
                Assets.shut.play();
                getBody().setType(BodyDef.BodyType.KinematicBody);
                getBody().setLinearVelocity(0, 0);
                getBody().setAngularVelocity(-5);
                state = BossState.FREEZE;
                timer = 0;
            }
        } else if (state == BossState.FREEZE) {
            float last = timer;
            timer += delta;
            if (timer > 1 && last <= 1) {
                size.getVelocity().set(40, 40);

                spawnChasers();
            }

            if (timer > 1) {
                boolean allDead = true;
                for (LivingEntity e : offspring) {
                    if (e.health > 0) allDead = false;
                }

                if (allDead) {
                    offspring.clear();
                    color = Palette.HIGHLIGHT;
                    getBody().setType(BodyDef.BodyType.DynamicBody);
                    getBody().setGravityScale(0);
                    state = BossState.CHASE;
                    Assets.ding.play();
                    timer = 0;
                }
            }
        } else if (state == BossState.CHASE) {
            timer += delta;

            centerSpring.getTarget().set(GameScreen.i.getPlayer().getBody().getPosition());
            centerSpring.getPosition().set(getBody().getPosition());
            centerSpring.update(delta);
            getBody().setLinearVelocity(centerSpring.getVelocity());

            if (timer > 3) {
                state = BossState.BACKFORTH;
                timer = 0;
            }
        } else if (state == BossState.BACKFORTH) {
            float last = timer;
            timer += delta;

            if (MathUtils.floor(last % 2) != MathUtils.floor(timer % 2)) {
                float direction = MathUtils.floor(last % 2) == 0 ? 1 : -1;
                centerSpring.getTarget().set(levelCenter + (10 * direction), GameScreen.i.getPlayer().getBody().getPosition().y);
                Assets.boop.play();
            }

            centerSpring.getPosition().set(getBody().getPosition());
            centerSpring.update(delta);
            getBody().setLinearVelocity(centerSpring.getVelocity());
            centerSpring.setFrequency(4);

            if (timer > 5) {
                centerSpring.setFrequency(1);

                upCount = 0;
                state = BossState.UP;
                getBody().setGravityScale(0);
                fixture.setSensor(true);
                getBody().setLinearVelocity(0, 100);
                timer = 0;
            }
        } else if (state == BossState.UP) {
            if (getBody().getPosition().y > 15) {
                getBody().setGravityScale(0);
                state = BossState.DROPPING;
            }
        } else if (state == BossState.LANDWAIT) {
            timer += delta;

            if (timer > 1) {
                if (upCount == 2) {
                    state = BossState.CENTER;
                    timer = 0;
                    centerSpring.getPosition().set(getBody().getPosition());
                    centerSpring.getTarget().set(levelCenter, 14);
                } else {
                    size.getVelocity().set(-50, 50);
                    upCount++;
                    state = BossState.UP;
                    timer = 0;
                    getBody().setLinearVelocity(0, 100);
                    fixture.setSensor(true);
                    Assets.uppy.play();
                }
            }
        }
    }

    @Override
    public void kill() {
        super.kill();
        fixture.setSensor(false);

        for (int i = 0; i < 10; i++) {
            HPPickup hpp = new HPPickup(0.5f);
            hpp.getBody().setTransform(getBody().getPosition(), 0);
            hpp.getBody().setLinearVelocity(MathUtils.random(-50, 50), MathUtils.random(-50, 50));
            GameScreen.i.getEntities().add(hpp);
        }

        MaxHPPickup mh = new MaxHPPickup(3);
        mh.getBody().setTransform(getBody().getPosition(), 0);
        GameScreen.i.getEntities().add(mh);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    private void spawnChasers() {
        Assets.shut.play();
        for (int i = 0; i < 10; i++) {
            Chaser c = new Chaser();
            c.getBody().setTransform(getBody().getPosition().x + (i - 5), getBody().getPosition().y, 0);
            c.getBody().setLinearVelocity((i - 5) * 50, -50);
            GameScreen.i.getEntities().add(c);
            offspring.add(c);
        }
    }

    @Override
    public void collide(Entity entity) {
        super.collide(entity);

        if (entity instanceof BasicTile || entity instanceof Player) {
            if (state == BossState.FALL) {
                Assets.boop.play();
                state = BossState.LANDWAIT;
                timer = 0;
                size.getVelocity().set(50, -50);
            }
        }
    }

    @Override
    public void damage(float amount, Vector2 source) {
        if (state != BossState.FREEZE) super.damage(amount, source);
    }

    @Override
    public void render() {
        super.render();
    }

    public enum BossState {
        WAITING,
        DROPPING,
        FALL,
        CENTER,
        FREEZE,
        CHASE,
        BACKFORTH,
        UP,
        LANDWAIT
    }
}
