package Radium.Integration.Python;

import org.python.core.PyInstance;
import org.python.core.PyObject;

import java.util.Objects;

public class UserVariable {

    public String name;
    public String type;
    public Object value;

    public UserVariable(String name, String type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public void Set(Object value, Python python) {
        this.value = value;

        python.GetInterpreter().set(name, value);
    }

    public void SetProperty(String name, PyObject value) {
        PyInstance instance = (PyInstance)this.value;
        instance.__setattr__(name, value);
    }

    public void UpdateProperties(Python python) {
        python.GetInterpreter().set(name, value);
    }

    public float GetFloat(String name) {
        return (float)((PyInstance)value).__getattr__(name).asDouble();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return name.equals(((UserVariable) o).name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, value);
    }

}
