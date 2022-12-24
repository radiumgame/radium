package Radium.Integration.Python;

import Radium.Engine.Input.Input;
import org.python.core.PyObject;

import java.util.HashMap;

public class PythonData {

    private Allocation allocation;
    private HashMap<String, PyObject> data = new HashMap<>();

    public PythonData(Allocation allocation) {
        this.allocation = allocation;
    }

    public PyObject GetData(String key) {
        if (data.containsKey(key)) return data.get(key);

        if (key.equals("MOUSE_X")) {
            return allocation.Float((float)Input.GetMouseX());
        } else if (key.equals("MOUSE_Y")) {
            return allocation.Float((float)Input.GetMouseY());
        } else if (key.equals("MOUSE_DELTA_X")) {
            return allocation.Float((float)Input.GetMouseDeltaX());
        } else if (key.equals("MOUSE_DELTA_Y")) {
            return allocation.Float((float)Input.GetMouseDeltaY());
        }

        return new PyObject();
    }

}
