package Radium.Engine.Scripting.Node.Properties;

import Radium.Editor.Console;
import Radium.Editor.EditorGUI;
import Radium.Engine.Input.Keys;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Scripting.Node.Events.Link;
import Radium.Engine.Scripting.Node.IO.NodeIO;
import Radium.Engine.Scripting.Node.IO.NodeInput;
import Radium.Engine.Scripting.Node.IO.NodeOutput;
import Radium.Engine.Scripting.Node.Node;
import Radium.Engine.Scripting.Node.NodeGraph;
import Radium.Engine.Scripting.Node.Types.*;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Property {

    public static final List<NodeIoType> availableProperties = new LinkedList<>(List.of(
            new IntType(), new FloatType(), new StringType(), new Vector2Type(), new Vector3Type(), new BooleanType(), new KeyType()
    ));
    private static final String[] availablePropertiesNames = new String[] {
            "Int", "Float", "String", "Vector2", "Vector3", "Boolean", "Key"
    };

    public String name;
    public NodeIoType type;
    public Object value;
    public NodeGraph graph;

    private List<Node> nodes = new LinkedList<>();
    private String uuid = UUID.randomUUID().toString();
    private int selectedTypeIndex = 0;

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
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 5, 5);

        boolean popTree = ImGui.treeNodeEx(uuid, TreeFlags, name + "(" + type.name + ")");

        if (ImGui.isMouseClicked(1)) {
            ImGui.openPopup("NODE_PROPERTY_RIGHT_CLICK");
        }
        RightClickMenu();

        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(this);
            ImGui.text(name);
            ImGui.endDragDropSource();
        }

        if (popTree) {
            String newName = EditorGUI.InputString("Name", name);
            if (!name.equals(newName)) {
                graph.ChangeNameOfProperty(name, newName, this);
                name = newName;
                nodes.forEach((node) -> {
                    node.name = newName;
                });
            }

            int selectedType = EditorGUI.DropdownIndex("Type", selectedTypeIndex, availablePropertiesNames);
            if (selectedType != selectedTypeIndex) {
                type = availableProperties.get(selectedType);
                value = type.defaultValue;
                selectedTypeIndex = selectedType;

                for (int i = 0; i < nodes.size(); i++) {
                    Node destroyNode = nodes.get(i);

                    for (NodeInput input : destroyNode.inputs) {
                        Link link = input.link;
                        if (link != null) {
                            graph.links.remove(link);
                            DestroyLink(link.id);
                        }
                    }
                    for (NodeOutput output : destroyNode.outputs) {
                        for (Link link : output.links) {
                            if (link != null) {
                                graph.links.remove(link);
                                DestroyLink(link.id);
                            }
                        }
                    }
                    destroyNode.ChangeOutput(0, GetOutputType());
                }
            }
            RenderChangeValue();

            ImGui.treePop();
        }
        ImGui.popStyleVar();
    }

    private void RightClickMenu() {
        if (ImGui.beginPopup("NODE_PROPERTY_RIGHT_CLICK")) {
            if (graph == null) ImGui.closeCurrentPopup();

            if (ImGui.menuItem("Delete")) {
                for (int i = 0; i < nodes.size(); i++) {
                    Node destroyNode = nodes.get(i);
                    for (NodeInput input : destroyNode.inputs) {
                        Link link = input.link;
                        if (link != null) {
                            graph.links.remove(link);
                            DestroyLink(link.id);
                        }
                    }
                    for (NodeOutput output : destroyNode.outputs) {
                        for (Link link : output.links) {
                            if (link != null) {
                                graph.links.remove(link);
                                DestroyLink(link.id);
                            }
                        }
                    }
                    graph.DestroyNode(destroyNode);
                    Node.DestroyNode(destroyNode.id);
                }

                graph.DestroyProperty(this);
            }

            ImGui.endPopup();
        }
    }

    private static void DestroyLink(int id) {
        Link link = Link.GetLinks(id);
        NodeOutput output = (NodeOutput) NodeIO.GetIO(link.startIo);
        NodeInput input = (NodeInput) NodeIO.GetIO(link.endIo);

        input.link = null;
        output.links.remove(link);
        output.SetBaseObject();

        Link.DestroyLink(id);
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
        } else if (type.name.equals("Boolean")) {
            boolean val = EditorGUI.Checkbox("Value", (boolean) value);
            if (val != (boolean) value) {
                OnValueChange(value, val);
                value = val;
            }
        } else if (type.name.equals("Key")) {
            Keys val = (Keys)EditorGUI.EnumSelect("Value", ((Keys)value).ordinal(), Keys.class);
            if (val != value) {
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
        } else if (type.name.equals("Boolean")) {
            boolean val = EditorGUI.Checkbox(name, (boolean) value);
            if (val != (boolean) value) {
                OnValueChange(value, val);
                value = val;
            }
        } else if (type.name.equals("Key")) {
            Keys val = (Keys)EditorGUI.EnumSelect(name, ((Keys)value).ordinal(), Keys.class);
            if (val != value) {
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
        else if (type.name.equals("Float")) return NodeIO.FloatOutput("Value");
        else if (type.name.equals("String")) return NodeIO.StringOutput("Value");
        else if (type.name.equals("Vector2")) return NodeIO.Vector2Output("Value");
        else if (type.name.equals("Vector3")) return NodeIO.Vector3Output("Value");
        else if (type.name.equals("Boolean")) return NodeIO.BooleanOutput("Value");
        else if (type.name.equals("Key")) return NodeIO.KeyOutput("Value");

        return NodeIO.IntOutput("Value");
    }

}
