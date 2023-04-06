package Radium.Engine.Components.Particles;

import Radium.Editor.Console;
import Radium.Engine.Color.Color;
import Radium.Engine.Color.ColorMode;
import Radium.Engine.Color.Gradient;
import Radium.Engine.Component;
import Radium.Engine.Graphics.RenderQueue;
import Radium.Engine.Math.Random;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.ParticleSystem.*;
import Radium.Engine.Time;
import Radium.Editor.Annotations.*;
import Radium.Editor.Debug.Gizmo.ComponentGizmo;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.PerformanceImpact;
import Radium.Editor.EditorGUI;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

/**
 * Creates and renders particles
 */
public class ParticleSystem extends Component {

    @ExecuteGUI("TRANSFORM")
    @HideInEditor
    public Vector2 particleSize = new Vector2(0.25f, 0.25f);
    @HideInEditor
    public boolean randomSize;
    @HideInEditor
    public Vector2 particleSizeLimits = new Vector2(0.25f, 0.5f);

    @HideInEditor
    public float particleRotation;
    @HideInEditor
    public boolean randomRotation;
    @HideInEditor
    public Vector2 rotationLimits = new Vector2(0, 360);

    @HideInEditor
    public Vector3 positionOffset = Vector3.Zero();
    @HideInEditor
    public boolean randomPositionOffset;
    @HideInEditor
    public Vector3 positionOffsetLimits1 = new Vector3(-0.25f, -0.25f, -0.25f);
    @HideInEditor
    public Vector3 positionOffsetLimits2 = new Vector3(0.25f, 0.25f, 0.25f);

    @ExecuteGUI("LIFETIME")
    @HideInEditor
    public float particleLifetime = 5.0f;
    @HideInEditor
    public boolean randomLifetime;
    @HideInEditor
    public Vector2 lifetimeLimits = new Vector2(5.0f, 10.0f);


    @ExecuteGUI("EMISSION")
    @HideInEditor
    public float emissionRate = 10.0f;
    private float emissionTime;
    @HideInEditor
    public EmissionShape emissionShape = EmissionShape.Sphere;
    @HideInEditor
    public float coneRadius = 0.5f;
    @HideInEditor
    public float coneAngle = 45.0f;

    @ExecuteGUI("SPEED")
    @HideInEditor
    public Vector3 particleSpeed = new Vector3(1, 1, 1);
    @HideInEditor
    public boolean randomSpeed;
    @HideInEditor
    public Vector3 speedLimits1 = new Vector3(0.5f, 0.5f, 0.5f);
    @HideInEditor
    public Vector3 speedLimits2 = new Vector3(1, 1, 1);

    @Divider
    @Header("Graphics")
    public Texture texture = new Texture("EngineAssets/Textures/Particles/particle.jpg", false);
    public BlendType blendType = BlendType.Alpha;
    public boolean transparent = false;
    @ExecuteGUI("TEXTURE_ATLAS")
    @HideInEditor
    public Vector2 atlasSize = Vector2.One();

    public ColorMode colorMode = ColorMode.Color;
    @ExecuteGUI("COLOR_CHOOSE")
    @HideInEditor
    public Color color = new Color(1.0f, 1.0f, 1.0f);
    @HideInEditor
    private Gradient grad = new Gradient();

    @Divider
    @Header("General")
    public Vector3 initialVelocity = new Vector3(0, 3, 0);
    public Vector3 gravity = new Vector3(0, -3, 0);
    public boolean playOnAwake = true;
    @Tooltip("WARNING: Can destroy particles before end of lifetime")
    public int maxParticles = 1000;

    private transient ParticleRenderer renderer;
    private transient ParticleBatch batch;

    private transient ComponentGizmo gizmo;

    private boolean playing = false;

    /**
     * Create an empty particle system
     */
    public ParticleSystem() {
        name = "Particle System";
        description = "Generates particles";
        impact = PerformanceImpact.Medium;
        icon = new Texture("EngineAssets/Editor/Icons/particlesystem.png", true).GetTextureID();
        submenu = "Particles";
    }
    
    public void Start() {
        if (playOnAwake) {
            Play();
        }
    }

