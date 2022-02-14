package Radium.Components;

import Radium.Component;
import Radium.Scripting.NodeInput;
import Radium.Scripting.NodeScript;
import Radium.Scripting.ScriptingNode;
import Radium.System.FileExplorer;
import RadiumEditor.Console;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.ArrayList;
import java.util.List;

public class NodeScriptManager extends Component {

    private List<NodeScript> scripts = new ArrayList<>();

    public NodeScriptManager() {
        name = "Script Manager";
    }

    @Override
    public void Start() {
        for (NodeScript script : scripts) {
            script.Start();
        }
    }

    @Override
    public void Update() {
        for (NodeScript script : scripts) {
            script.Update();
        }
    }

    @Override
    public void Stop() {

    }

    @Override
    public void OnAdd() {

    }

    @Override
    public void OnRemove() {

    }

    @Override
    public void UpdateVariable() {

    }

    @Override
    public void GUIRender() {
        if (ImGui.treeNodeEx("Scripts", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            for (NodeScript script : scripts) {
                ImGui.treeNodeEx(script.name, ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Leaf);
                ImGui.treePop();
            }

            ImGui.treePop();
        }

        if (ImGui.button("Add Script", ImGui.getWindowWidth(), 30)) {
            String path = FileExplorer.Choose("script");
            if (path != null) {
                scripts.add(NodeScript.Load(path));
            }
        }
    }

}
