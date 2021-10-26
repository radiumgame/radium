package Engine.ParticleSystem;

import Editor.Console;
import Engine.Color;
import Engine.Math.Transform;
import Engine.Math.Vector.Vector3;
import Engine.Time;

public class Particle {

    public Transform transform;
    public Color color;

    private float lifetime = 5;
    private float timeToLive;
    private ParticleBatch batch;

    public Particle(Transform transform, ParticleBatch batch, float lifespan, Color color) {
        this.transform = transform;
        this.batch = batch;
        this.lifetime = lifespan;
        this.color = color;

        timeToLive = this.lifetime;
    }

    public void Update() {
        Vector3 up = Vector3.Multiply(transform.Up(), new Vector3(0, 1, 0));
        transform.position = Vector3.Add(transform.position, Vector3.Multiply(up, new Vector3(Time.deltaTime, Time.deltaTime, Time.deltaTime)));

        timeToLive -= Time.deltaTime;
        if (timeToLive <= 0) {
            Die();
        }
    }

    private void Die() {
        batch.particles.remove(this);
    }

}
