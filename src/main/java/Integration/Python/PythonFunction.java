package Integration.Python;

import org.python.core.*;

import java.util.function.Consumer;

public class PythonFunction {

    public String name;
    private Consumer<PyObject[]> function;

    public PythonFunction(String name, Consumer<PyObject[]> function) {
        this.name = name;
        this.function = function;
    }

    public PyFunction AsPyFunction() {
        PyCode code = GetPyCode();
        return new PyFunction(new PyStringMap(), null, code);
    }

    public void Define(Python interpreter) {
        interpreter.GetInterpreter().set(name, AsPyFunction());
    }

    private PyCode GetPyCode() {
        return new PyCode() {
            @Override
            public PyObject call(ThreadState threadState, PyFrame pyFrame, PyObject pyObject) {
                PyObject[] params = new PyObject[] { pyObject };
                function.accept(params);

                return null;
            }

            @Override
            public PyObject call(ThreadState threadState, PyObject[] pyObjects, String[] strings, PyObject pyObject, PyObject[] pyObjects1, PyObject pyObject1) {
                PyObject[] params = new PyObject[] { };
                function.accept(params);

                return null;
            }

            @Override
            public PyObject call(ThreadState threadState, PyObject pyObject, PyObject[] pyObjects, String[] strings, PyObject pyObject1, PyObject[] pyObjects1, PyObject pyObject2) {
                PyObject[] params = new PyObject[] { pyObject, pyObject1, pyObject2 };
                function.accept(params);

                return null;
            }

            @Override
            public PyObject call(ThreadState threadState, PyObject pyObject, PyObject[] pyObjects, PyObject pyObject1) {
                PyObject[] params = new PyObject[] { };
                function.accept(params);

                return null;
            }

            @Override
            public PyObject call(ThreadState threadState, PyObject pyObject, PyObject pyObject1, PyObject[] pyObjects, PyObject pyObject2) {
                PyObject[] params = new PyObject[] { pyObject };
                function.accept(params);

                return null;
            }

            @Override
            public PyObject call(ThreadState threadState, PyObject pyObject, PyObject pyObject1, PyObject pyObject2, PyObject[] pyObjects, PyObject pyObject3) {
                PyObject[] params = new PyObject[] { pyObject, pyObject1 };
                function.accept(params);

                return null;
            }

            @Override
            public PyObject call(ThreadState threadState, PyObject pyObject, PyObject pyObject1, PyObject pyObject2, PyObject pyObject3, PyObject[] pyObjects, PyObject pyObject4) {
                PyObject[] params = new PyObject[] { pyObject, pyObject1, pyObject2 };
                function.accept(params);

                return null;
            }

            @Override
            public PyObject call(ThreadState threadState, PyObject pyObject, PyObject pyObject1, PyObject pyObject2, PyObject pyObject3, PyObject pyObject4, PyObject[] pyObjects, PyObject pyObject5) {
                PyObject[] params = new PyObject[] { pyObject, pyObject1, pyObject2, pyObject3 };
                function.accept(params);

                return null;
            }
        };
    }

}
