package Radium.Engine.ParticleSystem;

import Radium.Engine.Color.Color;
import Radium.Engine.Components.Particles.ParticleSystem;
import Radium.Engine.Math.Mathf;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Time;
import Radium.Engine.Variables;
import org.joml.Matrix4f;

public class Particle {

    public Vector3 position = new Vector3(0, 0, 0);
    public float rotation = 0;
    public Vector2 size = new Vector2(0.25f, 0.25f);
    public Vector3 velocity = new Vector3(0, 0, 0);
    public Color color;

    public Vector2 textureOffset1 = new Vector2(0, 0);
    public Vector2 textureOffset2 = new Vector2(0, 0);
    public float blend;

    public float distance;

    public float lifetime = 5.0f;
    private float life = 0.0f;

    public Vector3 speed = Vector3.One();

    private transient ParticleBatch batch;
    public transient ParticleSystem system;

    private transient Matrix4f transform = new Matrix4f();

    public void Update() {
        life += Time.deltaTime;
        if (life > lifetime) {
            batch.particles.remove(this);
        }

        distance = Mathf.Square(Vector3.Length(Vector3.Subtract(Variables.DefaultCamera.gameObject.transform.WorldPosition(), position)));

        UpdateTextureCoordinates();

        Vector3 delta = new Vector3(Time.deltaTime, Time.deltaTime, Time.deltaTime);
        velocity = Vector3.Add(velocity, Vector3.Multiply(system.gravity, delta));
        position = Vector3.Add(position, Vector3.Multiply(Vector3.Multiply(velocity, delta), speed));
    }

    public Matrix4f CalculateTransform(Matrix4f view) {
        transform.identity();
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

    public float CalculateDistance() {
        Vector3 cam = Variables.DefaultCamera.gameObject.transform.WorldPosition();
        return Vector3.Distance(cam, position);
    }

    public void SetBatch(ParticleBatch batch) {
        this.batch = batch;

        position = batch.obj.transform.WorldPosition();
    }

    public void SetSystem(ParticleSystem system) {
        this.system = system;
    }

    public void UpdateTextureCoordinates() {
        float lifeFactor = life / lifetime;
        int stageCount = (int)system.atlasSize.x * (int)system.atlasSize.y;
        float atlasProgression = lifeFactor * stageCount;
        int index1 = Mathf.Floor(atlasProgression);
        int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
        blend = atlasProgression % 1.0f;

        SetTextureOffset(textureOffset1, index1);
        SetTextureOffset(textureOffset2, index2);
    }

    public void SetTextureOffset(Vector2 offset, int index) {
        int column = index % (int)system.atlasSize.x;
        int row = index / (int)system.atlasSize.x;
        offset.x = (float)column / system.atlasSize.x;
        offset.y = (float)row / system.atlasSize.y;
    }

    public void CalculatePath(EmissionShape shape) {
        if (shape == EmissionShape.Sphere) {
            velocity = EmissionShapeGenerator.Sphere();
        } else if (shape == EmissionShape.Cone) {
            velocity = EmissionShapeGenerator.Cone(system.coneRadius, system.coneAngle);
        }

        //EmissionShapeGenerator.ApplyRotation(this);
    }

    public void SetPositionOffset(Vector3 offset) {
        position = Vector3.Add(position, offset);
    }

}
