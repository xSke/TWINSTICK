package pw.ske.stick.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import pw.ske.stick.Assets;
import pw.ske.stick.GameScreen;
import pw.ske.stick.Palette;
import pw.ske.stick.util.SpringingContext1D;

public class UI {
    private OrthographicCamera camera;

    private float bossHealthRemovalTimer = -1;

    private SpringingContext1D playerHealth = new SpringingContext1D(4, 1);
    private SpringingContext1D bossHealth = new SpringingContext1D(4, 1);
    private SpringingContext1D bossOpacity = new SpringingContext1D(1, 1);

    public UI() {
        camera = new OrthographicCamera();
    }

    public void render(ShapeRenderer sr, SpriteBatch batch) {
        playerHealth.update(Gdx.graphics.getDeltaTime());
        bossHealth.update(Gdx.graphics.getDeltaTime());
        bossOpacity.update(Gdx.graphics.getDeltaTime());

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sr.identity();
        batch.setProjectionMatrix(camera.combined);
        sr.setProjectionMatrix(camera.combined);

        float width = 400;
        float height = 40;

        playerHealth.target = Math.max(0, GameScreen.i.getPlayer().getHealth() / GameScreen.i.getPlayer().getMaxHealth());
        float playerMax = GameScreen.i.getPlayer().getMaxHealth() / 8f;

        bossHealth.target = 0;
        if (GameScreen.i.getBoss() != null)
            bossHealth.target = Math.max(0, GameScreen.i.getBoss().getHealth() / GameScreen.i.getBoss().getMaxHealth());
        bossOpacity.target = GameScreen.i.getBossState() == GameScreen.BossState.FIGHTING ? 1 : 0;

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        batch.begin();
        BitmapFont font = Assets.font;
        font.setColor(Color.WHITE);
        font.setUseIntegerPositions(true);
        font.getCache().setText("YOU", 65, screenHeight - height - 40);
        font.getCache().draw(batch);

        if (GameScreen.i.getBoss() != null) {
            font.getCache().setText(GameScreen.i.getBoss().getName().toUpperCase(), screenWidth - 65, screenHeight - height - 40, 1, Align.right, false);
            font.getCache().setAlphas(bossOpacity.value);
            font.getCache().draw(batch);
        }
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Palette.FOREGROUND);
        sr.rect(40, screenHeight - height - 40, width * playerMax, height);
        sr.setColor(Palette.BACKGROUND);
        sr.rect(45, screenHeight - height - 35, width * playerMax - 10, height - 10);
        sr.setColor(Palette.HIGHLIGHT);
        sr.rect(50, screenHeight - height - 30, (width * playerHealth.value * playerMax) - 20, height - 20);
        if (GameScreen.i.getBoss() != null) {
            sr.setColor(Palette.FOREGROUND);
            sr.getColor().a = bossOpacity.value;
            sr.rect(screenWidth - width - 40, screenHeight - height - 40, width, height);
            sr.setColor(Palette.BACKGROUND);
            sr.getColor().a = bossOpacity.value;
            sr.rect(screenWidth - width - 35, screenHeight - height - 35, width - 10, height - 10);
            sr.setColor(Palette.HIGHLIGHT);
            sr.getColor().a = bossOpacity.value;
            sr.rect(screenWidth - width - 30, screenHeight - height - 30, (width - 20) * bossHealth.value, height - 20);
        }
        sr.end();


        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0, 0, 0, GameScreen.i.getBlackout().value);
        sr.rect(0, 0, screenWidth, screenHeight);
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
