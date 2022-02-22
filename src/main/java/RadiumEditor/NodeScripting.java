package RadiumEditor;

import Radium.Color;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.Scripting.*;
import Radium.System.FileExplorer;
import imgui.ImColor;
import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;
import imgui.extension.imnodes.flag.ImNodesColorStyle;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;

import java.util.HashMap;
import java.util.Locale;

public class NodeScripting {

    public static NodeScript currentScript;

    private static int GridBackground = ImColor.floatToColor(0.09f, 0.09f, 0.09f, 0.94f);
    private static int TitleBarColor = ImColor.floatToColor(0.18f, 0.18f, 0.18f, 1.0f);

    protected NodeScripting() {}

    public static void Render() {
        ImGui.begin("Node Scripting", ImGuiWindowFlags.MenuBar);

        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("New")) {
                    NodeScript script = new NodeScript();
                    currentScript = script;
                }
                if (ImGui.menuItem("Open")) {
                    String path = FileExplorer.Choose("script");
                    if (path != null) {
                        currentScript = NodeScript.Load(path);
                    }
                }
                if (ImGui.menuItem("Save")) {
                    currentScript.Save();
                }

                ImGui.endMenu();
            }

            ImGui.endMenuBar();
        }

        if (currentScript == null) {
            ImGui.setCursorPos((ImGui.getWindowWidth() / 2) - 125, ImGui.getWindowHeight() / 2);
            if (ImGui.button("Open Script", 250, 50)) {
                String path = FileExplorer.Choose("script");
                if (path != null) {
                    currentScript = NodeScript.Load(path);
                }
            }

            ImGui.end();
            return;
        }

        if (ImGui.button("Create Node")) {
            ImGui.openPopup("Create Node");
        }
        if (ImGui.button("Create Property")) {
            ImGui.openPopup("Create Property");
        }

        CreatePopup();
        CreateProperty();

        ImGui.beginChildFrame(1, ImGui.getWindowWidth() / 5f, ImGui.getWindowHeight() - 150);

        for (NodeScriptProperty property : currentScript.properties) {
            property.Update(true);
        }
        ImGui.endChildFrame();

        ImGui.sameLine();

        ImNodes.pushColorStyle(ImNodesColorStyle.GridBackground, GridBackground);
        ImNodes.beginNodeEditor();
        ImNodes.popColorStyle();

        PushStyle();

        for (ScriptingNode node : currentScript.nodes) {
            ImNodes.pushColorStyle(ImNodesColorStyle.TitleBar, TitleBarColor);
            ImNodes.beginNode(node.ID);
            ImNodes.popColorStyle();

            ImNodes.getNodeGridSpacePos(node.ID, node.position);

            ImNodes.beginNodeTitleBar();
            ImGui.text(node.name);
            ImNodes.endNodeTitleBar();

            for (NodeInput input : node.inputs) {
                ImNodes.beginInputAttribute(input.ID);
                ImGui.text(input.name);
                ImNodes.endInputAttribute();
            }

            ImGui.spacing();

            ImGui.sameLine();
            node.display.accept(currentScript);
            ImGui.sameLine();

            ImGui.spacing();

            for (NodeInput output : node.outputs) {
                ImNodes.beginOutputAttribute(output.ID);
                ImGui.text(output.name);
                ImNodes.endOutputAttribute();
            }
            node.Update(currentScript);

            ImNodes.endNode();
        }

        int i = 2;
        HashMap<Integer, Integer> Links = new HashMap<>();
        for (int l = 0; l < currentScript.links.size(); l++) {
            ImNodes.link(i, currentScript.links.get(l)[0].ID, currentScript.links.get(l)[1].ID);
            Links.put(i, l);
            i++;
        }

        PopStyle();

        ImNodes.endNodeEditor();

        if (ImGui.beginDragDropTarget()) {
            if (ImGui.isMouseReleased(0)) {
                NodeScriptProperty property = ImGui.getDragDropPayload(NodeScriptProperty.class);
                currentScript.CreateNode(property.GetNode());
            }

            ImGui.endDragDropTarget();
        }

        ImGui.end();

        ImInt start = new ImInt(0), end = new ImInt(0);
        if (ImNodes.isLinkCreated(start, end)) {
            NodeInput nodeStart = currentScript.GetNodeInputByID(start.get());
            NodeInput nodeEnd = currentScript.GetNodeInputByID(end.get());

            if (nodeStart.type != nodeEnd.type && nodeEnd.type != Object.class) {
                if (!nodeEnd.type.isAssignableFrom(nodeStart.type)) {
                    Console.Error("Types do not match");
                    return;
                }
            }

            nodeStart.links.add(nodeEnd);
            nodeEnd.links.add(nodeStart);
            nodeStart.Link(nodeEnd);
            currentScript.links.add(new NodeInput[] { nodeStart, nodeEnd });
        }
    }

    private static String search = "";
    private static HashMap<NodeType, ScriptingNode> nodesToShow = new HashMap<>();
    private static boolean searching;
    private static void CreatePopup() {
        ImGui.setNextWindowSize(450, 350);
        if (ImGui.beginPopup("Create Node")) {
            String newSearch = EditorGUI.InputString("Search", search);
            if (search != newSearch) {
                nodesToShow.clear();
                for (NodeType type : NodeType.values()) {
                    if (type.name().toLowerCase().contains(search.toLowerCase(Locale.ROOT))) {
                        ScriptingNode node = ScriptingNode.NodeFromType(type);
                        if (node != null) nodesToShow.put(type, node);
                    }
                }

                search = newSearch;
            }
            searching = (!search.isEmpty() || !search.isBlank());

            if (searching) {
                for (ScriptingNode node : nodesToShow.values()) {
                    RenderChoice(node.name, node);
                }
            } else {
                if (StartSubmenu("Properties")) {
                    RenderChoice("Integer", Nodes.Integer());
                    RenderChoice("Float", Nodes.Float());
                    RenderChoice("Boolean", Nodes.Boolean());
                    RenderChoice("String", Nodes.String());
                    RenderChoice("Vector2", Nodes.Vector2());
                    RenderChoice("Vector3", Nodes.Vector3());
                    RenderChoice("Color", Nodes.Color());
                    RenderChoice("Texture", Nodes.Texture());

                    EndSubmenu();
                }
                if (StartSubmenu("Math")) {
                    if (StartSubmenu("Integer/Float")) {
                        RenderChoice("Add", Nodes.AddNode());
                        RenderChoice("Subtract", Nodes.SubtractNode());
                        RenderChoice("Multiply", Nodes.MultiplyNode());
                        RenderChoice("Divide", Nodes.DivideNode());

                        EndSubmenu();
                    }
                    if (StartSubmenu("Vector3")) {
                        RenderChoice("Vector3 Add", Nodes.Vector3AddNode());
                        RenderChoice("Vector3 Subtract", Nodes.Vector3SubtractNode());
                        RenderChoice("Vector3 Multiply", Nodes.Vector3MultiplyNode());
                        RenderChoice("Vector3 Divide", Nodes.Vector3DivideNode());
                        RenderChoice("Vector3 Lerp", Nodes.Vector3LerpNode());
                        RenderChoice("Compose Vector", Nodes.ComposeVector());
                        RenderChoice("Decompose Vector", Nodes.DecomposeVector());

                        EndSubmenu();
                    }

                    RenderChoice("Sine", Nodes.SineNode());
                    RenderChoice("Cosine", Nodes.CosineNode());
                    RenderChoice("Normalize", Nodes.Normalize());

                    EndSubmenu();
                }
                if (StartSubmenu("Transform")) {
                    RenderChoice("Get Position", Nodes.Position());
                    RenderChoice("Set Position", Nodes.SetPosition());
                    RenderChoice("Get Rotation", Nodes.Rotation());
                    RenderChoice("Set Rotation", Nodes.SetRotation());
                    RenderChoice("Get Scale", Nodes.Scale());
                    RenderChoice("Set Scale", Nodes.SetScale());
                    RenderChoice("Translate", Nodes.Translate());
                    RenderChoice("Rotate", Nodes.Rotate());
                    RenderChoice("Scale", Nodes.Scaling());

                    EndSubmenu();
                }
                if (StartSubmenu("Components")) {
                    RenderChoice("Get Component", Nodes.GetComponent());
                    if (StartSubmenu("Mesh Filter")) {
                        RenderChoice("Destroy Mesh", Nodes.DestroyMesh());
                        RenderChoice("Set Material Texture", Nodes.SetMaterialTexture());
                        RenderChoice("Set Material Normal Map", Nodes.SetMaterialNormalMap());
                        RenderChoice("Set Material Specular Map", Nodes.SetMaterialSpecularMap());
                        RenderChoice("Toggle Normal Map", Nodes.ToggleNormalMap());
                        RenderChoice("Toggle Specular Map", Nodes.ToggleSpecularMap());
                        RenderChoice("Toggle Specular Lighting", Nodes.ToggleSpecularLighting());

                        EndSubmenu();
                    }
                    if (StartSubmenu("Mesh Renderer")) {
                        RenderChoice("Toggle Cull Faces", Nodes.ToggleCullFaces());

                        EndSubmenu();
                    }
                    if (StartSubmenu("Outline")) {
                        RenderChoice("Outline Width", Nodes.OutlineWidth());
                        RenderChoice("Outline Color", Nodes.OutlineColor());

                        EndSubmenu();
                    }

                    EndSubmenu();
                }
                if (StartSubmenu("Convert")) {
                    RenderChoice("Vector3 to Color", Nodes.Vector3ToColor());
                    RenderChoice("Color to Vector3", Nodes.ColorToVector3());

                    EndSubmenu();
                }

                RenderChoice("Log", Nodes.Log());
                RenderChoice("Time", Nodes.Time());
            }

            ImGui.endPopup();
        }
    }

    private static String Name = "New Property";
    private static PropertyType Type = PropertyType.Integer;
    private static Object Value = 0;
    private static void CreateProperty() {
        ImGui.setNextWindowSize(400, 350);
        if (ImGui.beginPopup("Create Property")) {
            Name = EditorGUI.InputString("Name", Name);
            Type = (PropertyType)EditorGUI.EnumSelect("Property Type", Type.ordinal(), PropertyType.class);
            if (Type == PropertyType.Integer) {
                if (Value.getClass() != Integer.class) {
                    Value = 0;
                }

                Value = EditorGUI.DragInt("Value", (int)Value);
            } else if (Type == PropertyType.Float) {
                if (Value.getClass() != Float.class) {
                    Value = 0f;
                }

                Value = EditorGUI.DragFloat("Value", (float)Value);
            } else if (Type == PropertyType.String) {
                if (Value.getClass() != String.class) {
                    Value = "";
                }

                Value = EditorGUI.InputString("Value", (String)Value);
            } else if (Type == PropertyType.Boolean) {
                if (Value.getClass() != Boolean.class) {
                    Value = false;
                }

                Value = EditorGUI.Checkbox("Value", (boolean)Value);
            } else if (Type == PropertyType.Vector2) {
                if (Value.getClass() != Vector2.class) {
                    Value = Vector2.Zero();
                }

                Value = EditorGUI.DragVector2("Value", (Vector2)Value);
            } else if (Type == PropertyType.Vector3) {
                if (Value.getClass() != Vector3.class) {
                    Value = Vector3.Zero();
                }

                Value = EditorGUI.DragVector3("Value", (Vector3)Value);
            } else if (Type == PropertyType.Color) {
                if (Value.getClass() != Color.class) {
                    Value = new Color(1f, 1f, 1f, 1f);
                }

                Value = EditorGUI.ColorField("Value", (Color)Value);
            }

            if (ImGui.button("Create Property")) {
                CreatePropertyFromValues();
            }

            ImGui.endPopup();
        }
    }

    private static boolean StartSubmenu(String name) {
        return ImGui.treeNodeEx(name, ImGuiTreeNodeFlags.SpanAvailWidth);
    }

    private static void EndSubmenu() {
        ImGui.treePop();
    }

    private static void RenderChoice(String name, ScriptingNode node) {
        if (ImGui.treeNodeEx(name, ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.DefaultOpen)) {
            if (ImGui.isItemClicked(0)) {
                currentScript.CreateNode(node);
                ImGui.closeCurrentPopup();
            }

            ImGui.treePop();
        }
    }

    private static void CreatePropertyFromValues() {
        NodeScriptProperty property = new NodeScriptProperty();
        property.name = new String(Name);
        property.type = Value.getClass();
        property.value = Value;

        currentScript.properties.add(property);
        Name = "New Property";
        Value = 0;
        Type = PropertyType.Integer;

        ImGui.closeCurrentPopup();
    }

    private static void PushStyle() {
        ImNodes.pushColorStyle(ImNodesColorStyle.LinkHovered, ImColor.floatToColor(1f, 0.78f, 0.3f, 1f));
        ImNodes.pushColorStyle(ImNodesColorStyle.LinkSelected, ImColor.floatToColor(1f, 0.78f, 0.3f, 1f));
    }

    private static void PopStyle() {
        ImNodes.popColorStyle();
        ImNodes.popColorStyle();
    }

}
