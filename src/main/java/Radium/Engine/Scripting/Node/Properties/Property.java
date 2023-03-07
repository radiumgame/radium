package Radium.Engine.Scripting.Node.Properties;

import Radium.Editor.Console;
import Radium.Editor.EditorGUI;
import Radium.Engine.Scripting.Node.IO.NodeIO;
import Radium.Engine.Scripting.Node.IO.NodeOutput;
import Radium.Engine.Scripting.Node.Node;
import Radium.Engine.Scripting.Node.Types.*;
import imgui.ImGui;
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

    private static final int TreeFlags = ImGuiTreeNodeFlags.SpanAvailWidth;
    public void RenderOptions() {
        if (ImGui.treeNodeEx(name, TreeFlags)) {
            RenderChangeValue();

            ImGui.treePop();
        }
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

        } else if (type.name.equals("Vector2")) {

        } else if (type.name.equals("Vector3")) {

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

        } else if (type.name.equals("Vector2")) {

        } else if (type.name.equals("Vector3")) {

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
