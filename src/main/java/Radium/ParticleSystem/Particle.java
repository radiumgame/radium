package Radium.ParticleSystem;

import Radium.Color;
import Radium.Math.QuaternionUtility;
import Radium.Math.Transform;
import Radium.Math.Vector.Vector3;
import Radium.Time;
import Radium.Variables;

/**
 * Contains particle information such as position and color
 */
public class Particle {

    /**
     * Transform of particle
     */
    public Transform transform;
    /**
     * Color of particle
     */
    public Color color;
    private boolean applyGravity;
    private float rotation;

    private float lifetime;
    private float timeToLive;
    private ParticleBatch batch;

    private Vector3 velocity = new Vector3(0, 1.5f, 0);
    private float terminalVelocity = 15f;

    /**
     * Create particle with preset
     * @param transform Transform of particle
     * @param batch Batch to add particle to
     * @param lifespan Lifespan of particle after creation
     * @param color Color of particle
     * @param applyGravity If true, gravity is applied on the particle
     * @param rotation Rotation of particle
     */
    public Particle(Transform transform, ParticleBatch batch, float lifespan, Color color, boolean applyGravity, float rotation) {
        this.transform = transform;
        this.batch = batch;
        this.lifetime = lifespan;
        this.color = color;
        this.applyGravity = applyGravity;
        this.rotation = rotation;

        timeToLive = this.lifetime;
    }

    /**
     * Updates the particles transform
     */
    public void Update() {
        transform.position = Vector3.Add(transform.position, Vector3.Multiply(velocity, new Vector3(Time.deltaTime, Time.deltaTime, Time.deltaTime)));
        transform.rotation = Vector3.Subtract(QuaternionUtility.LookAt(transform, Variables.DefaultCamera.gameObject.transform.position), new Vector3(90, 0, -rotation));

        if (applyGravity) {
            if (velocity.y > -terminalVelocity) {
                velocity.y -= Time.deltaTime;
            }
        }

        timeToLive -= Time.deltaTime;
        if (timeToLive <= 0) {
            Die();
        }
    }

    private void Die() {
        batch.particles.remove(this);
    }

}
