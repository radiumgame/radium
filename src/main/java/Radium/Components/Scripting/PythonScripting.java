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

    public List<PythonScript> scripts = new ArrayList<>();

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

    @Override
    public void Stop() {
        for (PythonScript script : scripts) {
            script.Stop();
        }
    }

    private float buttonPadding = 20;
    @Override
    public void GUIRender() {
        if (ImGui.button("Reload Scripts")) {
            ReloadScripts();
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
                scripts.add(new PythonScript(path, gameObject));
            }
        }
    }

    private void ReloadScripts() {
        List<String> paths = new ArrayList<>();
        for (PythonScript script : scripts) paths.add(script.file.getPath());
        scripts.clear();
        for (String path : paths) scripts.add(new PythonScript(path, gameObject));
    }

}
