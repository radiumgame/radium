package Radium.Scripting.Nodes;

import Radium.Color;
import Radium.Components.Audio.Source;
import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.MeshRenderer;
import Radium.Components.Graphics.Outline;
import Radium.Components.Misc.Rotator;
import Radium.Components.Particles.ParticleSystem;
import Radium.Components.Physics.Rigidbody;
import Radium.Components.Rendering.Camera;
import Radium.Components.Rendering.Light;
import Radium.Components.UI.Image;
import Radium.Components.UI.Text;
import Radium.Graphics.Texture;
import Radium.Math.Axis;
import Radium.Math.Mathf;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.System.FileExplorer;
import Radium.Time;
import RadiumEditor.Console;
import RadiumEditor.EditorGUI;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;

import java.util.function.Consumer;

public class NodeAction {

    protected NodeAction() {}

    public static Consumer<NodeScript> ActionFromType(ScriptingNode node) {
        switch (node.nodeType) {
            case If: {
                return (script) -> {
                    boolean IF = (boolean)node.inputs.get(1).object;
                    NodeTrigger i = (NodeTrigger)node.outputs.get(0).object;
                    NodeTrigger e = (NodeTrigger)node.outputs.get(1).object;

                    if (IF) {
                        i.locked = false;
                        e.locked = true;
                    } else {
                        i.locked = true;
                        e.locked = false;
                    }
                };
            }
            case Log: {
                return (script) -> {
                    Console.Log(node.inputs.get(1).object);
                };
            }
            case SetPosition: {
                return (script) -> {
                    node.gameObject.transform.localPosition = (Vector3)node.inputs.get(1).object;
                };
            }
            case SetRotation: {
                return (script) -> {
                    node.gameObject.transform.localRotation = (Vector3)node.inputs.get(1).object;
                };
            }
            case SetScale: {
                return (script) -> {
                    node.gameObject.transform.localScale = (Vector3)node.inputs.get(1).object;
                };
            }
            case Translate: {
                return((script) -> {
                    node.gameObject.transform.localPosition = Vector3.Add(node.gameObject.transform.localPosition, (Vector3)node.inputs.get(1).object);
                });
            }
            case Rotate: {
                return((script) -> {
                    node.gameObject.transform.localRotation = Vector3.Add(node.gameObject.transform.localRotation, (Vector3)node.inputs.get(1).object);
                });
            }
            case Scaling: {
                return((script) -> {
                    node.gameObject.transform.localScale = Vector3.Add(node.gameObject.transform.localScale, (Vector3)node.inputs.get(1).object);
                });
            }
            case DestroyMesh: {
                return ((script) -> {
                    MeshFilter filter = script.gameObject.GetComponent(MeshFilter.class);
                    if (filter == null && filter.mesh != null) return;
                    filter.mesh.Destroy();
                });
            }
            case SetMaterialTexture: {
                return ((script) -> {
                    MeshFilter filter = script.gameObject.GetComponent(MeshFilter.class);
                    if (filter == null) return;
                    filter.material.path = ((Texture)node.inputs.get(1).Value()).filepath;
                    filter.material.CreateMaterial();
                });
            }
            case SetMaterialNormalMap: {
                return ((script) -> {
                    MeshFilter filter = script.gameObject.GetComponent(MeshFilter.class);
                    if (filter == null) return;
                    filter.material.normalMapPath = ((Texture)node.inputs.get(1).Value()).filepath;
                    filter.material.CreateMaterial();
                });
            }
            case SetMaterialSpecularMap: {
                return ((script) -> {
                    MeshFilter filter = script.gameObject.GetComponent(MeshFilter.class);
                    if (filter == null) return;
                    filter.material.specularMapPath = ((Texture)node.inputs.get(1).Value()).filepath;
                    filter.material.CreateMaterial();
                });
            }
            case ToggleNormalMap: {
                return ((script) -> {
                    MeshFilter filter = script.gameObject.GetComponent(MeshFilter.class);
                    if (filter == null) return;
                    filter.material.useNormalMap = (boolean)node.inputs.get(1).Value();
                });
            }
            case ToggleSpecularMap: {
                return ((script) -> {
                    MeshFilter filter = script.gameObject.GetComponent(MeshFilter.class);
                    if (filter == null) return;
                    filter.material.useSpecularMap = (boolean)node.inputs.get(1).Value();
                });
            }
            case ToggleSpecularLighting: {
                return ((script) -> {
                    MeshFilter filter = script.gameObject.GetComponent(MeshFilter.class);
                    if (filter == null) return;
                    filter.material.specularLighting = (boolean)node.inputs.get(1).Value();
                });
            }
            case ToggleCullFaces: {
                return ((script) -> {
                    MeshRenderer renderer = script.gameObject.GetComponent(MeshRenderer.class);
                    if (renderer == null) return;
                    renderer.cullFaces = (boolean)node.inputs.get(1).Value();
                });
            }
            case SetOutlineWidth: {
                return ((script) -> {
                    Outline outline = script.gameObject.GetComponent(Outline.class);
                    if (outline == null) return;
                    outline.outlineWidth = (float)node.inputs.get(1).Value();
                });
            }
            case SetOutlineColor: {
                return ((script) -> {
                    Outline outline = script.gameObject.GetComponent(Outline.class);
                    if (outline == null) return;
                    outline.outlineColor = (Color)node.inputs.get(1).Value();
                });
            }
            case PlayParticle: {
                return((script) -> {
                    ParticleSystem particleSystem = script.gameObject.GetComponent(ParticleSystem.class);
                    if (particleSystem == null) return;
                    particleSystem.PlayParticles();
                });
            }
            case StopParticle: {
                return((script) -> {
                    ParticleSystem particleSystem = script.gameObject.GetComponent(ParticleSystem.class);
                    if (particleSystem == null) return;
                    particleSystem.StopParticles();
                });
            }
            case SetEmissionRate: {
                return((script) -> {
                    ParticleSystem particleSystem = script.gameObject.GetComponent(ParticleSystem.class);
                    if (particleSystem == null) return;
                    particleSystem.emissionRate = (float)node.inputs.get(1).Value();
                    particleSystem.UpdateBatch();
                });
            }
            case ParticleGravity: {
                return((script) -> {
                    ParticleSystem particleSystem = script.gameObject.GetComponent(ParticleSystem.class);
                    if (particleSystem == null) return;
                    particleSystem.applyGravity = (boolean)node.inputs.get(1).Value();
                });
            }
            case ParticleLifespan: {
                return((script) -> {
                    ParticleSystem particleSystem = script.gameObject.GetComponent(ParticleSystem.class);
                    if (particleSystem == null) return;
                    particleSystem.particleLifespan = (float)node.inputs.get(1).Value();
                    particleSystem.UpdateBatch();
                });
            }
            case SetRotatorAxis: {
                return((script) -> {
                    Rotator rotator = script.gameObject.GetComponent(Rotator.class);
                    if (rotator == null) return;
                    rotator.rotationAxis = (Axis)node.inputs.get(1).Value();
                });
            }
            case SetRotatorSpeed: {
                return((script) -> {
                    Rotator rotator = script.gameObject.GetComponent(Rotator.class);
                    if (rotator == null) return;
                    rotator.rotationSpeed = (float)node.inputs.get(1).Value();
                });
            }
            case RigidbodyMass: {
                return((script) -> {
                    Rigidbody body = script.gameObject.GetComponent(Rigidbody.class);
                    if (body == null) return;
                    body.mass = (float)node.inputs.get(1).Value();
                    body.UpdateBody();
                });
            }
            case RigidbodyGravity: {
                return((script) -> {
                    Rigidbody body = script.gameObject.GetComponent(Rigidbody.class);
                    if (body == null) return;
                    body.applyGravity = (boolean)node.inputs.get(1).Value();
                    body.UpdateBody();
                });
            }
            case CameraFOV: {
                return ((script) -> {
                    Camera cam = script.gameObject.GetComponent(Camera.class);
                    if (cam == null) return;
                    cam.fov = (float)node.inputs.get(1).Value();
                });
            }
            case CameraNear: {
                return ((script) -> {
                    Camera cam = script.gameObject.GetComponent(Camera.class);
                    if (cam == null) return;
                    cam.near = (float)node.inputs.get(1).Value();
                });
            }
            case CameraFar: {
                return ((script) -> {
                    Camera cam = script.gameObject.GetComponent(Camera.class);
                    if (cam == null) return;
                    cam.far = (float)node.inputs.get(1).Value();
                });
            }
            case LightColor: {
                return ((script) -> {
                    Light light = script.gameObject.GetComponent(Light.class);
                    if (light == null) return;
                    light.color = (Color) node.inputs.get(1).Value();
                });
            }
            case LightIntensity: {
                return ((script) -> {
                    Light light = script.gameObject.GetComponent(Light.class);
                    if (light == null) return;
                    light.intensity = (float) node.inputs.get(1).Value();
                });
            }
            case LightAttenuation: {
                return ((script) -> {
                    Light light = script.gameObject.GetComponent(Light.class);
                    if (light == null) return;
                    light.attenuation = (float) node.inputs.get(1).Value();
                });
            }
            case AudioPlay: {
                return ((script) -> {
                    Source source = script.gameObject.GetComponent(Source.class);
                    if (source == null) return;
                    source.Play();
                });
            }
            case AudioStop: {
                return ((script) -> {
                    Source source = script.gameObject.GetComponent(Source.class);
                    if (source == null) return;
                    source.StopPlay();
                });
            }
            case AudioPause: {
                return ((script) -> {
                    Source source = script.gameObject.GetComponent(Source.class);
                    if (source == null) return;
                    source.Pause();
                });
            }
            case AudioPitch: {
                return ((script) -> {
                    Source source = script.gameObject.GetComponent(Source.class);
                    if (source == null) return;
                    source.audioPitch = (float)node.inputs.get(1).Value();
                    source.ReloadAudio();
                });
            }
            case AudioLoop: {
                return ((script) -> {
                    Source source = script.gameObject.GetComponent(Source.class);
                    if (source == null) return;
                    source.loop = (boolean)node.inputs.get(1).Value();
                    source.ReloadAudio();
                });
            }
            case AudioPlayOnAwake: {
                return ((script) -> {
                    Source source = script.gameObject.GetComponent(Source.class);
                    if (source == null) return;
                    source.playOnAwake = (boolean)node.inputs.get(1).Value();
                    source.ReloadAudio();
                });
            }
            case ImagePosition: {
                return ((script) -> {
                    Image image = script.gameObject.GetComponent(Image.class);
                    if (image == null) return;
                    image.mesh.Position = (Vector2)node.inputs.get(1).Value();
                });
            }
            case ImageSize: {
                return ((script) -> {
                    Image image = script.gameObject.GetComponent(Image.class);
                    if (image == null) return;
                    image.mesh.Size = (Vector2)node.inputs.get(1).Value();
                });
            }
            case ImageTexture: {
                return ((script) -> {
                    Image image = script.gameObject.GetComponent(Image.class);
                    if (image == null) return;
                    image.mesh.texture = (Texture)node.inputs.get(1).object;
                });
            }
            case TextPosition: {
                return ((script) -> {
                    Text text = script.gameObject.GetComponent(Text.class);
                    if (text == null) return;
                    text.Position = (Vector2)node.inputs.get(1).Value();
                    text.UpdateTransform();
                });
            }
            case TextColor: {
                return ((script) -> {
                    Text text = script.gameObject.GetComponent(Text.class);
                    if (text == null) return;
                    text.color = (Color)node.inputs.get(1).Value();
                    text.UpdateTransform();
                });
            }
            case TextContent: {
                return ((script) -> {
                    Text text = script.gameObject.GetComponent(Text.class);
                    if (text == null) return;
                    text.text = (String)node.inputs.get(1).Value();
                    text.CreateMeshes();
                    text.UpdateTransform();
                });
            }
            case TextSize: {
                return ((script) -> {
                    Text text = script.gameObject.GetComponent(Text.class);
                    if (text == null) return;
                    text.fontSize = (int)node.inputs.get(1).Value();
                    text.CreateMeshes();
                    text.UpdateTransform();
                });
            }
        }

        return (script) -> {};
    }

