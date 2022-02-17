package Radium.Scripting;

import Radium.Color;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import RadiumEditor.Console;
import RadiumEditor.EditorGUI;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.ArrayList;
import java.util.List;

public class NodeScriptProperty {

    public String name;
    public Class type;
    public Object value;

    private List<ScriptingNode> propertyNodes = new ArrayList<>();

    public void Update() {
        Render();

        for (ScriptingNode node : propertyNodes) {
            node.outputs.get(0).object = value;
            for (NodeInput link : node.outputs.get(0).links) {
                link.object = value;
            }
        }
    }

    public void Render() {
        if (ImGui.treeNodeEx("(" + type.getSimpleName() + ")" + name, ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.DefaultOpen)) {
            if (ImGui.beginDragDropSource()) {
                ImGui.setDragDropPayload(this);
                ImGui.text(name);

                ImGui.endDragDropSource();
            }

            if (value.getClass() == Double.class) {
                Double d = (Double)value;
                value = d.floatValue();
            }

            if (value.getClass() == Integer.class) {
                value = EditorGUI.DragInt("Value", (int)value);
            } else if (value.getClass() == Float.class) {
                value = EditorGUI.DragFloat("Value", (float)value);
            } else if (value.getClass() == String.class) {
                value = EditorGUI.InputString("Value", (String)value);
            } else if (value.getClass() == Boolean.class) {
                value = EditorGUI.Checkbox("Value", (boolean)value);
            } else if (value.getClass() == Vector2.class) {
                value = EditorGUI.DragVector2("Value", (Vector2)value);
            } else if (value.getClass() == Vector3.class) {
                value = EditorGUI.DragVector3("Value", (Vector3)value);
            } else if (value.getClass() == Color.class) {
                value = EditorGUI.ColorField("Value", (Color)value);
            }

            ImGui.treePop();
        }
    }

    public ScriptingNode GetNode() {
        ScriptingNode node = new ScriptingNode();
        node.name = name;
        node.inputs.clear();

        NodeInput output = node.outputs.get(0);
        output.name = name;
        output.type = type;
        output.object = value;

        propertyNodes.add(node);
        return node;
    }

}
