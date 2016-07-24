package pw.ske.stick.entity;

import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import pw.ske.stick.Assets;
import pw.ske.stick.GameScreen;
import pw.ske.stick.util.SpringingContext1D;

public class Text extends Entity {
    private final String text;
    private final boolean bossdeath;
    private SpringingContext1D opacity = new SpringingContext1D(4, 1);

    public Text(String text, boolean bossdeath) {
        this.text = text;
        this.bossdeath = bossdeath;
        getBody().setType(BodyDef.BodyType.StaticBody);

        z = -3;
    }

    @Override
    public void update(float delta) {
        super.update(delta);


        if (bossdeath) {
            opacity.target = GameScreen.i.getBossState() == GameScreen.BossState.DEAD ? 1 : 0;
        } else {
            if (GameScreen.i.getBossState() == GameScreen.BossState.DEAD) {
                opacity.target = 0;
            } else if (GameScreen.i.getPlayer().getBody().getPosition().x + 3 > getBody().getPosition().x) {
                opacity.target = 1;
            }
        }

        opacity.update(delta);
    }

    @Override
    public void render() {
        super.render();

        Vector3 project = GameScreen.i.getCamera().project(new Vector3(getBody().getPosition().x, getBody().getPosition().y, 0));

        GameScreen.i.getBatch().enableBlending();
        GameScreen.i.getBatch().begin();

        BitmapFontCache cache = Assets.font.getCache();
        cache.setText(text, project.x, project.y + Assets.font.getCapHeight());
        cache.setAlphas(opacity.value);
        cache.draw(GameScreen.i.getBatch());
        GameScreen.i.getBatch().end();
        //GameScreen.i.getBatch().disableBlending();
    }
}
