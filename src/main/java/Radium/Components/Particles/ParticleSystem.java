package Radium.Components.Particles;

import Radium.Color.Color;
import Radium.Component;
import Radium.Graphics.RenderQueue;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.ParticleSystem.Particle;
import Radium.ParticleSystem.ParticleBatch;
import Radium.ParticleSystem.ParticleRenderer;
import Radium.Time;
import RadiumEditor.Annotations.Divider;
import RadiumEditor.Annotations.Header;
import RadiumEditor.Debug.Gizmo.ComponentGizmo;
import Radium.Graphics.Texture;
import Radium.PerformanceImpact;

/**
 * Creates and renders particles
 */
public class ParticleSystem extends Component {

    @Header("Settings")
    public Vector2 particleSize = new Vector2(0.25f, 0.25f);
    public float emissionRate = 10.0f;
    private float emissionTime;
    public Color color = new Color(1.0f, 1.0f, 1.0f, 1.0f);

    @Divider
    @Header("Graphics")
    public Texture texture = new Texture("EngineAssets/Textures/Misc/blank.jpg");
    public boolean transparent = false;

    @Divider
    @Header("Physics")
    public Vector3 initialVelocity = new Vector3(0, 3, 0);
    public Vector3 gravity = new Vector3(0, -3, 0);

    private ParticleRenderer renderer;
    private ParticleBatch batch;

    private transient ComponentGizmo gizmo;

    /**
     * Create an empty particle system
     */
    public ParticleSystem() {
        name = "Particle System";
        description = "Generates particles";
        impact = PerformanceImpact.Low;
        icon = new Texture("EngineAssets/Editor/Icons/particlesystem.png").textureID;
        submenu = "Particles";
    }
    
    public void Start() {

    }

    private float particleTimer = 0.0f;
    public void Update() {
        particleTimer += Time.deltaTime;
        if (particleTimer > emissionTime) {
            particleTimer = 0.0f;

            Particle p = new Particle();
            p.size = particleSize;
            p.velocity = initialVelocity;
            p.color = color;
            p.SetBatch(batch);
            p.SetSystem(this);
            batch.particles.add(p);
        }

        batch.Update();
        if (transparent) {
            RenderQueue.transparentParticles.add(renderer);
        } else {
            RenderQueue.opaqueParticles.add(renderer);
        }
    }

    public void Stop() {
        batch.particles.clear();
    }

    
    public void OnAdd() {
        gizmo = new ComponentGizmo(gameObject, new Texture("EngineAssets/Editor/Icons/particlesystem.png"));
        batch = new ParticleBatch(new Texture("EngineAssets/Textures/Misc/blank.jpg"));
        batch.obj = gameObject;
        batch.texture = texture;
        renderer = new ParticleRenderer(batch);
        emissionTime = 1.0f / emissionRate;
    }

    
    public void OnRemove() {
        gizmo.Destroy();
    }

    
    public void UpdateVariable(String update) {
        if (DidFieldChange(update, "texture")) {
            batch.texture = texture;
        }

        emissionTime = 1.0f / emissionRate;
    }

}
