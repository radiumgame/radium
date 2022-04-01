package Radium.Components.Scripting;

import Radium.Component;
import Radium.Scripting.Nodes.NodeScript;
import Radium.Scripting.Nodes.NodeScriptProperty;
import Radium.Scripting.Python.PythonScript;
import Radium.System.FileExplorer;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class PythonScripting extends Component {

    private List<PythonScript> scripts = new ArrayList<>();

    public PythonScripting() {
        name = "Python Scripting";
        submenu = "Scripting";
        LoadIcon("python.png");
    }

    @Override
    public void Start() {
        for (PythonScript script : scripts) {
            script.Start();
        }
    }

    @Override
    public void Update() {
        for (PythonScript script : scripts) {
            script.Update();
        }
    }

    private float buttonPadding = 20;
    @Override
    public void GUIRender() {
        if (ImGui.button("Reload Scripts")) {
            for (PythonScript script : scripts) {
                script.Reload();
            }
        }
        ImGui.sameLine();
        if (ImGui.treeNodeEx("Scripts")) {
            for (int i = 0; i < scripts.size(); i++) {
                PythonScript script = scripts.get(i);

                if (ImGui.button("Remove")) {
                    scripts.remove(i);
                }
                ImGui.sameLine();
                if (ImGui.treeNodeEx(script.GetName())) {
                    ImGui.treePop();
                }
            }

            ImGui.treePop();
        }


        ImGui.setCursorPosX(buttonPadding);
        if (ImGui.button("Add Script", ImGui.getWindowWidth() - (buttonPadding * 2), 25)) {
            String path = FileExplorer.Choose("py");
            if (path != null) {
                scripts.add(new PythonScript(path));
            }
        }
    }
}
