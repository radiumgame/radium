package Radium.Components.Particles;

import Radium.Color;
import Radium.Component;
import RadiumEditor.Debug.Gizmo.ComponentGizmo;
import Radium.Graphics.Mesh;
import Radium.Graphics.Texture;
import Radium.Math.Random;
import Radium.Math.Transform;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.ParticleSystem.Particle;
import Radium.ParticleSystem.ParticleBatch;
import Radium.ParticleSystem.ParticleRenderer;
import Radium.PerformanceImpact;
import Radium.Time;

public class ParticleSystem extends Component {

    public Vector2 particleScale = new Vector2(0.1f, 0.1f);
    public Color color = new Color(1f, 1f, 1f);
    public boolean randomColors = false;
    public boolean applyGravity = true;
    public float emissionRate = 10;
    public float particleLifespan = 5f;
    public float particleSpawnRange = 0.5f;
    public float startRotation = 0;
    public boolean randomRotation;

    private transient float emissionRateTime = 0;
    private transient float spawnTime = 0;
    private transient ParticleRenderer renderer;
    private transient ParticleBatch batch;

    private transient ComponentGizmo gizmo;

    public ParticleSystem() {
        description = "Generates particles";
        impact = PerformanceImpact.Low;
        icon = new Texture("EngineAssets/Editor/Icons/particlesystem.png").textureID;
        submenu = "Particles";
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        spawnTime += Time.deltaTime;
        if (spawnTime >= emissionRateTime) {
            Transform particleTransform = new Transform();
            particleTransform.position = new Vector3(gameObject.transform.position.x + Random.RandomFloat(-particleSpawnRange, particleSpawnRange), gameObject.transform.position.y, gameObject.transform.position.z + Random.RandomFloat(-particleSpawnRange, particleSpawnRange));
            particleTransform.rotation = new Vector3(0, 90, 90);
            particleTransform.scale = Vector3.One;

            float rotation = randomRotation ? Random.RandomFloat(0, 360) : startRotation;

            Particle particle = new Particle(particleTransform, batch, particleLifespan, Color.Green(), applyGravity, rotation);
            particle.color = color;
            if (randomColors) {
                Color col = new Color(Random.RandomFloat(0f, 1f), Random.RandomFloat(0f, 1f), Random.RandomFloat(0f, 1f));
                particle.color = col;
            }

            batch.particles.add(particle);
            spawnTime = 0;
        }

        renderer.Render();
    }

    @Override
    public void Stop() {
        renderer.batch.particles.clear();
    }

    @Override
    public void OnAdd() {
        ParticleBatch particleBatch = new ParticleBatch(Mesh.Plane(particleScale.x, particleScale.y));
        renderer = new ParticleRenderer(particleBatch);
        batch = renderer.batch;

        emissionRateTime = 1 / emissionRate;

        gizmo = new ComponentGizmo(gameObject, new Texture("EngineAssets/Editor/Icons/particlesystem.png"));
    }

    @Override
    public void OnRemove() {
        gizmo.Destroy();
    }

    @Override
    public void UpdateVariable() {
        batch.Destroy();

        ParticleBatch particleBatch = new ParticleBatch(Mesh.Plane(particleScale.x, particleScale.y));
        renderer = new ParticleRenderer(particleBatch);
        batch = renderer.batch;

        emissionRateTime = 1 / emissionRate;
    }

    @Override
    public void GUIRender() {

    }

}
