package Radium.Integration.Python;

import Radium.Engine.Math.Transform;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import java.util.ArrayList;
import java.util.List;

public class Allocation {

    public PythonInterpreter python;

    // # of class allocations is how many times the class is available per frame

    private List<PyInteger> integers = new ArrayList<>();
    private List<PyFloat> floats = new ArrayList<>();
    private List<PyString> strings = new ArrayList<>();

    private List<PyObject> vector2 = new ArrayList<>();
    private int vector2Allocations = 128;

    private List<PyObject> vector3 = new ArrayList<>();
    private int vector3Allocations = 128;

    private List<PyObject> transform = new ArrayList<>();
    private int transformAllocations = 64;

    public Allocation(PythonInterpreter python) {
        this.python = python;

        for (int i = 0; i < vector2Allocations; i++) {
            vector2.add(python.get("Vector2").__call__(new PyObject[] {
                    new PyFloat(0),
                    new PyFloat(0)
            }));
        }
        for (int i = 0; i < vector3Allocations; i++) {
            vector3.add(python.get("Vector3").__call__(new PyObject[] {
                    new PyFloat(0),
                    new PyFloat(0),
                    new PyFloat(0)
            }));
        }
        for (int i = 0; i < transformAllocations; i++) {
            transform.add(python.get("Transform").__call__(new PyObject[] {
                    Vector3(0, 0, 0),
                    Vector3(0, 0, 0),
                    Vector3(0, 0, 0)
            }));
        }
    }

    public void Destroy() {
        vector2.forEach(vec -> vec.__delete__(vec));
        vector3.forEach(vec -> vec.__delete__(vec));
        transform.forEach(trs -> trs.__delete__(trs));
    }

    public PyInteger Integer(int value) {
        PyInteger integer = new PyInteger(value);
        integers.add(integer);
        return integer;
    }

    public PyFloat Float(float value) {
        PyFloat flt = new PyFloat(value);
        floats.add(flt);
        return flt;
    }

    public PyString String(String value) {
        PyString str = new PyString(value);
        strings.add(str);
        return str;
    }

    public PyObject Vector2(float x, float y) {
        PyObject vector = vector2.get(0);
        vector2.remove(0);
        vector2.add(vector);

        vector.__setattr__("x", new PyFloat(x));
        vector.__setattr__("y", new PyFloat(y));

        return vector;
    }

    public PyObject Vector2(Vector2 vec) {
        PyObject vector = vector2.get(0);
        vector2.remove(0);
        vector2.add(vector);

        vector.__setattr__("x", new PyFloat(vec.x));
        vector.__setattr__("y", new PyFloat(vec.y));

        return vector;
    }

    public PyObject Vector3(float x, float y, float z) {
        PyObject vector = vector3.get(0);
        vector3.remove(0);
        vector3.add(vector);

        vector.__setattr__("x", new PyFloat(x));
        vector.__setattr__("y", new PyFloat(y));
        vector.__setattr__("z", new PyFloat(z));

        return vector;
    }

    public PyObject Vector3(Vector3 vec) {
        PyObject vector = vector3.get(0);
        vector3.remove(0);
        vector3.add(vector);

        vector.__setattr__("x", new PyFloat(vec.x));
        vector.__setattr__("y", new PyFloat(vec.y));
        vector.__setattr__("z", new PyFloat(vec.z));

        return vector;
    }

    public PyObject Transform(Vector3 position, Vector3 rotation, Vector3 scale) {
        PyObject transform = this.transform.get(0);
        this.transform.remove(0);
        this.transform.add(transform);

        transform.__setattr__("position", Vector3(position));
        transform.__setattr__("rotation", Vector3(rotation));
        transform.__setattr__("scale", Vector3(scale));

        return transform;
    }

    public PyObject Transform(Transform transform) {
        PyObject pyTransform = this.transform.get(0);
        this.transform.remove(0);
        this.transform.add(pyTransform);

        pyTransform.__setattr__("position", Vector3(transform.position));
        pyTransform.__setattr__("rotation", Vector3(transform.rotation));
        pyTransform.__setattr__("scale", Vector3(transform.scale));

        return pyTransform;
    }

}
