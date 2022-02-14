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

public class NodeScripting {

    public static NodeScript currentScript;

    private static int GridBackground = ImColor.floatToColor(0.09f, 0.09f, 0.09f, 0.94f);
    private static int TitleBarColor = ImColor.floatToColor(0.18f, 0.18f, 0.18f, 1.0f);

    protected NodeScripting() {}

    public static void Render() {
        ImGui.begin("Node Scripting", ImGuiWindowFlags.MenuBar);

        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
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

        if (ImGui.button("Create Node")) {
            ImGui.openPopup("Create Node");
        }
        if (ImGui.button("Create Property")) {
            ImGui.openPopup("Create Property");
        }
        if (ImGui.button("Run")) {
            Run();
        }

        CreatePopup();
        CreateProperty();

        ImGui.beginChildFrame(1, ImGui.getWindowWidth() / 5f, ImGui.getWindowHeight() - 150);

        for (NodeScriptProperty property : currentScript.properties) {
            property.Update();
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

            for (NodeInput output : node.outputs) {
                ImNodes.beginOutputAttribute(output.ID);
                ImGui.text(output.name);
                ImNodes.endOutputAttribute();
            }
            node.Update();

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
                currentScript.nodes.add(property.GetNode());
            }

            ImGui.endDragDropTarget();
        }

        ImGui.end();

        ImInt start = new ImInt(0), end = new ImInt(0);
        if (ImNodes.isLinkCreated(start, end)) {
            NodeInput nodeStart = currentScript.GetNodeInputByID(start.get());
            NodeInput nodeEnd = currentScript.GetNodeInputByID(end.get());

            if (nodeStart.type != nodeEnd.type && nodeEnd.type != Object.class) {
                Console.Error("Types do not match");
                return;
            }

            nodeStart.links.add(nodeEnd);
            nodeEnd.links.add(nodeStart);
            nodeStart.Link(nodeEnd);
            currentScript.links.add(new NodeInput[] { nodeStart, nodeEnd });
        }
    }

    private static void CreatePopup() {
        ImGui.setNextWindowSize(200, 350);
        if (ImGui.beginPopup("Create Node")) {
            if (StartSubmenu("Math")) {
                RenderChoice("Add", Nodes.AddNode());
                RenderChoice("Subtract", Nodes.SubtractNode());
                RenderChoice("Multiply", Nodes.MultiplyNode());
                RenderChoice("Divide", Nodes.DivideNode());

                EndSubmenu();
            }

            RenderChoice("Log", Nodes.Log());
            RenderChoice("Time", Nodes.Time());

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
                currentScript.nodes.add(node);
                ImGui.closeCurrentPopup();
            }

            ImGui.treePop();
        }
    }

    private static void Run() {
        ScriptingNode start = currentScript.nodes.get(0);

        TriggerNode(start);
    }

    private static void TriggerNode(ScriptingNode node) {
        node.action.run();

        NodeInput trigger = node.GetTriggerOutput();
        if (trigger == null) return;

        for (NodeInput link : trigger.links) {
            TriggerNode(link.node);
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
