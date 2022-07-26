package Radium.Engine.Components.Scripting;

import Radium.Engine.PerformanceImpact;
import Radium.Integration.Python.UserVariable;
import Radium.Engine.Color.Color;
import Radium.Engine.Component;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Scripting.Python.PythonScript;
import Radium.Engine.System.FileExplorer;
import Radium.Editor.Console;
import Radium.Editor.EditorGUI;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import org.python.core.*;

import java.util.ArrayList;
import java.util.List;

public class PythonScripting extends Component {

    public List<PythonScript> scripts = new ArrayList<>();

    public PythonScripting() {
        name = "Python Scripting";
        submenu = "Scripting";
        LoadIcon("python.png");

        impact = PerformanceImpact.Dependent;
        description = "Write and run python scripts";
    }

    
    public void Start() {
        for (PythonScript script : scripts) {
            script.Start();
        }
    }

    
    public void Update() {
        for (PythonScript script : scripts) {
            script.Update();
        }
    }

    
    public void Stop() {
        for (PythonScript script : scripts) {
            script.Stop();
        }
    }

    private float buttonPadding = 20;
    
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
                if (ImGui.treeNodeEx(script.GetName(), script.python.variables.size() > 0 ? ImGuiTreeNodeFlags.None : ImGuiTreeNodeFlags.Leaf)) {
                    RenderVariables(script);
                    ImGui.treePop();
                }
            }

            ImGui.treePop();
        }


        ImGui.setCursorPosX(buttonPadding);
        if (ImGui.button("Add Script", ImGui.getWindowWidth() - (buttonPadding * 2), 25)) {
            String path = FileExplorer.Choose("py");
            if (FileExplorer.IsPathValid(path)) {
                for (PythonScript script : scripts) {
                    if (script.file.getPath().equals(path)) {
                        Console.Error("Script already exists");
                        return;
                    }
                }

                scripts.add(new PythonScript(path, gameObject));
            }
        }
    }

    private void RenderVariables(PythonScript script) {
        for (UserVariable variable : script.python.variables) {
            RenderVariable(script, variable);
        }
    }

    private void RenderVariable(PythonScript script, UserVariable variable) {
        switch (variable.type) {
            case "Integer":
                variable.Set(EditorGUI.DragInt(variable.name, (int)variable.value), script.python);
                break;
            case "Double":
                variable.Set((double)EditorGUI.DragFloat(variable.name, ((Double)variable.value).floatValue()), script.python);
                break;
            case "String":
                variable.Set(EditorGUI.InputString(variable.name, (String)variable.value), script.python);
                break;
            case "Boolean":
                variable.Set(EditorGUI.Checkbox(variable.name, (boolean)variable.value), script.python);
                break;
            case "Vector2":
                Vector2 vec2 = EditorGUI.DragVector2(variable.name, new Vector2(variable.GetFloat("x"), variable.GetFloat("y")));
                variable.SetProperty("x", new PyFloat(vec2.x));
                variable.SetProperty("y", new PyFloat(vec2.y));
                variable.UpdateProperties(script.python);

                break;
            case "Vector3":
                Vector3 vec3 = EditorGUI.DragVector3(variable.name, new Vector3(variable.GetFloat("x"), variable.GetFloat("y"), variable.GetFloat("z")));
                variable.SetProperty("x", new PyFloat(vec3.x));
                variable.SetProperty("y", new PyFloat(vec3.y));
                variable.SetProperty("z", new PyFloat(vec3.z));
                variable.UpdateProperties(script.python);

                break;
            case "Color":
                Color col = EditorGUI.ColorField(variable.name, new Color(variable.GetFloat("r"), variable.GetFloat("g"), variable.GetFloat("b"), variable.GetFloat("a")));
                variable.SetProperty("r", new PyFloat(col.r));
                variable.SetProperty("g", new PyFloat(col.g));
                variable.SetProperty("b", new PyFloat(col.b));
                variable.SetProperty("a", new PyFloat(col.a));
                variable.UpdateProperties(script.python);

                break;
        }
    }

    private void ReloadScripts() {
        List<String> paths = new ArrayList<>();
        for (PythonScript script : scripts) paths.add(script.file.getPath());
        scripts.clear();
        for (String path : paths) scripts.add(new PythonScript(path, gameObject));
    }

    public PythonScript Find(String name) {
        for (PythonScript script : scripts) {
            if (script.GetName().equals(name)) return script;
        }

        return null;
    }

}
