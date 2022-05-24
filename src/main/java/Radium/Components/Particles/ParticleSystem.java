package Radium.Components.Particles;

import Radium.Color.Color;
import Radium.Color.ColorMode;
import Radium.Color.Gradient;
import Radium.Component;
import Radium.Graphics.RenderQueue;
import Radium.Math.Random;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.ParticleSystem.*;
import Radium.Time;
import RadiumEditor.Annotations.Divider;
import RadiumEditor.Annotations.ExecuteGUI;
import RadiumEditor.Annotations.Header;
import RadiumEditor.Annotations.HideInEditor;
import RadiumEditor.Console;
import RadiumEditor.Debug.Gizmo.ComponentGizmo;
import Radium.Graphics.Texture;
import Radium.PerformanceImpact;
import RadiumEditor.EditorGUI;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

/**
 * Creates and renders particles
 */
public class ParticleSystem extends Component {

    @Header("Transform")
    @ExecuteGUI("TRANSFORM")
    private Vector2 particleSize = new Vector2(0.25f, 0.25f);
    private boolean randomSize;
    private Vector2 particleSizeLimits = new Vector2(0.25f, 0.5f);

    private float particleRotation;
    private boolean randomRotation;
    private Vector2 rotationLimits = new Vector2(0, 360);

    private Vector3 positionOffset = Vector3.Zero();
    private boolean randomPositionOffset;
    private Vector3 positionOffsetLimits1 = new Vector3(-0.25f, -0.25f, -0.25f);
    private Vector3 positionOffsetLimits2 = new Vector3(0.25f, 0.25f, 0.25f);

    public float particleLifetime = 5.0f;

    public float emissionRate = 10.0f;
    private float emissionTime;

    public EmissionShape emissionShape = EmissionShape.Sphere;
    @HideInEditor
    public float coneRadius = 0.5f;
    @HideInEditor
    public float coneAngle = 45.0f;
    @ExecuteGUI("EMISSION_SHAPE")
    public Vector3 particleSpeed = new Vector3(1, 1, 1);

    @Divider
    @Header("Graphics")
    public Texture texture = new Texture("EngineAssets/Textures/Particle Textures/particle.jpg");
    public BlendType blendType;
    public boolean transparent = false;

    public ColorMode colorMode = ColorMode.Color;
    @ExecuteGUI("COLOR_CHOOSE")
    private Color color = new Color(1.0f, 1.0f, 1.0f);
    private Gradient grad = new Gradient();

    @Divider
    @Header("Physics")
    public Vector3 initialVelocity = new Vector3(0, 3, 0);
    public Vector3 gravity = new Vector3(0, -3, 0);

    private transient ParticleRenderer renderer;
    private transient ParticleBatch batch;

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
        while (particleTimer > emissionTime) {
            particleTimer -= emissionTime;
            CreateParticle();
        }

