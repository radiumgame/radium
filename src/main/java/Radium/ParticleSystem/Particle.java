package Radium.ParticleSystem;

import Radium.Color.Color;
import Radium.Components.Particles.ParticleSystem;
import Radium.Math.Mathf;
import Radium.Math.QuaternionUtility;
import Radium.Math.Random;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.Time;
import Radium.Variables;
import RadiumEditor.Console;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Particle {

    public Vector3 position = new Vector3(0, 0, 0);
    public float rotation = 0;
    public Vector2 size = new Vector2(0.25f, 0.25f);
    public Vector3 velocity = new Vector3(0, 0, 0);
    public Color color;

    public float lifetime = 5.0f;
    private float life = 0.0f;

    private transient ParticleBatch batch;
    public transient ParticleSystem system;

    public void Update() {
        life += Time.deltaTime;
        if (life > lifetime) {
            batch.particles.remove(this);
        }

        Vector3 delta = new Vector3(Time.deltaTime, Time.deltaTime, Time.deltaTime);
        velocity = Vector3.Add(velocity, Vector3.Multiply(system.gravity, delta));
        position = Vector3.Add(position, Vector3.Multiply(Vector3.Multiply(velocity, delta), system.particleSpeed));
    }

    public Matrix4f CalculateTransform(Matrix4f view) {
        Matrix4f transform = new Matrix4f().identity();
        transform.translate(position.x, position.y, position.z);
        transform.m00(view.m00());
        transform.m01(view.m10());
        transform.m02(view.m20());
        transform.m10(view.m01());
        transform.m11(view.m11());
        transform.m12(view.m21());
        transform.m20(view.m02());
        transform.m21(view.m12());
        transform.m22(view.m22());
        transform.rotate(Mathf.Radians(rotation), 0, 0, 1);
        transform.scale(size.x, size.y, 1.0f);

        return transform;
    }

    public void SetBatch(ParticleBatch batch) {
        this.batch = batch;

        position = batch.obj.transform.WorldPosition();
    }

    public void SetSystem(ParticleSystem system) {
        this.system = system;
    }

    public void CalculatePath(EmissionShape shape) {
        if (shape == EmissionShape.Sphere) {
            float x = Random.RandomFloat(0, 1);
            float y = Random.RandomFloat(0, 1);
            float z = Random.RandomFloat(0, 1);
            velocity = new Vector3(x * 2 - 1, y * 2 - 1, z * 2 - 1);
        }
    }

}
