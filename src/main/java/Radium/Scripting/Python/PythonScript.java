package Radium.Scripting.Python;

import Integration.Python.Python;
import Radium.Objects.GameObject;

import java.io.File;

public class PythonScript {

    public File file;
    private Python python;

    public GameObject gameObject;

    public PythonScript(String path, GameObject gameObject) {
        file = new File(path);
        python = new Python(this);
        this.gameObject = gameObject;

        python.Execute(file);
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
        python.Execute(file);
    }

    public String GetName() {
        return file.getName().replace(".py", "");
    }

}
