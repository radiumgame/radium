package Radium.Engine.Scripting.Node.Properties;

import Radium.Editor.Console;
import Radium.Editor.EditorGUI;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Scripting.Node.IO.NodeIO;
import Radium.Engine.Scripting.Node.IO.NodeOutput;
import Radium.Engine.Scripting.Node.Node;
import Radium.Engine.Scripting.Node.Types.*;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.LinkedList;
import java.util.List;

public class Property {

    public static final List<NodeIoType> availableProperties = new LinkedList<>(List.of(
            new IntType(), new FloatType(), new StringType(), new Vector2Type(), new Vector3Type()
    ));

    public String name;
    public NodeIoType type;
    public Object value;

    private List<Node> nodes = new LinkedList<>();

    public Property(String name) {
        this.name = name;
        type = availableProperties.get(0);
        value = type.defaultValue;
    }

    public Node CreateNode() {
        Node property = new Node(name);
        property.AddOutput(GetOutputType());
        property.isProperty = true;
        property.property = name;
        property.outputs.get(0).value = value;
        nodes.add(property);

        return property;
    }

    private static final int TreeFlags = ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.FramePadding;
    public void RenderOptions() {
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 5);
        if (ImGui.treeNodeEx(name + "(" + type.name + ")", TreeFlags)) {
            RenderChangeValue();
            ImGui.treePop();
        }
        ImGui.popStyleVar();
    }

    public void RenderChangeValue() {
        if (type.name.equals("Int")) {
            int val = EditorGUI.DragInt("Value", (int)value);
            if (val != (int) value) {
                OnValueChange(value, val);
                value = val;
            }
        } else if (type.name.equals("Float")) {
            float val = EditorGUI.DragFloat("Value", (float)value);
            if (val != (float) value) {
                OnValueChange(value, val);
                value = val;
            }
        } else if (type.name.equals("String")) {
            String val = EditorGUI.InputString("Value", (String)value);
            if (!val.equals(value)) {
                OnValueChange(value, val);
                value = val;
            }
        } else if (type.name.equals("Vector2")) {
            Vector2 val = EditorGUI.DragVector2("Value", (Vector2) value);
            if (!val.equals(value)) {
                OnValueChange(value, val);
                value = val;
            }
        } else if (type.name.equals("Vector3")) {
            Vector3 val = EditorGUI.DragVector3("Value", (Vector3) value);
            if (!val.equals(value)) {
                OnValueChange(value, val);
                value = val;
            }
        }
    }

    public void RenderChangeValueWithName() {
        if (type.name.equals("Int")) {
            int val = EditorGUI.DragInt(name, (int)value);
            if (val != (int) value) {
                OnValueChange(value, val);
                value = val;
            }
        } else if (type.name.equals("Float")) {
            float val = EditorGUI.DragFloat(name, (float)value);
            if (val != (float) value) {
                OnValueChange(value, val);
                value = val;
            }
        } else if (type.name.equals("String")) {
            String val = EditorGUI.InputString(name, (String)value);
            if (!val.equals(value)) {
                OnValueChange(value, val);
                value = val;
            }
        } else if (type.name.equals("Vector2")) {
            Vector2 val = EditorGUI.DragVector2(name, (Vector2) value);
            if (!val.equals(value)) {
                OnValueChange(value, val);
                value = val;
            }
        } else if (type.name.equals("Vector3")) {
            Vector3 val = EditorGUI.DragVector3(name, (Vector3) value);
            if (!val.equals(value)) {
                OnValueChange(value, val);
                value = val;
            }
        }
    }

    private void OnValueChange(Object oldVal, Object newVal) {
        nodes.forEach((node) -> {
            node.outputs.get(0).value = newVal;
            node.UpdateValue(node.outputs.get(0));
        });
    }

    private NodeOutput GetOutputType() {
        if (type.name.equals("Int")) return NodeIO.IntOutput("Value");
        else if (type.name.equals("Float")) return NodeIO.IntOutput("Value");
        else if (type.name.equals("String")) return NodeIO.IntOutput("Value");
        else if (type.name.equals("Vector2")) return NodeIO.IntOutput("Value");
        else if (type.name.equals("Vector3")) return NodeIO.IntOutput("Value");

        return NodeIO.IntOutput("Value");
    }

}
