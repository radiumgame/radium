package Radium.ParticleSystem;

import Radium.Components.Particles.ParticleSystem;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.Time;
import org.joml.Matrix4f;
import org.python.bouncycastle.pqc.math.linearalgebra.Matrix;

public class Particle {

    public Vector3 position = new Vector3(0, 0, 0);
    public float rotation = 0;
    public Vector2 size = new Vector2(0.25f, 0.25f);
    public Vector3 velocity = new Vector3(0, 0, 0);

    public float lifetime = 5.0f;
    private float life = 0.0f;

    private ParticleBatch batch;
    private ParticleSystem system;

    public void Update() {
        life += Time.deltaTime;
        if (life > lifetime) {
            batch.particles.remove(this);
        }

        Vector3 delta = new Vector3(Time.deltaTime, Time.deltaTime, Time.deltaTime);
        velocity = Vector3.Add(velocity, Vector3.Multiply(system.gravity, delta));
        position = Vector3.Add(position, Vector3.Multiply(velocity, delta));
    }

    public Matrix4f CalculateTransform() {
        Matrix4f transform = new Matrix4f();
        transform.translate(position.x, position.y, position.z);
        transform.scale(size.x, 1.0f, size.y);
        return transform;
    }

    public void SetBatch(ParticleBatch batch) {
        this.batch = batch;

        position = batch.obj.transform.WorldPosition();
    }

    public void SetSystem(ParticleSystem system) {
        this.system = system;
    }

}
