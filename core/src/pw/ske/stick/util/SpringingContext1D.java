package pw.ske.stick.util;

import com.badlogic.gdx.math.MathUtils;

public class SpringingContext1D {
    public float frequency;
    public float damping;
    public float value;
    public float velocity;
    public float target;

    public SpringingContext1D(float frequency, float damping) {
        this.frequency = frequency;
        this.damping = damping;
    }

    public void update(float deltaTime) {
        float angularFrequency = frequency;
        angularFrequency *= MathUtils.PI2;

        float f = 1.0f + 2.0f * deltaTime * damping * angularFrequency;
        float oo = angularFrequency * angularFrequency;
        float hoo = deltaTime * oo;
        float hhoo = deltaTime * hoo;
        float detInv = 1.0f / (f + hhoo);
        float detX = f * value + deltaTime * velocity + hhoo * target;
        float detV = velocity + hoo * (target - value);
        value = detX * detInv;
        velocity = detV * detInv;
    }
}
