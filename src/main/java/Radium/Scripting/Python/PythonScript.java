package Radium.Scripting.Python;

import Integration.Python.Python;
import Integration.Python.PythonLibrary;
import Radium.Objects.GameObject;
import Radium.Util.FileUtility;
import org.python.core.PyObject;
import org.python.core.PyType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PythonScript {

    public File file;
    private transient Python python;

    public transient GameObject gameObject;

    public PythonScript(String path, GameObject gameObject) {
        file = new File(path);
        this.gameObject = gameObject;

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

}
