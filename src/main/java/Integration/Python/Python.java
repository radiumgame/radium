package Integration.Python;

import RadiumEditor.Console;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import java.io.File;

public class Python {

    private PythonInterpreter interpreter;

    public Python() {
        Initialize();
    }

    public void Initialize() {
        interpreter = new PythonInterpreter();
        ApplyRadiumFunctions();
    }

    public void Execute(String code) {
        interpreter.exec(code);
    }

    public void Execute(File f) {
        interpreter.execfile(f.getPath());
    }

    public void Reload() {
        interpreter.close();
        Initialize();
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

    public void ApplyRadiumFunctions() {
        new PythonFunction("log", (params) -> {
            Console.Log(params[0].asString());
        }).Define(this);
    }

}
