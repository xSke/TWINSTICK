package pw.ske.stick.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import pw.ske.stick.GameScreen;
import pw.ske.stick.Palette;
import pw.ske.stick.util.SpringingContext1D;

public class RunBoss extends Boss {
    private final Fixture fixture;
    private BossState state = BossState.WAITING;
    private SubState substate = SubState.ROOT;
    private float timer;
    private float subtimer;
    private SpringingContext1D xSpring = new SpringingContext1D(1, 1);

    public RunBoss() {
        super("YOU SHOULD PROBABLY RUN");

        health = 1;
        maxHealth = 1;
        damageOnTouch = 2;

        size.getTarget().set(50, 50);

        getBody().setType(BodyDef.BodyType.KinematicBody);
        getBody().setTransform(-50, 0, 0);

        fixture = rectCollision(200, 50, -75, 0);
        fixture.setSensor(true);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        xSpring.value = getBody().getPosition().x;

        if (state == BossState.WAITING) {
            xSpring.target = -50;
            getBody().setTransform(-50, 0, 0);

            if (GameScreen.i.getBossState() == GameScreen.BossState.FIGHTING) {
                GameScreen.i.getBoss().setName("");
                state = BossState.COMING;
                GameScreen.i.setFreezeControls(true);
                timer = 0;
            }
        } else if (state == BossState.COMING) {
            float last = timer;
            timer += delta;

            if (timer > 3 && last <= 3) {
                xSpring.target = -15;
                GameScreen.i.getBoss().setName("HEY...");
            }

            if (timer > 6 && last <= 6) {
                xSpring.target = -8;
                GameScreen.i.getBoss().setName("YOU");
                GameScreen.i.getBoss().setHealth(1.05f);
            }

            if (timer > 7 && last <= 7) {
                xSpring.target = -4;
                GameScreen.i.getBoss().setName("YOU SHOULD");
            }

            if (timer > 8 && last <= 8) {
                xSpring.target = 0;
                GameScreen.i.getBoss().setName("YOU SHOULD PROBABLY");
                GameScreen.i.getBoss().setHealth(1.1f);
            }

            if (timer > 11 && last <= 11) {
                GameScreen.i.getBoss().setName("RRRUUUUUUUNNNNNN!!!!");
                GameScreen.i.setFreezeControls(false);
                GameScreen.i.getPlayer().setSpeed(30);
                GameScreen.i.getBoss().setHealth(99999f);
            }
            if (timer > 11.5f) {
                state = BossState.RUN;
            }
        } else if (state == BossState.RUN) {
            float last = timer;
            timer += delta;

            xSpring.target += delta * 20;

            float limit = GameScreen.i.getPlayer().getBody().getPosition().x - 25f;
            if (xSpring.target < limit) {
                xSpring.target = limit + (float) Math.sin(timer * 2) * 2;
            }

            color = Palette.HIGHLIGHT.cpy().lerp(Palette.HIGHLIGHT2, (float) (0.5f + Math.sin(timer * 6) * 0.5f));
            if (MathUtils.floor(last) != MathUtils.floor(timer)) size.getVelocity().x = 100;
            if (MathUtils.floor(last + 0.5f) != MathUtils.floor(timer + 0.5f)) size.getVelocity().x = -100;

            float lastSub = subtimer;
            subtimer += delta;
            if (substate == SubState.ROOT) {
                if (subtimer > 2) {
                    substate = Array.with(
                            //SubState.BULLETS,
                            SubState.SWEEP
                    ).random();
                    subtimer = 0;
                }
            } else if (substate == SubState.BULLETS) {
                substate = SubState.ROOT;
            } else if (substate == SubState.SWEEP) {
                if (MathUtils.floor(lastSub * 0.8f) != MathUtils.floor(subtimer * 0.8f)) {
                    //BossSweep sweep = new BossSweep(this, MathUtils.random(1, 3) * 2);
                    //GameScreen.i.getEntities().add(sweep);
                }

                if (subtimer > 3) {
                    subtimer = 0;
                    substate = SubState.ROOT;
                }
            }
        }

        xSpring.update(delta);
        getBody().setLinearVelocity(xSpring.velocity, 0);

        if (GameScreen.i.getCollisionTracker().getCollisions().containsKey(fixture) && state == BossState.RUN) {
            for (Fixture fix : GameScreen.i.getCollisionTracker().getCollisions().get(fixture)) {
                Object ud = fix.getBody().getUserData();
                if (ud instanceof Player) {
                    ((Player) ud).damage(damageOnTouch, getBody().getPosition());
                }
            }
        }
    }

    @Override
    protected void collideDamage(Entity toDamage) {
        //super.collideDamage(toDamage);
    }

    public enum BossState {
        WAITING,
        COMING,
        RUN
    }

    public enum SubState {
        ROOT,
        BULLETS,
        SWEEP
    }
}
