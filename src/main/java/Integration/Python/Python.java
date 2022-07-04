package Integration.Python;

import Integration.Project.Project;
import Radium.*;
import Radium.Color.Color;
import Radium.Components.Scripting.PythonScripting;
import Radium.Graphics.Material;
import Radium.Graphics.Mesh;
import Radium.Graphics.Texture;
import Radium.Graphics.Vertex;
import Radium.Input.Input;
import Radium.Input.Keys;
import Radium.Math.Transform;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.Objects.GameObject;
import Radium.SceneManagement.SceneManager;
import Radium.Scripting.Python.PythonScript;
import RadiumEditor.Console;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.text.WordUtils;
import org.python.core.*;
import org.python.util.PythonInterpreter;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Python {

    public transient HashMap<String, PythonFunction> functions = new HashMap<>();

    private PythonInterpreter interpreter;
    private final transient PythonScript script;

    private final List<PythonLibrary> libraries = new ArrayList<>();

    public final List<UserVariable> variables = new ArrayList<>();

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
        List<String> nonVariables = new ArrayList<>();
        StringBuilder srcCode = new StringBuilder();
        for (PythonLibrary library : libraries) {
            srcCode.append(library.content);
            nonVariables.add(library.src.getName().split("[.]")[0]);
        }
        srcCode.append(code);

        interpreter.exec(srcCode.toString());

        nonVariables.add("__builtins__");
        nonVariables.add("__name__");
        nonVariables.add("__package__");
        nonVariables.add("__doc__");
        nonVariables.add("start");
        nonVariables.add("update");

        PyStringMap stringMap = (PyStringMap) interpreter.getLocals();
        Object[] names = stringMap.keys().toArray();
        Object[] values = stringMap.values().toArray();

        variables.clear();
        for (int i = 0; i < names.length; i++) {
            if (nonVariables.contains(names[i].toString()) || values[i] == null) continue;

            if (values[i] instanceof PyObject) {
                PyObject pyObject = (PyObject) values[i];
                if (pyObject.isCallable()) {
                    continue;
                }
            }

            String typeName = values[i].getClass().getSimpleName();
            if (values[i] instanceof PyInstance) {
                typeName = ((PyInstance)values[i]).instclass.__name__;
            }

            UserVariable variable = new UserVariable(names[i].toString(), typeName, values[i]);
            variables.add(variable);
        }

        CreateFunctions();
        CreateVariables();
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

            for (PyObject component : go.__getattr__("components").asIterable()) {
                try {
                    Class<? extends Component> compClass = Component.GetComponentType(component.__getattr__("name").asString());
                    Component instance = compClass.getDeclaredConstructor().newInstance();
                    object.AddComponent(instance);
                } catch (Exception e) {
                    Console.Error("Failed to add component " + component.__getattr__("name").asString());
                }
            }

            go.__setattr__("id", new PyString(object.id));
        }).Define(this);
        new PythonFunction("UPDATE_GAMEOBJECT", 1, (params) -> {
            PyObject go = params[0];

            GameObject obj = GameObject.Find(go.__getattr__("id").asString());
            if (obj == null) {
                Console.Error("GameObject must be instantiated to edit properties");
                return;
            }

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

            if (GameObject.Find(id) == null) {
                Console.Error("GameObject must be instantiated to edit properties");
                return;
            }

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

                    if (value.getType() == PyNone.TYPE) {
                        field.set(componentInstance, null);
                        return;
                    }

                    if (field.getType().isEnum()) {
                        Object val = Enum.valueOf((Class<Enum>)field.getType(), value.asString());
                        field.set(componentInstance, val);
                    } else {
                        switch (field.getType().getSimpleName()) {
                            case "int":
                                field.set(componentInstance, value.asInt());
                                break;
                            case "float":
                                field.set(componentInstance, (float) value.asDouble());
                                break;
                            case "boolean":
                                field.set(componentInstance, ((PyBoolean) value).getBooleanValue());
                                break;
                            case "String":
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
                            case "Material":
                                PyObject mat = value;
                                String path = mat.__getattr__("mainTex").asString();
                                String nor = mat.__getattr__("normalTex").asString();
                                String spec = mat.__getattr__("specularTex").asString();
                                boolean specular = ((PyBoolean)mat.__getattr__("specularLighting")).getBooleanValue();
                                boolean useNormalMap = ((PyBoolean)mat.__getattr__("useNormalMap")).getBooleanValue();
                                boolean useSpecularMap = ((PyBoolean)mat.__getattr__("useSpecularMap")).getBooleanValue();
                                float reflectivity = (float) mat.__getattr__("reflectivity").asDouble();
                                float shineDamper = (float) mat.__getattr__("shineDamper").asDouble();
                                PyObject col = mat.__getattr__("color");
                                Color color = new Color((float) col.__getattr__("r").asDouble(), (float) col.__getattr__("g").asDouble(), (float) col.__getattr__("b").asDouble(), (float)col.__getattr__("a").asDouble());

                                Material material = new Material(path);
                                material.normalMapPath = nor;
                                material.specularMapPath = spec;
                                material.specularLighting = specular;
                                material.useNormalMap = useNormalMap;
                                material.useSpecularMap = useSpecularMap;
                                material.reflectivity = reflectivity;
                                material.shineDamper = shineDamper;
                                material.color = color;
                                material.CreateMaterial();

                                field.set(componentInstance, material);
                                break;
                            case "Mesh":
                                Mesh val = GetMesh(value);
                                field.set(componentInstance, val);
                            case "Texture":
                                String tex = Project.Current().assets + value.asString();
                                field.set(componentInstance, new Texture(tex));
                            case "Color":
                                Color c = GetColor(value);
                                field.set(componentInstance, c);
                        }
                    }

                    componentInstance.UpdateVariable(field.getName());

                    componentInstance.UpdateVariable(attributeName);
                } catch (Exception e) {
                    Console.Error("Unknown Component: " + name);
                    Console.Error(e);
                }
            }
        }).Define(this);
        new PythonFunction("CALL_COMPONENT_METHOD", 2, (params) -> {
            PyObject component = params[0];
            String componentName = component.__getattr__("name").asString();
            String methodName = params[1].asString();

            Class<? extends Component> componentClass = Component.GetComponentType(componentName);
            if (componentClass == null) {
                Console.Error("Unknown Component: " + componentName);
                return;
            }

            PyObject obj = component.__findattr__("gameObject");
            if (obj != null) {
                String id = obj.__getattr__("id").asString();
                GameObject gameObject = GameObject.Find(id);
                if (gameObject != null) {
                    Component componentInstance = gameObject.GetComponent(componentClass);
                    try {
                        componentInstance.getClass().getMethod(methodName).invoke(componentInstance);
                    } catch (Exception e) {
                        Console.Error("Invalid Method: " + methodName);
                    }
                }
            } else {
                Console.Error("Component is not attached to a GameObject: " + componentName);
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
        new PythonFunction("GET_KEYBOARD_INPUT", 2, (params) -> {
            String inputType = params[0].asString();
            String key = params[1].asString();

            if (inputType.equals("down")) {
                boolean val = Input.GetKey(Keys.valueOf(WordUtils.capitalize(key)));
                Return("GET_KEYBOARD_INPUT", new PyBoolean(val));
                return;
            }

            Return("GET_KEYBOARD_INPUT", new PyBoolean(false));
        }).Define(this);
        new PythonFunction("GET_MOUSE_INPUT", 2, (params) -> {
            String inputType = params[0].asString();
            String button = params[1].asString();

            int mb = 0;
            switch (button) {
                case "left":
                    break;
                case "right":
                    mb = 1;
                    break;
                case "middle":
                    mb = 2;
                    break;
            }

            if (inputType.equals("down")) {
                boolean val = Input.GetMouseButton(mb);
                Return("GET_MOUSE_INPUT", new PyBoolean(val));
                return;
            }

            Return("GET_MOUSE_INPUT", new PyBoolean(false));
        }).Define(this);
        new PythonFunction("GET_TIME_PROPERTY", 1, (params) -> {
            String name = params[0].asString();
            if (name.equals("deltaTime")) {
                Return("GET_TIME_PROPERTY", new PyFloat(1.0f / Application.FPS));
                return;
            } else if (name.equals("time")) {
                Return("GET_TIME_PROPERTY", new PyFloat(Time.GetPlayTime()));
                return;
            }

            Return("GET_TIME_PROPERTY", new PyFloat(0));
        }).Define(this);
        new PythonFunction("GET_SCRIPT_VAR", 3, (params) -> {
            String gid = params[0].asString();
            String scriptName = params[1].asString();
            String varName = params[2].asString();

            GameObject obj = GameObject.Find(gid);
            if (obj == null) {
                Console.Error("GameObject not found: " + gid);
                return;
            }

            PythonScripting scripting = obj.GetComponent(PythonScripting.class);
            if (scripting == null) {
                Console.Error("GameObject does not have a PythonScripting component: " + gid);
                return;
            }

            PythonScript script = scripting.Find(scriptName);
            if (script == null) {
                Console.Error("Script not found: " + scriptName);
                return;
            }

            PyObject var = script.python.GetInterpreter().get(varName);
            if (var == null) {
                Console.Error("Variable not found: " + varName);
                return;
            }

            Return("GET_SCRIPT_VAR", var);
        }).Define(this);
        new PythonFunction("CALL_SCRIPT_METHOD", 3, (params) -> {
            String gid = params[0].asString();
            String scriptName = params[1].asString();
            String methodName = params[2].asString();

            GameObject obj = GameObject.Find(gid);
            if (obj == null) {
                Console.Error("GameObject not found: " + gid);
                return;
            }

            PythonScripting scripting = obj.GetComponent(PythonScripting.class);
            if (scripting == null) {
                Console.Error("GameObject does not have a PythonScripting component: " + gid);
                return;
            }

            PythonScript script = scripting.Find(scriptName);
            if (script == null) {
                Console.Error("Script not found: " + scriptName);
                return;
            }

            PyObject method = script.python.GetInterpreter().get(methodName);
            if (method == null) {
                Console.Error("Method not found: " + methodName);
                return;
            }

            PyObject res = method.__call__();
            Return("CALL_SCRIPT_METHOD", res);
        }).Define(this);
        new PythonFunction("GET_SCRIPT", 2, (params) -> {
            String gid = params[0].asString();
            String scriptName = params[1].asString();

            GameObject obj = GameObject.Find(gid);
            if (obj == null) {
                Console.Error("GameObject not found: " + gid);
                return;
            }

            PythonScripting scripting = obj.GetComponent(PythonScripting.class);
            if (scripting == null) {
                Console.Error("GameObject does not have a PythonScripting component: " + gid);
                return;
            }

            PythonScript script = scripting.Find(scriptName);
            if (script == null) {
                Console.Error("Script not found: " + scriptName);
                return;
            }

            PyObject pyscript = script.python.GetInterpreter().get("Script").__call__(new PyString(gid), new PyString(scriptName));
            Return("GET_SCRIPT", pyscript);
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
                        case "String":
                            componentInstance.__setattr__(field.getName(), new PyString((String) field.get(component)));
                            break;
                        case "Vector3":
                            componentInstance.__setattr__(field.getName(), CreateVector3((Vector3) field.get(component)));
                            break;
                        case "Vector2":
                            componentInstance.__setattr__(field.getName(), CreateVector2((Vector2) field.get(component)));
                            break;
                        case "Texture":
                            componentInstance.__setattr__(field.getName(), new PyString(((Texture) field.get(component)).filepath));
                            break;
                        case "Material":
                            Material mat = (Material) field.get(component);

                            PyObject matInstance = componentInstance.__getattr__(field.getName());
                            matInstance.__setattr__("mainTex", new PyString(mat.path));
                            matInstance.__setattr__("normalTex", new PyString(mat.normalMapPath));
                            matInstance.__setattr__("specularTex", new PyString(mat.specularMapPath));
                            matInstance.__setattr__("specularLighting", new PyBoolean(mat.specularLighting));
                            matInstance.__setattr__("useNormalMap", new PyBoolean(mat.useNormalMap));
                            matInstance.__setattr__("useSpecularMap", new PyBoolean(mat.useSpecularMap));
                            matInstance.__setattr__("reflectivity", new PyFloat(mat.reflectivity));
                            matInstance.__setattr__("shineDamper", new PyFloat(mat.shineDamper));

                            PyObject color = matInstance.__getattr__("color");
                            color.__setattr__("r", new PyFloat(mat.color.r));
                            color.__setattr__("g", new PyFloat(mat.color.g));
                            color.__setattr__("b", new PyFloat(mat.color.b));
                            color.__setattr__("a", new PyFloat(mat.color.a));
                            matInstance.__setattr__("color", color);

                            break;
                    }
                } catch (Exception e) {
                    Console.Error("Failed to set field: " + field.getName());
                }
            }
        }

        return componentInstance;
    }

    private Mesh GetMesh(PyObject obj) {
        PyArray vertices = (PyArray) obj.__getattr__("vertices");
        PyArray uvs = (PyArray) obj.__getattr__("uvs");
        PyArray normals = (PyArray) obj.__getattr__("normals");
        PyArray tangents = (PyArray) obj.__getattr__("tangents");
        PyArray bitangents = (PyArray) obj.__getattr__("bitangents");
        PyArray indices = (PyArray) obj.__getattr__("indices");

        Vertex[] radiumVertices = new Vertex[vertices.__len__()];
        for (int i = 0; i < vertices.__len__(); i++) {
            Vector3 vertex = GetVector3(vertices.__getitem__(i));
            Vector2 uv = GetVector2(uvs.__getitem__(i));
            Vector3 normal = GetVector3(normals.__getitem__(i));
            Vector3 tangent = GetVector3(tangents.__getitem__(i));
            Vector3 bitangent = GetVector3(bitangents.__getitem__(i));

            Vertex radVertex = new Vertex(vertex, normal, uv);
            radVertex.SetTangent(tangent);
            radVertex.SetBitangent(bitangent);
            radiumVertices[i] = radVertex;
        }

        int[] radiumIndices = new int[indices.__len__()];
        for (int i = 0; i < indices.__len__(); i++) {
            radiumIndices[i] = indices.__getitem__(i).asInt();
        }

        Mesh mesh = new Mesh(radiumVertices, radiumIndices);
        return mesh;
    }

    private Color GetColor(PyObject obj) {
        int r = obj.__getattr__("r").asInt();
        int g = obj.__getattr__("r").asInt();
        int b = obj.__getattr__("r").asInt();
        int a = obj.__getattr__("r").asInt();
        return new Color(r, g, b, a);
    }

    private PyObject CreateColor(Color color) {
        return interpreter.get("Color").__call__(new PyFloat(color.r), new PyFloat(color.g), new PyFloat(color.b), new PyFloat(color.a));
    }

    private Vector2 GetVector2(PyObject obj) {
        return new Vector2((float) obj.__getattr__("x").asDouble(), (float) obj.__getattr__("y").asDouble());
    }

    private Vector3 GetVector3(PyObject obj) {
        return new Vector3((float) obj.__getattr__("x").asDouble(), (float) obj.__getattr__("y").asDouble(), (float) obj.__getattr__("z").asDouble());
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
