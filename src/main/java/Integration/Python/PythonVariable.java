package Integration.Python;

import org.python.core.PyObject;

public class PythonVariable {

    public String name;
    private PyObject value;

    private Python interpreter = null;

    public PythonVariable(String name, PyObject value) {
        this.name = name;
        this.value = value;
    }

    public void Define(Python interpreter) {
        this.interpreter = interpreter;
        interpreter.GetInterpreter().set(name, value);
    }

    public void SetValue(PyObject value) {
        this.value = value;

        if (interpreter != null) {
            interpreter.GetInterpreter().set(name, this.value);
        }
    }


}