    public static Consumer<NodeScript> StartFromType(ScriptingNode node) {
        switch (node.nodeType) {
            case DecomposeVector: {
                Vector3 vector = (Vector3)node.inputs.get(0).object;
                if (vector == null) {
                    return (script) -> {};
                }

                return (script) -> {
                    node.outputs.get(0).object = vector.x;
                    node.outputs.get(1).object = vector.y;
                    node.outputs.get(2).object = vector.z;
                };
            }
            case Position: {
                return (script) -> {
                    node.outputs.get(0).object = node.gameObject.transform.localPosition;
                };
            }
            case Rotation: {
                return (script) -> {
                    node.outputs.get(0).object = node.gameObject.transform.localRotation;
                };
            }
            case Scale: {
                return (script) -> {
                    node.outputs.get(0).object = node.gameObject.transform.localScale;
                };
            }
        }

        return (script) -> {};
    }

    public static Consumer<NodeScript> UpdateFromType(ScriptingNode node) {
        switch (node.nodeType) {
            case Add: {
                return (script) -> {
                    float a = (float)node.inputs.get(0).Value();
                    float b = (float)node.inputs.get(1).Value();
                    node.outputs.get(0).object = a + b;
                };
            }
            case Subtract: {
                return (script) -> {
                    float a = (float)node.inputs.get(0).Value();
                    float b = (float)node.inputs.get(1).Value();
                    node.outputs.get(0).object = a - b;
                };
            }
            case Multiply: {
                return (script) -> {
                    float a = (float)node.inputs.get(0).Value();
                    float b = (float)node.inputs.get(1).Value();
                    node.outputs.get(0).object = a * b;
                };
            }
            case Divide: {
                return (script) -> {
                    float a = (float)node.inputs.get(0).Value();
                    float b = (float)node.inputs.get(1).Value();
                    node.outputs.get(0).object = a / b;
                };
            }
            case Sine: {
                return ((script) -> {
                    node.outputs.get(0).object = Mathf.Sine((float)node.inputs.get(0).object);
                });
            }
            case Cosine: {
                return ((script) -> {
                    node.outputs.get(0).object = Mathf.Cosine((float)node.inputs.get(0).object);
                });
            }
            case Normalize: {
                return ((script) -> {
                    float val = (float)node.inputs.get(0).object;
                    float normalized = (val + 1) / 2;

                    node.outputs.get(0).object = normalized;
                });
            }
            case Vector3Add: {
                return ((script) -> {
                    Vector3 a = (Vector3)node.inputs.get(0).Value();
                    Vector3 b = (Vector3)node.inputs.get(0).Value();
                    node.outputs.get(0).object = Vector3.Add(a, b);
                });
            }
            case Vector3Subtract: {
                return ((script) -> {
                    Vector3 a = (Vector3)node.inputs.get(0).Value();
                    Vector3 b = (Vector3)node.inputs.get(0).Value();
                    node.outputs.get(0).object = Vector3.Subtract(a, b);
                });
            }
            case Vector3Multiply: {
                return ((script) -> {
                    Vector3 a = (Vector3)node.inputs.get(0).Value();
                    Vector3 b = (Vector3)node.inputs.get(0).Value();
                    node.outputs.get(0).object = Vector3.Multiply(a, b);
                });
            }
            case Vector3Divide: {
                return ((script) -> {
                    Vector3 a = (Vector3)node.inputs.get(0).Value();
                    Vector3 b = (Vector3)node.inputs.get(0).Value();
                    node.outputs.get(0).object = Vector3.Divide(a, b);
                });
            }
            case Vector3Lerp: {
                return ((script) -> {
                    Vector3 a = (Vector3)node.inputs.get(0).Value();
                    Vector3 b = (Vector3)node.inputs.get(1).Value();
                    float time = (float)node.inputs.get(2).Value();
                    node.outputs.get(0).object = Vector3.Lerp(a, b, time);
                });
            }
            case ColorLerp: {
                return ((script) -> {
                    Color aCol = (Color) node.inputs.get(0).Value();
                    Color bCol = (Color) node.inputs.get(1).Value();
                    Vector3 a = new Vector3(aCol.r, aCol.g, aCol.b);
                    Vector3 b = new Vector3(bCol.r, bCol.g, bCol.b);
                    float time = (float)node.inputs.get(2).Value();
                    Vector3 newCol = Vector3.Lerp(a, b, time);
                    node.outputs.get(0).object = Color.FromVector3(newCol);
                });
            }
            case Vector3ToColor: {
                return ((script) -> {
                    node.outputs.get(0).object = Color.FromVector3((Vector3)node.inputs.get(0).Value());
                });
            }
            case ColorToVector3: {
                return ((script) -> {
                    Color col = (Color)node.inputs.get(0).object;
                    node.outputs.get(0).object = new Vector3(col.r, col.g, col.b);
                });
            }
            case Time: {
                return (script) -> {
                    node.outputs.get(0).object = Time.time;
                    node.outputs.get(1).object = Time.deltaTime;
                };
            }
            case DecomposeVector: {
                Vector3 vector = (Vector3)node.inputs.get(0).Value();
                if (vector == null) {
                    return (script) -> {};
                }

                return (script) -> {
                    node.outputs.get(0).object = vector.x;
                    node.outputs.get(1).object = vector.y;
                    node.outputs.get(2).object = vector.z;
                };
            }
            case ComposeVector: {
                float x = (float)node.inputs.get(0).Value();
                float y = (float)node.inputs.get(1).Value();
                float z = (float)node.inputs.get(2).Value();

                return((script) -> {
                    node.outputs.get(0).object = new Vector3(x, y, z);
                });
            }
            case Position: {
                return (script) -> {
                    node.outputs.get(0).object = node.gameObject.transform.localPosition;
                };
            }
            case Rotation: {
                return (script) -> {
                    node.outputs.get(0).object = node.gameObject.transform.localRotation;
                };
            }
            case Scale: {
                return (script) -> {
                    node.outputs.get(0).object = node.gameObject.transform.localScale;
                };
            }
        }

        return (script) -> {};
    }

