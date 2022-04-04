package Integration.Python;

import RadiumEditor.Console;
import org.python.core.*;

import java.util.HashMap;
import java.util.function.Consumer;

public class PythonFunction {

    public String name;
    private Consumer<PyObject[]> function;

    public PyObject returnObject = null;

    private int requiredNumOfParam;

    public PythonFunction(String name, int paramCount, Consumer<PyObject[]> function) {
        this.name = name;
        this.function = function;
        this.requiredNumOfParam = paramCount;
    }

    public PyFunction AsPyFunction() {
        PyCode code = GetPyCode();
        return new PyFunction(new PyStringMap(), null, code);
    }

    public void Define(Python interpreter) {
        interpreter.GetInterpreter().set(name, AsPyFunction());
        interpreter.functions.put(name, this);
    }

    private PyCode GetPyCode() {
        return new PyCode() {
            @Override
            public PyObject call(ThreadState threadState, PyFrame pyFrame, PyObject pyObject) {
                PyObject[] params = new PyObject[] { pyObject };
                function.accept(params);

                return returnObject;
            }

            @Override
            public PyObject call(ThreadState threadState, PyObject[] pyObjects, String[] strings, PyObject pyObject, PyObject[] pyObjects1, PyObject pyObject1) {
                PyObject[] params = new PyObject[] { };
                function.accept(params);

                return returnObject;
            }

            @Override
            public PyObject call(ThreadState threadState, PyObject pyObject, PyObject[] pyObjects, String[] strings, PyObject pyObject1, PyObject[] pyObjects1, PyObject pyObject2) {
                PyObject[] params = new PyObject[] { pyObject, pyObject1, pyObject2 };
                function.accept(params);

                return returnObject;
            }

            @Override
            public PyObject call(ThreadState threadState, PyObject pyObject, PyObject[] pyObjects, PyObject pyObject1) {
                if (requiredNumOfParam != 0) {
                    Console.Error("Function " + name + " contains to many parameters");

                    return null;
                }

                PyObject[] params = new PyObject[] { };
                function.accept(params);

                return returnObject;
            }

            @Override
            public PyObject call(ThreadState threadState, PyObject pyObject, PyObject pyObject1, PyObject[] pyObjects, PyObject pyObject2) {
                if (requiredNumOfParam != 1) {
                    if (requiredNumOfParam < 1) Console.Error("Function " + name + " contains too many parameters");
                    else if (requiredNumOfParam > 1) Console.Error("Function " + name + " requires at least " + requiredNumOfParam + " parameters");

                    return null;
                }

                PyObject[] params = new PyObject[] { pyObject };
                function.accept(params);

                return returnObject;
            }

            @Override
            public PyObject call(ThreadState threadState, PyObject pyObject, PyObject pyObject1, PyObject pyObject2, PyObject[] pyObjects, PyObject pyObject3) {
                if (requiredNumOfParam != 2) {
                    if (requiredNumOfParam < 2) Console.Error("Function " + name + " contains too many parameters");
                    else if (requiredNumOfParam > 2) Console.Error("Function " + name + " requires at least " + requiredNumOfParam + " parameters");

                    return null;
                }

                PyObject[] params = new PyObject[] { pyObject, pyObject1 };
                function.accept(params);

                return returnObject;
            }

            @Override
            public PyObject call(ThreadState threadState, PyObject pyObject, PyObject pyObject1, PyObject pyObject2, PyObject pyObject3, PyObject[] pyObjects, PyObject pyObject4) {
                if (requiredNumOfParam != 3) {
                    if (requiredNumOfParam < 3) Console.Error("Function " + name + " contains too many parameters");
                    else if (requiredNumOfParam > 3) Console.Error("Function " + name + " requires at least " + requiredNumOfParam + " parameters");

                    return null;
                }

                PyObject[] params = new PyObject[] { pyObject, pyObject1, pyObject2 };
                function.accept(params);

                return returnObject;
            }

            @Override
            public PyObject call(ThreadState threadState, PyObject pyObject, PyObject pyObject1, PyObject pyObject2, PyObject pyObject3, PyObject pyObject4, PyObject[] pyObjects, PyObject pyObject5) {
                if (requiredNumOfParam != 4) {
                    if (requiredNumOfParam < 4) Console.Error("Function " + name + " contains too many parameters");
                    else if (requiredNumOfParam > 4) Console.Error("Function " + name + " requires at least " + requiredNumOfParam + " parameters");

                    return null;
                }

                PyObject[] params = new PyObject[] { pyObject, pyObject1, pyObject2, pyObject3 };
                function.accept(params);

                return returnObject;
            }
        };
    }

}
