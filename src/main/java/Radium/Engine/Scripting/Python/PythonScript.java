package Radium.Engine.Scripting.Python;

import Radium.Integration.Python.Python;
import Radium.Integration.Python.PythonLibrary;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.Util.FileUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PythonScript {

    public File file;
    public transient Python python;

    public transient GameObject gameObject;
    public transient String id;

    private static transient final List<PythonScript> scripts = new ArrayList<>();

    public PythonScript() {}

    public PythonScript(String path, GameObject gameObject) {
        file = new File(path);
        this.gameObject = gameObject;
        id = UUID.randomUUID().toString();

        scripts.add(this);
        Initialize();
    }

    public void Initialize() {
        python = new Python(this);
        python.Initialize();

        List<File> files = Search("EngineAssets/Python/");
        for (File file : files) {
            python.AddLibrary(new PythonLibrary(file));
        }

        python.Execute(FileUtility.ReadFile(file));
    }

    public void Start() {
        python.TryCall("start");
    }

    public void Update() {
        python.Update();
        python.TryCall("update");
    }

    public void Stop() {
        python.TryCall("stop");

        python.Initialize();
        python.Execute(FileUtility.ReadFile(file));
    }

    public String GetName() {
        return file.getName().replace(".py", "");
    }

    private List<File> Search(String path) {
        List<File> result = new ArrayList<>();

        File folder = new File(path);
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                result.addAll(Search(file.getPath()));
            } else {
                result.add(file);
            }
        }

        return result;
    }

    public static PythonScript Find(String id) {
        for (PythonScript script : scripts) {
            if (script.id.equals(id)) {
                return script;
            }
        }

        return null;
    }

}
