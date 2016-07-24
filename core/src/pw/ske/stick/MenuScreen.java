package pw.ske.stick;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import pw.ske.stick.util.SpringingContext1D;
import pw.ske.stick.util.SpringingContext2D;

public class MenuScreen extends ScreenAdapter {
    private ShapeRenderer sr;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private SpringingContext2D playSize = new SpringingContext2D(4, 0.25f);
    private SpringingContext1D playColor = new SpringingContext1D(4, 1);

    private float timer;

    private boolean down;

    public MenuScreen() {
        batch = new SpriteBatch();
        sr = new ShapeRenderer();
        camera = new OrthographicCamera();

        playSize.getTarget().set(400, 50);
    }

    @Override
    public void render(float delta) {
        timer += delta;

        Color c = Palette.BACKGROUND;
        Gdx.gl.glClearColor(c.r, c.g, c.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());

        float wmid = Gdx.graphics.getWidth() / 2f;
        float hmid = Gdx.graphics.getHeight() / 2f;

        if (new Rectangle(wmid - 200, hmid - 25, 400, 50).contains(mouse)) {
            playColor.target = 1;

            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                playSize.getTarget().set(375, 55);
            } else {
                if (down) {
                    Game.start();
                }
                playSize.getTarget().set(450, 75);
            }

            down = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        } else {
            playColor.target = 0;
            playSize.getTarget().set(400, 50);
        }

        sr.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        playSize.update(delta);
        playColor.update(delta);

        sr.begin(ShapeRenderer.ShapeType.Filled);

        sr.setColor(Palette.FOREGROUND);
        sr.rect(wmid - playSize.getPosition().x / 2f, hmid - playSize.getPosition().y / 2f, playSize.getPosition().x, playSize.getPosition().y);
        sr.setColor(Palette.BACKGROUND.cpy().lerp(Palette.FOREGROUND, playColor.value));
        sr.rect(wmid - (playSize.getPosition().x / 2f - 5), hmid - playSize.getPosition().y / 2f + 5, playSize.getPosition().x - 10, playSize.getPosition().y - 10);

        sr.end();

        batch.begin();
        Assets.fontbig.setUseIntegerPositions(false);
        Assets.fontbig.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        float scl = MathUtils.sin(timer * 2) * 0.1f + 1;
        Assets.fontbig.getData().setScale(scl);
        Assets.fontbig.setColor(Palette.FOREGROUND);
        Assets.fontbig.draw(batch, "TWIN STICK", wmid, hmid * 1.3f + (Assets.fontbig.getCapHeight() / 2f * scl), 1, Align.center, false);

        BitmapFont font = Assets.font;
        font.setColor(Palette.FOREGROUND.cpy().lerp(Palette.BACKGROUND, playColor.value));
        font.draw(batch, "PLAY", wmid, hmid + 21, 1, Align.center, false);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        sr.dispose();
    }
}
