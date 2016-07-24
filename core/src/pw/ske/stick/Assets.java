package pw.ske.stick;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Assets {
    public static BitmapFont font = new BitmapFont(Gdx.files.internal("font.fnt"));
    public static BitmapFont fontbig = new BitmapFont(Gdx.files.internal("fontbig.fnt"));

    public static Sound hurt = Gdx.audio.newSound(Gdx.files.internal("audio/hurt.wav"));
    public static Sound crackleup = Gdx.audio.newSound(Gdx.files.internal("audio/crackleup.wav"));
    public static Sound ding = Gdx.audio.newSound(Gdx.files.internal("audio/ding.wav"));
    public static Sound boop = Gdx.audio.newSound(Gdx.files.internal("audio/boop.wav"));
    public static Sound uppy = Gdx.audio.newSound(Gdx.files.internal("audio/uppy.wav"));
    public static Sound downy = Gdx.audio.newSound(Gdx.files.internal("audio/downy.wav"));
    public static Sound hit = Gdx.audio.newSound(Gdx.files.internal("audio/hit.wav"));
    public static Sound xplode = Gdx.audio.newSound(Gdx.files.internal("audio/xplode.wav"));
    public static Sound jump = Gdx.audio.newSound(Gdx.files.internal("audio/jump.wav"));
    public static Sound trow = Gdx.audio.newSound(Gdx.files.internal("audio/throw.wav"));
    public static Sound shut = Gdx.audio.newSound(Gdx.files.internal("audio/shut.wav"));
}
