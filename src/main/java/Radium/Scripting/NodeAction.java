package Radium.Scripting;

import Radium.Color;
import Radium.Component;
import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.MeshRenderer;
import Radium.Components.Graphics.Outline;
import Radium.Graphics.Texture;
import Radium.Math.Mathf;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.System.FileExplorer;
import Radium.Time;
import RadiumEditor.Console;
import RadiumEditor.EditorGUI;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.type.ImInt;

import java.util.function.Consumer;

public class NodeAction {

    protected NodeAction() {}

    public static Consumer<NodeScript> ActionFromType(ScriptingNode node) {
        switch (node.nodeType) {
            case GetComponent: {
                return (script) -> {
                    Class<? extends Component> clazz = (Class<? extends Component>)node.inputs.get(1).object.getClass();
                    node.outputs.get(1).object = node.gameObject.GetComponent(clazz);
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
                    filter.material.path = ((Texture)node.inputs.get(1).object).filepath;
                    filter.material.CreateMaterial();
                });
            }
            case SetMaterialNormalMap: {
                return ((script) -> {
                    MeshFilter filter = script.gameObject.GetComponent(MeshFilter.class);
                    if (filter == null) return;
                    filter.material.normalMapPath = ((Texture)node.inputs.get(1).object).filepath;
                    filter.material.CreateMaterial();
                });
            }
            case SetMaterialSpecularMap: {
                return ((script) -> {
                    MeshFilter filter = script.gameObject.GetComponent(MeshFilter.class);
                    if (filter == null) return;
                    filter.material.specularMapPath = ((Texture)node.inputs.get(1).object).filepath;
                    filter.material.CreateMaterial();
                });
            }
            case ToggleNormalMap: {
                return ((script) -> {
                    MeshFilter filter = script.gameObject.GetComponent(MeshFilter.class);
                    if (filter == null) return;
                    filter.material.useNormalMap = (boolean)node.inputs.get(1).object;
                });
            }
            case ToggleSpecularMap: {
                return ((script) -> {
                    MeshFilter filter = script.gameObject.GetComponent(MeshFilter.class);
                    if (filter == null) return;
                    filter.material.useSpecularMap = (boolean)node.inputs.get(1).object;
                });
            }
            case ToggleSpecularLighting: {
                return ((script) -> {
                    MeshFilter filter = script.gameObject.GetComponent(MeshFilter.class);
                    if (filter == null) return;
                    filter.material.specularLighting = (boolean)node.inputs.get(1).object;
                });
            }
            case ToggleCullFaces: {
                return ((script) -> {
                    MeshRenderer renderer = script.gameObject.GetComponent(MeshRenderer.class);
                    if (renderer == null) return;
                    renderer.cullFaces = (boolean)node.inputs.get(1).object;
                });
            }
            case SetOutlineWidth: {
                return ((script) -> {
                    Outline outline = script.gameObject.GetComponent(Outline.class);
                    if (outline == null) return;
                    outline.outlineWidth = (float)node.inputs.get(1).object;
                });
            }
            case SetOutlineColor: {
                return ((script) -> {
                    Outline outline = script.gameObject.GetComponent(Outline.class);
                    if (outline == null) return;
                    outline.outlineColor = (Color)node.inputs.get(1).object;
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
            case GetComponent: {
                ImInt selected = new ImInt(0);
                return ((script) -> {
                    ImGui.setNextItemWidth(150);
                    if (ImGui.combo("Component Type", selected, Component.ComponentNames())) {
                        node.outputs.get(1).object = Component.ComponentTypes().get(selected.get());
                    }
                });
            }
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
        }

        return (script) -> {};
    }

}