        batch.Update();
        if (transparent) {
            RenderQueue.transparentParticles.add(renderer);
        } else {
            RenderQueue.opaqueParticles.add(renderer);
        }
    }

    private void CreateParticle() {
        Particle p = new Particle();
        p.size = particleSize;

        if (randomRotation) { p.rotation = Random.RandomFloat(rotationLimits.x, rotationLimits.y); }
        else { p.rotation = particleRotation; }

        if (randomSize) {
            float rand = Random.RandomFloat(particleSizeLimits.x, particleSizeLimits.y);
            p.size = new Vector2(rand, rand);
        } else {
            p.size = particleSize;
        }

        p.velocity = initialVelocity;
        p.lifetime = particleLifetime;

        if (colorMode == ColorMode.Color) {
            p.color = color;
        } else {
            p.color = grad.Ease();
        }

        p.SetBatch(batch);
        p.SetSystem(this);
        p.CalculatePath(emissionShape);

        Vector3 posOffset;
        if (randomPositionOffset) {
            posOffset = new Vector3(Random.RandomFloat(positionOffsetLimits1.x, positionOffsetLimits2.x),
                    Random.RandomFloat(positionOffsetLimits1.y, positionOffsetLimits2.y),
                    Random.RandomFloat(positionOffsetLimits1.z, positionOffsetLimits2.z));
        } else {
            posOffset = positionOffset;
        }
        p.SetPositionOffset(posOffset);

        batch.particles.add(p);
    }

    public void Stop() {
        batch.particles.clear();
    }

    @Override
    public void ExecuteGUI(String name) {
        if (name.equals("TRANSFORM")) {
            if (ImGui.collapsingHeader("Transform", ImGuiTreeNodeFlags.SpanAvailWidth)) {
                ImGui.indent();

                if (ImGui.collapsingHeader("Particle Position", ImGuiTreeNodeFlags.SpanAvailWidth)) {
                    ImGui.indent();

                    randomPositionOffset = EditorGUI.Checkbox("Random Position Offset", randomPositionOffset);
                    if (!randomPositionOffset) {
                        positionOffset = EditorGUI.DragVector3("Position Offset", positionOffset);
                    } else {
                        positionOffsetLimits1 = EditorGUI.DragVector3("Position Offset Min", positionOffsetLimits1);
                        positionOffsetLimits2 = EditorGUI.DragVector3("Position Offset Max", positionOffsetLimits2);
                    }

                    ImGui.unindent();
                }
                if (ImGui.collapsingHeader("Particle Rotation", ImGuiTreeNodeFlags.SpanAvailWidth)) {
                    ImGui.indent();

                    randomRotation = EditorGUI.Checkbox("Random Rotation", randomRotation);
                    if (!randomRotation) {
                        particleRotation = EditorGUI.DragFloat("Rotation", particleRotation);
                    } else  {
                        rotationLimits = EditorGUI.DragVector2("Rotation Range", rotationLimits);
                    }

                    ImGui.unindent();
                }
                if (ImGui.collapsingHeader("Particle Size", ImGuiTreeNodeFlags.SpanAvailWidth)) {
                    ImGui.indent();

                    randomSize = EditorGUI.Checkbox("Random Size", randomSize);
                    if (!randomSize) {
                        particleSize = EditorGUI.DragVector2("Size", particleSize);
                    } else  {
                        particleSizeLimits = EditorGUI.DragVector2("Size Range", particleSizeLimits);
                    }

                    ImGui.unindent();
                }

                ImGui.unindent();
            }
        }
        if (name.equals("COLOR_CHOOSE")) {
            if (colorMode == ColorMode.Color) {
                color = EditorGUI.ColorField("Color", color);
            } else if (colorMode == ColorMode.Gradient) {
                grad = EditorGUI.GradientEditor("Color", grad);
            }
        }
        if (name.equals("EMISSION_SHAPE")) {
            if (emissionShape == EmissionShape.Cone) {
                coneRadius = EditorGUI.DragFloat("Cone Radius", coneRadius);
                coneAngle = EditorGUI.DragFloat("Cone Angle", coneAngle);
            }
        }
    }

    public void OnAdd() {
        gizmo = new ComponentGizmo(gameObject, new Texture("EngineAssets/Editor/Icons/particlesystem.png"));
        batch = new ParticleBatch(new Texture("EngineAssets/Textures/Misc/blank.jpg"));
        batch.obj = gameObject;
        batch.texture = texture;
        renderer = new ParticleRenderer(batch, this);

        if (emissionRate < 0) {
            emissionTime = Float.MAX_VALUE;
        } else {
            emissionTime = 1.0f / emissionRate;
        }
    }

    
    public void OnRemove() {
        gizmo.Destroy();
    }

    
    public void UpdateVariable(String update) {
        if (DidFieldChange(update, "texture")) {
            batch.texture = texture;
        }

        if (emissionRate < 0) {
            emissionTime = Float.MAX_VALUE;
        } else {
            emissionTime = 1.0f / emissionRate;
        }
    }

}
