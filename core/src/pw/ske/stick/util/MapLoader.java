package pw.ske.stick.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import pw.ske.stick.GameScreen;
import pw.ske.stick.entity.*;

public class MapLoader {
    public Vector2 loadMap(FileHandle mapFile) {
        TiledMap map = new TmxMapLoader().load(mapFile.path());

        float w = 0;
        float h = 0;
        float tilew = 0;
        float tileh = 0;
        for (MapLayer layer : map.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tl = (TiledMapTileLayer) layer;
                tilew = tl.getTileWidth();
                tileh = tl.getTileHeight();
                w = tl.getWidth();
                h = tl.getHeight();
                for (int x = 0; x < tl.getWidth(); x++) {
                    for (int y = 0; y < tl.getHeight(); y++) {
                        if (tl.getCell(x, y) != null) {
                            BasicTile tile = new BasicTile(new Rectangle(x, y, 1, 1));
                            GameScreen.i.getEntities().add(tile);
                        }
                    }
                }
            }
        }

        for (MapLayer layer : map.getLayers()) {
            for (MapObject obj : layer.getObjects()) {
                MapProperties props = obj.getProperties();
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                r.x /= tilew;
                r.y /= tileh;
                r.width /= tilew;
                r.height /= tileh;


                Vector2 center = r.getCenter(new Vector2());

                String type = (String) props.get("type");
                if (type != null) {
                    if (type.equals("player")) {
                        spawnPlayer(center);
                    } else if (type.equals("walker")) {
                        spawnWalker(center);
                    } else if (type.equals("tile")) {
                        spawnTile(r);
                    } else if (type.equals("chaser")) {
                        spawnChaser(center);
                    } else if (type.equals("basicboss")) {
                        spawnBasicBoss(center);
                    } else if (type.equals("bosstrigger")) {
                        spawnBossTrigger(r);
                    } else if (type.equals("bossdoor")) {
                        spawnBossDoor(r);
                    } else if (type.equals("loadmap")) {
                        spawnLoadMap(r, (String) props.get("map"));
                    } else if (type.equals("otherboss")) {
                        spawnOtherBoss(center);
                    } else if (type.equals("runboss")) {
                        spawnRunBoss(center);
                    } else if (type.equals("godboss")) {
                        spawnGodBoss(center);
                    } else if (type.equals("text")) {
                        spawnText(r.x, r.y, (String) props.get("text"), props.get("bossdeath") != null);
                    } else if (type.equals("lastboss")) {
                        spawnLastBoss(center);
                    }
                }
            }
        }

        return new Vector2(w, h);
    }

    private void spawnLastBoss(Vector2 center) {
        LastBoss boss = new LastBoss();
        boss.getBody().setTransform(center, 0);
        GameScreen.i.setBoss(boss);
        GameScreen.i.getEntities().add(boss);
    }

    private void spawnText(float x, float y, String text, boolean bd) {
        Text t = new Text(text, bd);
        t.getBody().setTransform(x, y, 0);
        GameScreen.i.getEntities().add(t);
    }

    private void spawnGodBoss(Vector2 center) {
        GodBoss boss = new GodBoss();
        boss.getBody().setTransform(center, 0);
        GameScreen.i.setBoss(boss);
        GameScreen.i.getEntities().add(boss);
    }

    private void spawnRunBoss(Vector2 center) {
        RunBoss boss = new RunBoss();
        boss.getBody().setTransform(center, 0);
        GameScreen.i.setBoss(boss);
        GameScreen.i.getEntities().add(boss);
    }

    private void spawnOtherBoss(Vector2 center) {
        OtherBoss boss = new OtherBoss();
        boss.getBody().setTransform(center, 0);
        GameScreen.i.setBoss(boss);
        GameScreen.i.getEntities().add(boss);
    }

    private void spawnLoadMap(Rectangle r, String map) {
        LoadMapTrigger br = new LoadMapTrigger(r, map);
        GameScreen.i.getEntities().add(br);
    }

    private void spawnBossDoor(Rectangle r) {
        BossDoor br = new BossDoor(r);
        GameScreen.i.getEntities().add(br);
    }

    private void spawnBossTrigger(Rectangle r) {
        BossTrigger br = new BossTrigger(r);
        GameScreen.i.getEntities().add(br);
    }

    private void spawnBasicBoss(Vector2 center) {
        BasicBoss boss = new BasicBoss();
        boss.getBody().setTransform(center, 0);
        GameScreen.i.setBoss(boss);
        GameScreen.i.getEntities().add(boss);
    }

    private void spawnChaser(Vector2 center) {
        Chaser chaser = new Chaser();
        chaser.getBody().setTransform(center, 0);
        GameScreen.i.getEntities().add(chaser);
    }

    private void spawnTile(Rectangle r) {
        BasicTile t = new BasicTile(r);
        GameScreen.i.getEntities().add(t);
    }

    private void spawnWalker(Vector2 center) {
        Walker walker = new Walker();
        walker.getBody().setTransform(center, 0);
        GameScreen.i.getEntities().add(walker);
    }

    private void spawnPlayer(Vector2 center) {
        Player player = new Player();
        player.getBody().setTransform(center, 0);
        GameScreen.i.getEntities().add(player);
        GameScreen.i.setPlayer(player);
    }
}
