package Radium.ParticleSystem;

import Radium.Math.Mathf;
import Radium.Math.Random;
import Radium.Math.Vector.Vector3;
import Radium.Time;

public class EmissionShapeGenerator {

    protected EmissionShapeGenerator() {}

    public static Vector3 Sphere() {
        float x = Random.RandomFloat(0, 1);
        float y = Random.RandomFloat(0, 1);
        float z = Random.RandomFloat(0, 1);

        return new Vector3(x * 2 - 1, y * 2 - 1, z * 2 - 1);
    }

    public static Vector3 Cone(float radius, float angle) {
        float theta = Random.RandomFloat(0, angle);
        float phi = Random.RandomFloat(0, Mathf.PI * 2);

        return new Vector3(Mathf.Cosine(phi) * Mathf.Sine(theta), Mathf.Sine(phi) * Mathf.Cosine(theta), Mathf.Cosine(theta));
    }

    public static void ApplyRotation(Particle particle) {
        Vector3 vel = particle.velocity;
        Vector3 rotation = particle.system.gameObject.transform.WorldRotation();

        // X Rotation
        vel.y = vel.y * Mathf.Cosine(rotation.x) - vel.z * Mathf.Sine(rotation.x);
        vel.z = vel.y * Mathf.Sine(rotation.x) + vel.z * Mathf.Cosine(rotation.x);

        // Y Rotation
        vel.x = vel.x * Mathf.Cosine(rotation.y) + vel.z * Mathf.Sine(rotation.y);
        vel.z = -vel.x * Mathf.Sine(rotation.y) + vel.z * Mathf.Cosine(rotation.y);

        // Z Rotation
        vel.x = vel.x * Mathf.Cosine(rotation.z) - vel.y * Mathf.Sine(rotation.z);
        vel.y = vel.x * Mathf.Sine(rotation.z) + vel.y * Mathf.Cosine(rotation.z);

        particle.velocity = vel;
    }

}
