package Integration.Python;

import Integration.API.API;
import Integration.Project.Project;
import Radium.*;
import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.MeshRenderer;
import Radium.Components.Particles.ParticleSystem;
import Radium.Components.Physics.Rigidbody;
import Radium.Components.Rendering.Camera;
import Radium.Components.Rendering.Light;
import Radium.Components.UI.Button;
import Radium.Components.UI.Image;
import Radium.Components.UI.Text;
import Radium.Graphics.Lighting.LightType;
import Radium.Graphics.Material;
import Radium.Graphics.Texture;
import Radium.Input.Input;
import Radium.Input.Keys;
import Radium.Math.Transform;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.Objects.GameObject;
import Radium.Physics.ColliderType;
import Radium.SceneManagement.Scene;
import Radium.SceneManagement.SceneManager;
import Radium.Scripting.Python.PythonScript;
import Radium.System.FileExplorer;
import Radium.Util.EnumUtility;
import RadiumEditor.Console;
import RadiumEditor.Viewport;
import org.json.simple.JSONObject;
import org.python.core.*;
import org.python.util.PythonInterpreter;

import javax.tools.JavaCompiler;
import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Python {

    public transient HashMap<String, PythonFunction> functions = new HashMap<>();

    private PythonInterpreter interpreter;
    private transient PythonScript script;

    private List<PythonLibrary> libraries = new ArrayList<>();

    private PythonVariable gameObject;

    private PyObject go;
    private PyObject transform;

    public Python(PythonScript script) {
        this.script = script;
    }

    public void Initialize() {
        if (interpreter != null) interpreter.close();
        interpreter = new PythonInterpreter();
        interpreter.setErr(OutputStream.nullOutputStream());
    }

    public void AddLibrary(PythonLibrary library) {
        libraries.add(library);
    }

    public void Execute(String code) {
        String srcCode = "";
        for (PythonLibrary library : libraries) {
            srcCode += library.content;
        }
        srcCode += code;

        interpreter.exec(srcCode);
        CreateFunctions();
        CreateVariables();
    }

    public void Execute(File f) {
        try {
            interpreter.execfile(f.getPath());
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public void Update() {
        UpdateGameObject();
    }

    private void UpdateGameObject() {
        transform.__setattr__("position", CreateVector3(script.gameObject.transform.localPosition));
        transform.__setattr__("rotation", CreateVector3(script.gameObject.transform.localRotation));
        transform.__setattr__("scale", CreateVector3(script.gameObject.transform.localScale));

        go.__setattr__("name", new PyString(script.gameObject.name));
        go.__setattr__("transform", transform);
    }

    private void CreateFunctions() {
        new PythonFunction("log", 1, (params) -> {
            Console.Log(params[0].toString());
        }).Define(this);

        new PythonFunction("instantiate", 1, (params) -> {
            PyObject go = params[0];

            String name = go.__getattr__("name").toString();

            PyObject transform = go.__getattr__("transform");
            PyObject position = transform.__getattr__("position");
            PyObject rotation = transform.__getattr__("rotation");
            PyObject scale = transform.__getattr__("scale");
            Vector3 pos = new Vector3((float)position.__getattr__("x").asDouble(), (float)position.__getattr__("y").asDouble(), (float)position.__getattr__("z").asDouble());
            Vector3 rot = new Vector3((float)rotation.__getattr__("x").asDouble(), (float)rotation.__getattr__("y").asDouble(), (float)rotation.__getattr__("z").asDouble());
            Vector3 sca = new Vector3((float)scale.__getattr__("x").asDouble(), (float)scale.__getattr__("y").asDouble(), (float)scale.__getattr__("z").asDouble());

            GameObject object = new GameObject();
            object.name = name;
            object.transform.localPosition = pos;
            object.transform.localRotation = rot;
            object.transform.localScale = sca;
        }).Define(this);
        new PythonFunction("UPDATE_GAMEOBJECT", 1, (params) -> {
            PyObject go = params[0];

            String id = go.__getattr__("id").toString();
            boolean destroyed = ((PyBoolean)go.__getattr__("destroyed")).getBooleanValue();
            if (destroyed) {
                GameObject.Find(id).Destroy();
                return;
            }

            String name = go.__getattr__("name").toString();

            PyObject parent = go.__findattr__("parent");
            if (parent != null && parent.__findattr__("id") != null) {
                String parentID = parent.__getattr__("id").toString();
                script.gameObject.SetParent(GameObject.Find(parentID));
            }

            PyObject transform = go.__getattr__("transform");
            PyObject position = transform.__getattr__("position");
            PyObject rotation = transform.__getattr__("rotation");
            PyObject scale = transform.__getattr__("scale");
            Vector3 pos = new Vector3((float)position.__getattr__("x").asDouble(), (float)position.__getattr__("y").asDouble(), (float)position.__getattr__("z").asDouble());
            Vector3 rot = new Vector3((float)rotation.__getattr__("x").asDouble(), (float)rotation.__getattr__("y").asDouble(), (float)rotation.__getattr__("z").asDouble());
            Vector3 sca = new Vector3((float)scale.__getattr__("x").asDouble(), (float)scale.__getattr__("y").asDouble(), (float)scale.__getattr__("z").asDouble());

            GameObject object = GameObject.Find(id);
            if (object != null) {
                object.name = name;
                object.transform.localPosition = pos;
                object.transform.localRotation = rot;
                object.transform.localScale = sca;
            }
        }).Define(this);
        new PythonFunction("SET_COMPONENT_ATTRIBUTE", 3, (params) -> {
            PyObject component = params[0];
            String attributeName = params[1].asString();
            PyObject value = params[2];

            String name = component.__getattr__("name").toString();
            String id = component.__getattr__("gameObject").__getattr__("id").toString();

            if (attributeName.equals("NEW_COMPONENT")) {
                Class<? extends Component> compClass = Component.GetComponentType(value.asString());
                if (compClass != null) {
                    try {
                        Component instance = compClass.getDeclaredConstructor().newInstance();
                        GameObject.Find(id).AddComponent(instance);
                    } catch (Exception e) {
                        Console.Error("Failed to create component instance of " + value.asString());
                    }
                }

                return;
            }
            if (attributeName.equals("REMOVE_COMPONENT")) {
                Class<? extends Component> compClass = Component.GetComponentType(value.asString());
                if (compClass != null) {
                    GameObject.Find(id).RemoveComponent(compClass);
                }

                return;
            }

            GameObject object = GameObject.Find(id);
            if (object != null) {
                try {
                    Class<? extends Component> comp = Component.GetComponentType(name);
                    Component componentInstance = object.GetComponent(comp);
                    Field field = componentInstance.getClass().getField(attributeName);

                    if (field.getType().isEnum()) {
                        field.set(componentInstance, Enum.valueOf((Class<Enum>)field.getType(), value.asString()));
                    } else {
                        switch (field.getType().getName()) {
                            case "int":
                                field.set(componentInstance, value.asInt());
                                break;
                            case "float":
                                field.set(componentInstance, (float) value.asDouble());
                                break;
                            case "boolean":
                                field.set(componentInstance, ((PyBoolean) value).getBooleanValue());
                                break;
                            case "string":
                                field.set(componentInstance, value.asString());
                                break;
                            case "Vector3":
                                PyObject vec = value;
                                Vector3 vec3 = new Vector3((float) vec.__getattr__("x").asDouble(), (float) vec.__getattr__("y").asDouble(), (float) vec.__getattr__("z").asDouble());
                                field.set(componentInstance, vec3);
                                break;
                            case "Vector2":
                                PyObject vec2 = value;
                                Vector2 vec2d = new Vector2((float) vec2.__getattr__("x").asDouble(), (float) vec2.__getattr__("y").asDouble());
                                field.set(componentInstance, vec2d);
                                break;
                        }
                    }

                    componentInstance.UpdateVariable(attributeName);
                } catch (Exception e) {
                    Console.Error("Unknown Component: " + name);
                }
            }
        }).Define(this);
        new PythonFunction("GET_ENGINE_COMPONENT", 1, (params) -> {
            String componentName = params[0].asString();
            Class<? extends Component> comp = Component.GetComponentType(componentName);
            if (comp != null) {
                Return("GET_ENGINE_COMPONENT", CreateComponent(script.gameObject.GetComponent(comp)));
            }
        }).Define(this);
        new PythonFunction("GET_ENGINE_OBJECTS", 0, (params) -> {
            PyList list = new PyList();
            for (GameObject obj : SceneManager.GetCurrentScene().gameObjectsInScene) {
                list.append(CreateGameObject(obj));
            }

            Return("GET_ENGINE_OBJECTS", list);
        }).Define(this);
    }

    private void CreateVariables() {
        go = interpreter.get("GameObject").__call__();
        go.__setattr__("name", new PyString(script.gameObject.name));
        go.__setattr__("id", new PyString(script.gameObject.id));

        transform = interpreter.get("Transform").__call__(new PyObject[] {
                CreateVector3(script.gameObject.transform.localPosition),
                CreateVector3(script.gameObject.transform.localRotation),
                CreateVector3(script.gameObject.transform.localScale)
        });
        go.__setattr__("transform", transform);
        if (script.gameObject.GetParent() != null) {
            go.__setattr__("parent", CreateGameObject(script.gameObject.GetParent()));
        }

        new PythonVariable("gameObject", go).Define(this);
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

    private PyObject CreateVector2(Vector2 vec) {
        return interpreter.get("Vector2").__call__(new PyObject[] {
                new PyFloat(vec.x),
                new PyFloat(vec.y)
        });
    }

    private PyObject CreateVector3(Vector3 vec) {
        PyObject vec3 = interpreter.get("Vector3").__call__(new PyObject[] {
                new PyFloat(vec.x),
                new PyFloat(vec.y),
                new PyFloat(vec.z)
        });

        return vec3;
    }

    private PyObject CreateTransform(Transform transform) {
        PyObject t = interpreter.get("Transform").__call__(new PyObject[] {
                CreateVector3(transform.localPosition),
                CreateVector3(transform.localRotation),
                CreateVector3(transform.localScale)
        });

        return t;
    }

    private PyObject CreateComponent(Component component) {
        if (component == null) {
            return null;
        }

        String name = component.name.replace(" ", "");
        PyObject componentInstance = interpreter.get(name).__call__();
        componentInstance.__setattr__("gameObject", go);
        componentInstance.__setattr__("order", new PyInteger(component.order));
        componentInstance.__setattr__("enabled", new PyBoolean(component.enabled));

        for (Field field : component.getClass().getDeclaredFields()) {
            PyObject pyField = componentInstance.__findattr__(field.getName());
            if (pyField != null) {
                try {
                    switch (field.getType().getSimpleName()) {
                        case "int":
                            componentInstance.__setattr__(field.getName(), new PyInteger((int) field.get(component)));
                            break;
                        case "float":
                            componentInstance.__setattr__(field.getName(), new PyFloat((float) field.get(component)));
                            break;
                        case "boolean":
                            componentInstance.__setattr__(field.getName(), new PyBoolean((boolean) field.get(component)));
                            break;
                        case "string":
                            componentInstance.__setattr__(field.getName(), new PyString((String) field.get(component)));
                            break;
                        case "Vector3":
                            componentInstance.__setattr__(field.getName(), CreateVector3((Vector3) field.get(component)));
                            break;
                        case "Vector2":
                            componentInstance.__setattr__(field.getName(), CreateVector2((Vector2) field.get(component)));
                            break;
                    }
                } catch (Exception e) {
                    Console.Error("Failed to set field: " + field.getName());
                }
            }
        }

        return componentInstance;
    }

    private PyObject CreateGameObject(GameObject go) {
        PyObject newGO = interpreter.get("GameObject").__call__();
        newGO.__setattr__("name", new PyString(go.name));
        newGO.__setattr__("transform", CreateTransform(go.transform));
        newGO.__setattr__("id", new PyString(go.id));

        if (go.GetParent() != null) {
            newGO.__setattr__("parent", CreateGameObject(go.GetParent()));
        }

        return newGO;
    }

    private void Return(String functionName, PyObject value) {
        functions.get(functionName).returnObject = value;
    }

}
