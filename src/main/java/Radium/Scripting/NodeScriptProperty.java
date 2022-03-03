package Radium.Scripting;

import Radium.Color;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import RadiumEditor.Console;
import RadiumEditor.EditorGUI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.ArrayList;
import java.util.List;

public class NodeScriptProperty {

    public String name;
    public Class type;
    public Object value;

    public transient List<ScriptingNode> propertyNodes = new ArrayList<>();
    public List<Integer> NodeID = new ArrayList<>();

    public void Update(boolean render) {
        if (render) Render();

        for (int i = 0; i < propertyNodes.size(); i++) {
            ScriptingNode node = propertyNodes.get(i);
            if (!node.alive) {
                propertyNodes.remove(i);
                NodeID.remove(i);
                continue;
            }

            node.outputs.get(0).object = value;
            node.outputs.get(0).UpdateLinks();
        }
    }

    public void Render() {
        if (ImGui.treeNodeEx("(" + type.getSimpleName() + ")" + name, ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.DefaultOpen)) {
            if (ImGui.beginDragDropSource()) {
                ImGui.setDragDropPayload(this);
                ImGui.text(name);

                ImGui.endDragDropSource();
            }

            FixType();

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

    public void FixType() {
        if (value.getClass() == Double.class) {
            Double d = (Double)value;
            value = d.floatValue();
        }

        if (value.getClass() == LinkedTreeMap.class) {
            value = new GsonBuilder().create().fromJson(value.toString(), type);
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
        NodeID.add(node.ID);

        return node;
    }

}
