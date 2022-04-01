package Radium.Scripting.Python;

import Integration.Python.Python;

import java.io.File;

public class PythonScript {

    private File file;
    private Python python;

    public PythonScript(String path) {
        file = new File(path);
        python = new Python();

        python.Execute(file);
    }

    public void Start() {
        python.TryCall("start");
    }

    public void Update() {
        python.TryCall("update");
    }

    public String GetName() {
        return file.getName().replace(".py", "");
    }

    public void Reload() {
        python.Reload();
    }

}
