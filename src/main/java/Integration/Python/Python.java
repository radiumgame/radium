package Integration.Python;

import Radium.Math.Vector.Vector3;
import Radium.Scripting.Python.PythonScript;
import Radium.Time;
import Radium.Util.FileUtility;
import RadiumEditor.Console;
import org.python.core.PyException;
import org.python.core.PyFloat;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.io.OutputStream;

public class Python {

    private PythonInterpreter interpreter;
    private PythonScript script;

    private PythonVariable deltaTime;

    public Python(PythonScript script) {
        this.script = script;
        Initialize();
    }

    public void Initialize() {
        if (interpreter != null) interpreter.close();
        interpreter = new PythonInterpreter();
        interpreter.setErr(OutputStream.nullOutputStream());

        ApplyRadiumVariables();
        ApplyRadiumFunctions();
    }

    public void Execute(String code) {
        interpreter.exec(code);
    }

    public void Execute(File f) {
        try {
            interpreter.execfile(f.getPath());
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public void Update() {
        if (Time.deltaTime != 0) {
            deltaTime.SetValue(new PyFloat(Time.deltaTime));
        }
    }

    public boolean TryCall(String function) {
        PyObject func = interpreter.get(function);
        if (func != null && func.isCallable()) {
            func.__call__();
            return true;
        }

        return false;
    }

    public PythonInterpreter GetInterpreter() {
        return interpreter;
    }

    private void ApplyRadiumVariables() {
        deltaTime = new PythonVariable("deltaTime", new PyFloat(Time.deltaTime));
        deltaTime.Define(this);
    }

    private void ApplyRadiumFunctions() {
        new PythonFunction("log", (params) -> {
            Console.Log(params[0].asString());
        }).Define(this);

        new PythonFunction("setPosition", (params) -> {
            Vector3 newPos = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            script.gameObject.transform.localPosition = newPos;
        }).Define(this);
    }

}
