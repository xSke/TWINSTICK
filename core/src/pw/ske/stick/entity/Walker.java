package pw.ske.stick.entity;

public class Walker extends LivingEntity {
    private float direction = 1;
    private float timer;

    public Walker() {
        size.getTarget().set(1f, 1f);
        rectCollision(1f, 1f);
        damageOnTouch = 1;
        health = 5;
        getBody().setFixedRotation(true);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        timer -= delta;
        if (timer < 0) {
            timer = 1.3f;
            direction = -direction;
        }

        shear.target = 0.2f * direction;
        getBody().setLinearVelocity(6 * direction, getBody().getLinearVelocity().y);
    }
}
