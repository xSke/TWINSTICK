package pw.ske.stick.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import pw.ske.stick.Assets;
import pw.ske.stick.GameScreen;
import pw.ske.stick.Palette;

public class Player extends LivingEntity {
    private Fixture sensor;

    private Stick stickA;
    private Stick stickB;

    private boolean lastDown;
    private boolean doubleJump;

    private Array<Hat> hats = new Array<Hat>();
    private int speed = 15;

    private boolean shouldBoom;

    public Player() {
        health = maxHealth = GameScreen.i.getPlayerMaxHealth();
        invincibilityTime = 0.4f;

        getBody().setFixedRotation(true);
        Fixture fixture = rectCollision(1, 1);
        Filter filter = new Filter();
        filter.categoryBits = 0x02;
        fixture.setFilterData(filter);

        size.getTarget().set(1, 1);

        sensor = rectCollision(0.95f, 0.05f, 0, -0.5f);
        sensor.setSensor(true);
        Filter filterSensor = new Filter();
        filterSensor.categoryBits = 0x02;
        filterSensor.maskBits = 0x01;
        sensor.setFilterData(filterSensor);

        stickA = new Stick(0, Palette.HIGHLIGHT2, this);
        stickB = new Stick(1, Palette.HIGHLIGHT2, this);

        GameScreen.i.getEntities().add(stickA);
        GameScreen.i.getEntities().add(stickB);

        damagePauseAmount = 0.05f;
        damageShakeAmount = 0.2f;
    }

    public Stick getHeldStick() {
        if (stickA.getState() == Stick.StickState.HELD) return stickA;
        if (stickB.getState() == Stick.StickState.HELD) return stickB;
        return null;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (dying && shouldBoom && delta > 0.0001f) {
            Assets.xplode.play();
            shouldBoom = false;
            GameScreen.i.shake(0.5f);
        }

        float input = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) input--;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) input++;
        if (GameScreen.i.isFreezeControls()) input = 0;

        boolean grounded = false;
        if (GameScreen.i.getCollisionTracker().getCollisions().containsKey(sensor)) {
            for (Fixture fix : GameScreen.i.getCollisionTracker().getCollisions().get(sensor)) {
                if (fix.getBody().getUserData() instanceof BasicTile) grounded = true;
            }
        }

        if ((grounded || !doubleJump) && (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.W)) && !GameScreen.i.isFreezeControls()) {
            body.setLinearVelocity(body.getLinearVelocity().x, 50);
            Assets.jump.play();
            size.getVelocity().set(-20, 20);

            if (!grounded) doubleJump = true;
        }
        if (grounded) doubleJump = false;

        boolean mouseDown = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        if (mouseDown && !lastDown && !GameScreen.i.isFreezeControls()) {
            Stick held = getHeldStick();
            if (held != null) {
                held.throwStick(getMouseAngle());
            }
        }
        lastDown = mouseDown;

        body.setLinearVelocity(speed * input, body.getLinearVelocity().y);
        shear.target = input * 0.2f;

        if (getBody().getPosition().y < -5) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    GameScreen.i.reset();
                }
            });
        }
    }

    public float getMouseAngle() {
        Vector3 worldMouse = GameScreen.i.getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        float angle = new Vector2(worldMouse.x, worldMouse.y).sub(getBody().getPosition()).angle();
        return angle;
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void kill() {
        this.maxKillTimer = 1.5f;
        super.kill();
        Assets.shut.play();
        GameScreen.i.pause(1);
        GameScreen.i.shake(0.2f);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                shouldBoom = true;
            }
        });
    }

    @Override
    public void destroy() {
        super.destroy();
        if (!GameScreen.i.getIsResetting()) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    GameScreen.i.reset();
                }
            });
        }
    }

    public Array<Hat> getHats() {
        return hats;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