    public static Consumer<NodeScript> DisplayFromType(ScriptingNode node) {
        switch (node.nodeType) {
            case Integer: {
                return ((script) -> {
                    node.outputs.get(0).object = EditorGUI.DragInt("Value", (int)node.outputs.get(0).Value(), 75);
                });
            }
            case Float: {
                return ((script) -> {
                    ImGui.setNextItemWidth(75);
                    node.outputs.get(0).object = EditorGUI.DragFloat("Value", (float)node.outputs.get(0).Value());
                });
            }
            case Boolean: {
                return ((script) -> {
                    node.outputs.get(0).object = EditorGUI.Checkbox("Value", (boolean)node.outputs.get(0).Value());
                });
            }
            case String: {
                return ((script) -> {
                    ImGui.setNextItemWidth(100);
                    node.outputs.get(0).object = EditorGUI.InputString("Value", (String)node.outputs.get(0).Value());
                });
            }
            case Vector2: {
                return ((script) -> {
                    node.outputs.get(0).object = EditorGUI.DragVector2("Value", (Vector2)node.outputs.get(0).Value());
                });
            }
            case Vector3: {
                return ((script) -> {
                    node.outputs.get(0).object = EditorGUI.DragVector3("Value", (Vector3)node.outputs.get(0).Value());
                });
            }
            case Texture: {
                return ((script) -> {
                    ImGui.setCursorPosY(ImGui.getCursorPosY() + 25);
                    if (ImGui.button("Choose", 50, 30)) {
                        String path = FileExplorer.Choose("png,jpg,bmp;");
                        if (path != null) {
                            node.outputs.get(0).object = new Texture(path);
                        }
                    }
                    ImGui.sameLine();
                    ImGui.setCursorPosY(ImGui.getCursorPosY() - 25);
                    ImGui.image(((Texture)node.outputs.get(0).object).textureID, 80, 80);
                    ImGui.sameLine();
                });
            }
            case Color: {
                return ((script) -> {
                    node.outputs.get(0).object = EditorGUI.ColorField("Color##" + node.ID, (Color)node.outputs.get(0).object, ImGuiColorEditFlags.NoInputs);
                });
            }
            case Axis: {
                return ((script) -> {
                    node.outputs.get(0).object = EditorGUI.EnumSelect("Axis##" + node.ID, Axis.X.ordinal(), Axis.class);
                });
            }
        }

        return (script) -> {};
    }

}