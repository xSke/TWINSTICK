package pw.ske.stick.util;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import pw.ske.stick.entity.Entity;

public class CollisionTracker implements ContactListener {
    private ObjectMap<Fixture, Array<Fixture>> collisions = new ObjectMap<Fixture, Array<Fixture>>();

    @Override
    public void beginContact(Contact contact) {
        if (!collisions.containsKey(contact.getFixtureA())) collisions.put(contact.getFixtureA(), new Array<Fixture>());
        if (!collisions.containsKey(contact.getFixtureB())) collisions.put(contact.getFixtureB(), new Array<Fixture>());
        collisions.get(contact.getFixtureA()).add(contact.getFixtureB());
        collisions.get(contact.getFixtureB()).add(contact.getFixtureA());

        if (contact.getFixtureA().getBody().getUserData() instanceof Entity) {
            ((Entity) contact.getFixtureA().getBody().getUserData()).collide((Entity) contact.getFixtureB().getBody().getUserData());
        }

        if (contact.getFixtureB().getBody().getUserData() instanceof Entity) {
            ((Entity) contact.getFixtureB().getBody().getUserData()).collide((Entity) contact.getFixtureA().getBody().getUserData());
        }
    }

    @Override
    public void endContact(Contact contact) {
        collisions.get(contact.getFixtureA()).removeValue(contact.getFixtureB(), true);
        collisions.get(contact.getFixtureB()).removeValue(contact.getFixtureA(), true);
        if (collisions.get(contact.getFixtureA()).size == 0) collisions.remove(contact.getFixtureA());
        if (collisions.get(contact.getFixtureB()).size == 0) collisions.remove(contact.getFixtureB());
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public ObjectMap<Fixture, Array<Fixture>> getCollisions() {
        return collisions;
    }
}
