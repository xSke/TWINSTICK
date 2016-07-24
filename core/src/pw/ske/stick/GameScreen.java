package pw.ske.stick;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import pw.ske.stick.entity.Boss;
import pw.ske.stick.entity.Entity;
import pw.ske.stick.entity.Player;
import pw.ske.stick.ui.UI;
import pw.ske.stick.util.CollisionTracker;
import pw.ske.stick.util.MapLoader;
import pw.ske.stick.util.SpringingContext1D;

public class GameScreen extends ScreenAdapter {
    public static GameScreen i;
    private World world;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Array<Entity> entities = new Array<Entity>();
    private CollisionTracker collisionTracker = new CollisionTracker();
    private Player player;
    private UI ui;
    private BossState bossState;
    private Boss boss;
    private float shake;
    private float pause;
    private String currentMap;
    private Vector2 mapSize;
    private boolean isLoading;
    private float loadTimer;
    private boolean isResetting;
    private SpringingContext1D blackout = new SpringingContext1D(0.7f, 1);
    private float playerMaxHealth = 5;
    private boolean freezeControls;

    public GameScreen() {
        // TODO: polish

        i = this;
        world = new World(new Vector2(0, -140), true);
        world.setContactListener(collisionTracker);

        camera = new OrthographicCamera();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        currentMap = "premap.tmx";
        reset(currentMap);

        ui = new UI();
    }

    public void render(float delta) {
        Color c = Palette.BACKGROUND;
        Gdx.gl.glClearColor(c.r, c.g, c.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            reset();
        }

        shake -= delta;

        blackout.update(delta);

        if (isLoading) {
            loadTimer += delta;

            if (loadTimer > 2) {
                reset();
            }

            delta = 0.00001f;
        }

        if (pause > delta) {
            pause -= delta;
            delta = 0.00001f;
        } else {
            delta -= pause;
            pause = 0;
        }

        world.step(delta, 5, 5);

        float rightMargin = 2;
        camera.position.x = MathUtils.clamp(player.getBody().getPosition().x, camera.viewportWidth / 2f, (mapSize.x - rightMargin) - camera.viewportWidth / 2f);
        camera.position.y = Math.max(camera.viewportHeight / 2f, player.getBody().getPosition().y - camera.viewportHeight * 0.25f);

        shake = Math.max(shake, 0);
        camera.position.x += MathUtils.random(-shake, shake);
        camera.position.y += MathUtils.random(-shake, shake);

        camera.update();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        entities.sort();
        for (Entity e : entities) {
            e.update(delta);
            e.render();
        }
        shapeRenderer.end();

        ui.render(shapeRenderer, batch);

        //if (Gdx.input.isKeyPressed(Input.Keys.C)) {
            /*HPPickup p = new HPPickup(0);
            p.getBody().setTransform(23, 10, 0);
            entities.add(p);*/
        //}

        //new Box2DDebugRenderer().render(world, camera.combined);
    }

    public void reset() {
        reset(currentMap);
    }

    public void resetTimer(String map) {
        currentMap = map;
        isLoading = true;
        blackout.target = 1;
    }

    public void reset(String map) {
        currentMap = map;

        if (currentMap.equals("theend.tmx")) {
            Game.theend();
            return;
        }

        isResetting = true;
        for (Entity entity : entities) {
            entity.destroy();
        }

        isResetting = false;
        entities.clear();
        mapSize = new MapLoader().loadMap(Gdx.files.internal(map));

        blackout.value = 1;
        blackout.target = 0;
        isLoading = false;
        loadTimer = 0;
        shake = 0;
        pause = 0;
        bossState = BossState.PREFIGHT;
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / 32f, height / 32f);
    }

    @Override
    public void dispose() {
        world.dispose();
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public World getWorld() {
        return world;
    }

    public CollisionTracker getCollisionTracker() {
        return collisionTracker;
    }

    public Array<Entity> getEntities() {
        return entities;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Boss getBoss() {
        return boss;
    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }

    public void shake(float amnt) {
        shake = Math.max(shake, amnt);
    }

    public void pause(float amnt) {
        pause = Math.max(pause, amnt);
    }

    public String getCurrentMap() {
        return currentMap;
    }

    public BossState getBossState() {
        return bossState;
    }

    public void setBossState(BossState bossState) {
        this.bossState = bossState;
    }

    public SpringingContext1D getBlackout() {
        return blackout;
    }

    public float getPlayerMaxHealth() {
        return playerMaxHealth;
    }

    public void setPlayerMaxHealth(float playerMaxHealth) {
        this.playerMaxHealth = playerMaxHealth;
    }

    public boolean isFreezeControls() {
        return freezeControls;
    }

    public void setFreezeControls(boolean freezeControls) {
        this.freezeControls = freezeControls;
    }

    public boolean getIsResetting() {
        return isResetting;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public enum BossState {
        PREFIGHT,
        FIGHTING,
        DEAD
    }
}
