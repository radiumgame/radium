package Radium.Engine.Scripting.Node;

import Radium.Editor.Console;
import Radium.Editor.EditorGUI;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.Scripting.Node.IO.NodeIO;
import imgui.ImGui;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Nodes {

    private static HashMap<String, Integer> NodeIcons = new HashMap<>();

    protected Nodes() {}

    //region Events
    public static Node Start() {
        Node start = new Node("Start");
        start.AddOutput(NodeIO.EventOutput());

        return start;
    }

    public static Node Update() {
        Node update = new Node("Update");
        update.AddOutput(NodeIO.EventOutput());

        return update;
    }
    //endregion

    //region Console
    public static Node Log() {
        Node log = new Node("Log");
        log.AddInput(NodeIO.EventInput());
        log.AddInput(NodeIO.ObjectInput("Message"));
        log.AddOutput(NodeIO.EventOutput());
        log.SetAction((inputs, outputs) -> {
            Console.Log(inputs.get(1).value);
        });
        log.SetIcon("EngineAssets/Editor/Console/log.png");

        return log;
    }

    public static Node Warn() {
        Node warn = new Node("Warn");
        warn.AddInput(NodeIO.EventInput());
        warn.AddInput(NodeIO.ObjectInput("Warning"));
        warn.AddOutput(NodeIO.EventOutput());
        warn.SetAction((inputs, outputs) -> {
            Console.Warning(inputs.get(1).value);
        });
        warn.SetIcon("EngineAssets/Editor/Console/warning.png");

        return warn;
    }

    public static Node Error() {
        Node error = new Node("Error");
        error.AddInput(NodeIO.EventInput());
        error.AddInput(NodeIO.ObjectInput("Error"));
        error.AddOutput(NodeIO.EventOutput());
        error.SetAction((inputs, outputs) -> {
            Console.Error(inputs.get(1).value);
        });
        error.SetIcon("EngineAssets/Editor/Console/error.png");

        return error;
    }
    //endregion

    //region Data Types
    public static Node Int() {
        Node integer = new Node("Int");
        integer.AddOutput(NodeIO.IntOutput("Int"));
        integer.SetGUI((inputs, outputs) -> {
            ImGui.setNextItemWidth(125);
            int val = EditorGUI.DragInt("##NODE_INT_SLIDER_" + integer.hashCode(), (int)outputs.get(0).value);
            if (((int)outputs.get(0).value) != val) {
                outputs.get(0).value = val;
                integer.UpdateValue(outputs.get(0));
            }
        });

        return integer;
    }

    public static Node Float() {
        Node aFloat = new Node("Float");
        aFloat.AddOutput(NodeIO.FloatOutput("Float"));
        aFloat.SetGUI((inputs, outputs) -> {
            ImGui.setNextItemWidth(125);
            float val = EditorGUI.DragFloat("##NODE_FLOAT_SLIDER" + aFloat.hashCode(), (float)outputs.get(0).value);
            if (((float)outputs.get(0).value) != val) {
                outputs.get(0).value = val;
                aFloat.UpdateValue(outputs.get(0));
            }
        });

        return aFloat;
    }

    public static Node String() {
        Node string = new Node("String");
        string.AddOutput(NodeIO.StringOutput("String"));
        string.SetGUI((inputs, outputs) -> {
            ImGui.setNextItemWidth(125);
            String val = EditorGUI.InputString("##STRING_INPUT_NODE_" + string.hashCode(), (String)outputs.get(0).value);
            if (!((String)outputs.get(0).value).equals(val)) {
                outputs.get(0).value = val;
                string.UpdateValue(outputs.get(0));
            }
        });

        return string;
    }

    public static Node Vector2() {
        Node vector2 = new Node("Vector2");
        vector2.AddOutput(NodeIO.Vector2Output("Vector3"));
        vector2.SetGUI((inputs, outputs) -> {
            ImGui.setNextItemWidth(175);
            Vector2 val = EditorGUI.DragVector2("##VECTOR2_INPUT_NODE_" + vector2.hashCode(), (Vector2)outputs.get(0).value);
            if (!val.equals(outputs.get(0))) {
                outputs.get(0).value = val;
                vector2.UpdateValue(outputs.get(0));
            }
        });

        return vector2;
    }

    public static Node Vector3() {
        Node vector3 = new Node("Vector3");
        vector3.AddOutput(NodeIO.Vector3Output("Vector3"));
        vector3.SetGUI((inputs, outputs) -> {
            ImGui.setNextItemWidth(175);
            Vector3 val = EditorGUI.DragVector3("##VECTOR3_INPUT_NODE_" + vector3.hashCode(), (Vector3)outputs.get(0).value);
            if (!val.equals(outputs.get(0))) {
                outputs.get(0).value = val;
                vector3.UpdateValue(outputs.get(0));
            }
        });

        return vector3;
    }
    //endregion

    //region Math

    public static Node Add() {
        Node add = new Node("Add");
        add.AddInput(NodeIO.EventInput());
        add.AddInput(NodeIO.FloatInput("X"));
        add.AddInput(NodeIO.FloatInput("Y"));
        add.AddOutput(NodeIO.EventOutput());
        add.AddOutput(NodeIO.FloatOutput("Sum"));
        add.SetAction((inputs, outputs) -> {
            float val1 = (float) inputs.get(1).value;
            float val2 = (float) inputs.get(2).value;

            outputs.get(1).value = val1 + val2;
            add.UpdateValue(outputs.get(1));
        });
        add.SetIcon("EngineAssets/Editor/NodeEditor/Math/add.png");

        return add;
    }

    public static Node Subtract() {
        Node subtract = new Node("Subtract");
        subtract.AddInput(NodeIO.EventInput());
        subtract.AddInput(NodeIO.FloatInput("X"));
        subtract.AddInput(NodeIO.FloatInput("Y"));
        subtract.AddOutput(NodeIO.EventOutput());
        subtract.AddOutput(NodeIO.FloatOutput("Difference"));
        subtract.SetAction((inputs, outputs) -> {
            float val1 = (float) inputs.get(1).value;
            float val2 = (float) inputs.get(2).value;

            outputs.get(1).value = val1 - val2;
            subtract.UpdateValue(outputs.get(1));
        });
        subtract.SetIcon("EngineAssets/Editor/NodeEditor/Math/minus.png");

        return subtract;
    }

    public static Node Multiply() {
        Node multiply = new Node("Multiply");
        multiply.AddInput(NodeIO.EventInput());
        multiply.AddInput(NodeIO.FloatInput("X"));
        multiply.AddInput(NodeIO.FloatInput("Y"));
        multiply.AddOutput(NodeIO.EventOutput());
        multiply.AddOutput(NodeIO.FloatOutput("Product"));
        multiply.SetAction((inputs, outputs) -> {
            float val1 = (float) inputs.get(1).value;
            float val2 = (float) inputs.get(2).value;

            outputs.get(1).value = val1 * val2;
            multiply.UpdateValue(outputs.get(1));
        });
        multiply.SetIcon("EngineAssets/Editor/NodeEditor/Math/multiply.png");

        return multiply;
    }

    public static Node Divide() {
        Node divide = new Node("Divide");
        divide.AddInput(NodeIO.EventInput());
        divide.AddInput(NodeIO.FloatInput("X"));
        divide.AddInput(NodeIO.FloatInput("Y"));
        divide.AddOutput(NodeIO.EventOutput());
        divide.AddOutput(NodeIO.FloatOutput("Quotient"));
        divide.SetAction((inputs, outputs) -> {
            float val1 = (float) inputs.get(1).value;
            float val2 = (float) inputs.get(2).value;

            outputs.get(1).value = val1 / val2;
            divide.UpdateValue(outputs.get(1));
        });
        divide.SetIcon("EngineAssets/Editor/NodeEditor/Math/divide.png");

        return divide;
    }

    //endregion

    //region Transform

    public static Node Translate() {
        Node translate = new Node("Translate");
        translate.AddInput(NodeIO.EventInput());
        translate.AddInput(NodeIO.GameObjectInput("Object"));
        translate.AddInput(NodeIO.Vector3Input("Translation"));
        translate.AddOutput(NodeIO.EventOutput());
        translate.SetAction((inputs, outputs) -> {
           Object obj = inputs.get(1).value;
           GameObject go = null;
           if (!((String)obj).isBlank()) {
               go = GameObject.Find((String)obj);
           } else {
               go = translate.GetGameObject();
           }

           Vector3 translation = (Vector3)inputs.get(2).value;
           go.transform.localPosition.Add(translation);
        });

        return translate;
    }

    public static Node Rotate() {
        Node rotate = new Node("Rotate");
        rotate.AddInput(NodeIO.EventInput());
        rotate.AddInput(NodeIO.GameObjectInput("Object"));
        rotate.AddInput(NodeIO.Vector3Input("Rotation"));
        rotate.AddOutput(NodeIO.EventOutput());
        rotate.SetAction((inputs, outputs) -> {
            Object obj = inputs.get(1).value;
            GameObject go = null;
            if (!((String)obj).isBlank()) {
                go = GameObject.Find((String)obj);
            } else {
                go = rotate.GetGameObject();
            }

            Vector3 rotation = (Vector3)inputs.get(2).value;
            go.transform.localRotation.Add(rotation);
        });

        return rotate;
    }

    public static Node Scale() {
        Node scale = new Node("Scale");
        scale.AddInput(NodeIO.EventInput());
        scale.AddInput(NodeIO.GameObjectInput("Object"));
        scale.AddInput(NodeIO.Vector3Input("Scaling"));
        scale.AddOutput(NodeIO.EventOutput());
        scale.SetAction((inputs, outputs) -> {
            Object obj = inputs.get(1).value;
            GameObject go = null;
            if (!((String)obj).isBlank()) {
                go = GameObject.Find((String)obj);
            } else {
                go = scale.GetGameObject();
            }

            Vector3 scaling = (Vector3)inputs.get(2).value;
            go.transform.localScale.Multiply(scaling);
        });

        return scale;
    }

    public static Node SetPosition() {
        Node setPosition = new Node("Set Position");
        setPosition.AddInput(NodeIO.EventInput());
        setPosition.AddInput(NodeIO.GameObjectInput("Object"));
        setPosition.AddInput(NodeIO.Vector3Input("Position"));
        setPosition.AddOutput(NodeIO.EventOutput());
        setPosition.SetAction((inputs, outputs) -> {
            Object obj = inputs.get(1).value;
            GameObject go = null;
            if (!((String)obj).isBlank()) {
                go = GameObject.Find((String)obj);
            } else {
                go = setPosition.GetGameObject();
            }

            Vector3 position = (Vector3)inputs.get(2).value;
            go.transform.localPosition = position;
        });

        return setPosition;
    }

    public static Node SetRotation() {
        Node setRotation = new Node("Set Rotation");
        setRotation.AddInput(NodeIO.EventInput());
        setRotation.AddInput(NodeIO.GameObjectInput("Object"));
        setRotation.AddInput(NodeIO.Vector3Input("Rotation"));
        setRotation.AddOutput(NodeIO.EventOutput());
        setRotation.SetAction((inputs, outputs) -> {
            Object obj = inputs.get(1).value;
            GameObject go = null;
            if (!((String)obj).isBlank()) {
                go = GameObject.Find((String)obj);
            } else {
                go = setRotation.GetGameObject();
            }

            Vector3 rotation = (Vector3)inputs.get(2).value;
            go.transform.localRotation = rotation;
        });

        return setRotation;
    }

    public static Node SetScale() {
        Node setScale = new Node("Set Scale");
        setScale.AddInput(NodeIO.EventInput());
        setScale.AddInput(NodeIO.GameObjectInput("Object"));
        setScale.AddInput(NodeIO.Vector3Input("Scale"));
        setScale.AddOutput(NodeIO.EventOutput());
        setScale.SetAction((inputs, outputs) -> {
            Object obj = inputs.get(1).value;
            GameObject go = null;
            if (!((String)obj).isBlank()) {
                go = GameObject.Find((String)obj);
            } else {
                go = setScale.GetGameObject();
            }

            Vector3 scale = (Vector3)inputs.get(2).value;
            go.transform.localScale = scale;
        });

        return setScale;
    }

    public static Node GetPosition() {
        Node getPosition = new Node("Get Position");
        getPosition.AddInput(NodeIO.EventInput());
        getPosition.AddOutput(NodeIO.EventOutput());
        getPosition.AddOutput(NodeIO.Vector3Output("Value"));
        getPosition.SetAction((inputs, outputs) -> {
            outputs.get(1).value = getPosition.GetGameObject().transform.localPosition;
            getPosition.UpdateValue(outputs.get(1));
        });

        return getPosition;
    }

    public static Node GetRotation() {
        Node getRotation = new Node("Get Rotation");
        getRotation.AddInput(NodeIO.EventInput());
        getRotation.AddOutput(NodeIO.EventOutput());
        getRotation.AddOutput(NodeIO.Vector3Output("Value"));
        getRotation.SetAction((inputs, outputs) -> {
            outputs.get(1).value = getRotation.GetGameObject().transform.localRotation;
            getRotation.UpdateValue(outputs.get(1));
        });

        return getRotation;
    }

    public static Node GetScale() {
        Node getScale = new Node("Get Scale");
        getScale.AddInput(NodeIO.EventInput());
        getScale.AddOutput(NodeIO.EventOutput());
        getScale.AddOutput(NodeIO.Vector3Output("Value"));
        getScale.SetAction((inputs, outputs) -> {
            outputs.get(1).value = getScale.GetGameObject().transform.localScale;
            getScale.UpdateValue(outputs.get(1));
        });

        return getScale;
    }

    //endregion

    //region Non Nodes
    public static void GetIcons() {
        try {
            Method[] methods = Nodes.class.getMethods();
            for (Method method : methods) {
                if (method.getName().equals("GetIcons") ||
                        method.getName().equals("GetIcon") ||
                        !Modifier.isStatic(method.getModifiers()))
                    continue;

                Node node = (Node) method.invoke(null);
                NodeIcons.put(node.name, node.Icon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int GetIcon(String name) {
        return NodeIcons.getOrDefault(name, -1);
    }
    //endregion

}
