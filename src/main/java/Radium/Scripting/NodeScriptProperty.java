package Radium.Scripting;

import Radium.Math.Random;
import RadiumEditor.EditorGUI;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

public class NodeScriptProperty {

    public String name;
    public Class type;
    public Object value;

    public void Render() {
        if (ImGui.treeNodeEx("(" + type.getName() + ")" + name, ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.DefaultOpen)) {
            if (ImGui.beginDragDropSource()) {
                ImGui.setDragDropPayload(this);
                ImGui.text(name);

                ImGui.endDragDropSource();
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

        return node;
    }

}