    private float particleTimer = 0.0f;
    public void Update() {
        if (playing) {
            particleTimer += Time.deltaTime;
            while (particleTimer > emissionTime) {
                particleTimer -= emissionTime;
                CreateParticle();
            }
        }

        batch.Update();
        if (transparent) {
            RenderQueue.transparentParticles.add(renderer);
        } else {
            RenderQueue.opaqueParticles.add(renderer);
        }
    }

    private void CreateParticle() {
        if (batch.particles.size() >= maxParticles) {
            return;
        }

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

        if (randomLifetime) {
            p.lifetime = Random.RandomFloat(lifetimeLimits.x, lifetimeLimits.y);
        } else {
            p.lifetime = particleLifetime;
        }

        if (randomSpeed) {
            p.speed = new Vector3(Random.RandomFloat(speedLimits1.x, speedLimits2.x),
                    Random.RandomFloat(speedLimits1.y, speedLimits2.y),
                    Random.RandomFloat(speedLimits1.z, speedLimits2.z));
        } else {
            p.speed = particleSpeed;
        }

        p.velocity = initialVelocity;

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
        playing = false;
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
        if (name.equals("LIFETIME")) {
            if (ImGui.collapsingHeader("Lifetime##COLLAPSING_HEADER", ImGuiTreeNodeFlags.SpanAvailWidth)) {
                ImGui.indent();

                randomLifetime = EditorGUI.Checkbox("Random Lifetime", randomLifetime);
                if (!randomLifetime) {
                    particleLifetime = EditorGUI.DragFloat("Lifetime", particleLifetime);
                } else {
                    lifetimeLimits = EditorGUI.DragVector2("Lifetime Range", lifetimeLimits);
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
        if (name.equals("EMISSION")) {
            if (ImGui.collapsingHeader("Emission", ImGuiTreeNodeFlags.SpanAvailWidth)) {
                ImGui.indent();

                emissionRate = EditorGUI.DragFloat("Emission Rate", emissionRate);
                emissionShape = (EmissionShape) EditorGUI.EnumSelect("Emission Shape", emissionShape.ordinal(), EmissionShape.class);
                emissionTime = 1.0f / emissionRate;

                if (emissionShape == EmissionShape.Cone) {
                    coneRadius = EditorGUI.DragFloat("Cone Radius", coneRadius);
                    coneAngle = EditorGUI.DragFloat("Cone Angle", coneAngle);
                }

                ImGui.unindent();
            }
        }
        if (name.equals("SPEED")) {
            if (ImGui.collapsingHeader("Speed", ImGuiTreeNodeFlags.SpanAvailWidth)) {
                ImGui.indent();

                randomSpeed = EditorGUI.Checkbox("Random Speed", randomSpeed);
                if (!randomSpeed) {
                    particleSpeed = EditorGUI.DragVector3("Speed", particleSpeed);
                } else {
                    speedLimits1 = EditorGUI.DragVector3("Speed Range Min", speedLimits1);
                    speedLimits2 = EditorGUI.DragVector3("Speed Range Max", speedLimits2);
                }

                ImGui.unindent();
            }
        }
        if (name.equals("TEXTURE_ATLAS")) {
            if (ImGui.collapsingHeader("Texture Atlas", ImGuiTreeNodeFlags.SpanAvailWidth)) {
                ImGui.indent();

                atlasSize = EditorGUI.DragVectorInt2("Atlas Size", atlasSize);

                ImGui.unindent();
            }
        }
    }

    public void OnAdd() {
        gizmo = new ComponentGizmo(gameObject, new Texture("EngineAssets/Editor/Icons/particlesystem.png", true));
        batch = new ParticleBatch(this, new Texture("EngineAssets/Textures/Particles/particle.jpg", false));
        batch.obj = gameObject;
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

    public void Play() {
        playing = true;
    }

    public void StopPlay() {
        playing = false;
    }

    public void CheckVBOData() {
        if (maxParticles <= 0) maxParticles = 1;
        if (emissionRate <= 0) emissionRate = 1;
        if (emissionRate > maxParticles) emissionRate = maxParticles;
    }
    
    public void UpdateVariable(String update) {
        CheckVBOData();

        if (DidFieldChange(update, "texture")) {
            batch.texture = texture;
        } else if (DidFieldChange(update, "maxParticles")) {
            batch.ResizeBuffer();
        }
        emissionTime = 1.0f / emissionRate;

        if (emissionRate > maxParticles) {
            emissionRate = maxParticles;
            Console.Error("Cannot emit more than the max particles");
        }
    }

}
