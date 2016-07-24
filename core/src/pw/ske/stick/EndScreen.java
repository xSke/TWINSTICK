package pw.ske.stick;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;

public class EndScreen extends ScreenAdapter {
    private float timer;
    private SpriteBatch batch;
    private ShapeRenderer sr;
    private OrthographicCamera camera;

    public EndScreen() {
        batch = new SpriteBatch();
        sr = new ShapeRenderer();
        camera = new OrthographicCamera();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void render(float delta) {

        Color c = Palette.BACKGROUND;
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sr.setProjectionMatrix(camera.combined);

        timer += delta;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(c.r, c.g, c.b, timer * 1.5f);
        sr.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        Assets.fontbig.setColor(Palette.FOREGROUND);
        Assets.fontbig.getData().setScale(1);
        Assets.font.setColor(Palette.FOREGROUND);
        BitmapFontCache cache = Assets.font.getCache();
        BitmapFontCache cachebig = Assets.fontbig.getCache();

        batch.begin();
        cachebig.setText("THE END", Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() * 0.7f, 1, Align.center, false);
        cachebig.setAlphas(MathUtils.clamp(timer * 1.5f - 1, 0, 1));
        cachebig.draw(batch);

        cache.setText("By Ske", Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() * 0.6f, 1, Align.center, false);
        cache.setAlphas(MathUtils.clamp(timer * 1.5f - 1.5f, 0, 1));
        cache.draw(batch);

        cache.setText("@SkeDevs", Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() * 0.55f, 1, Align.center, false);
        cache.setAlphas(MathUtils.clamp(timer * 1.5f - 2f, 0, 1));
        cache.draw(batch);
        batch.end();
    }
}
