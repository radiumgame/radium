package Radium.Scripting;

import Radium.Math.Vector.Vector3;
import Radium.Time;
import RadiumEditor.Console;

import java.util.function.Consumer;

public class NodeAction {

    protected NodeAction() {}

    public static Consumer<NodeScript> ActionFromType(ScriptingNode node) {
        switch (node.nodeType) {
            case Add -> {
                return (script) -> {
                    float a = (float)node.inputs.get(1).object;
                    float b = (float)node.inputs.get(2).object;
                    node.outputs.get(1).object = a + b;
                };
            }
            case Subtract -> {
                return (script) -> {
                    float a = (float)node.inputs.get(1).object;
                    float b = (float)node.inputs.get(2).object;
                    node.outputs.get(1).object = a - b;
                };
            }
            case Multiply -> {
                return (script) -> {
                    float a = (float)node.inputs.get(1).object;
                    float b = (float)node.inputs.get(2).object;
                    node.outputs.get(1).object = a * b;
                };
            }
            case Divide -> {
                return (script) -> {
                    float a = (float)node.inputs.get(1).object;
                    float b = (float)node.inputs.get(2).object;
                    node.outputs.get(1).object = a / b;
                };
            }
            case Log -> {
                return (script) -> {
                    Console.Log(node.inputs.get(1).object);
                };
            }
            case SetPosition -> {
                return (script) -> {
                    node.gameObject.transform.localPosition = (Vector3)node.inputs.get(1).object;
                };
            }
            case SetRotation -> {
                return (script) -> {
                    node.gameObject.transform.localRotation = (Vector3)node.inputs.get(1).object;
                };
            }
            case SetScale -> {
                return (script) -> {
                    node.gameObject.transform.localScale = (Vector3)node.inputs.get(1).object;
                };
            }
        }

        return (script) -> {};
    }

    public static Consumer<NodeScript> StartFromType(ScriptingNode node) {
        switch (node.nodeType) {
            case Vector3Component -> {
                Vector3 vector = (Vector3)node.inputs.get(0).object;
                if (vector == null) {
                    return (script) -> {};
                }

                return (script) -> {
                    node.outputs.get(0).object = vector.x;
                    node.outputs.get(1).object = vector.y;
                    node.outputs.get(2).object = vector.z;
                };
            }
            case Position -> {
                return (script) -> {
                    node.outputs.get(0).object = node.gameObject.transform.localPosition;
                };
            }
        }

        return (script) -> {};
    }

    public static Consumer<NodeScript> UpdateFromType(ScriptingNode node) {
        switch (node.nodeType) {
            case Time: {
                return (script) -> {
                    node.outputs.get(0).object = Time.deltaTime;
                };
            }
            case Vector3Component: {
                Vector3 vector = (Vector3)node.inputs.get(0).object;
                if (vector == null) {
                    return (script) -> {};
                }

                return (script) -> {
                    node.outputs.get(0).object = vector.x;
                    node.outputs.get(1).object = vector.y;
                    node.outputs.get(2).object = vector.z;
                };
            }
            case Position: {
                return (script) -> {
                    node.outputs.get(0).object = node.gameObject.transform.localPosition;
                };
            }
            case Rotation: {
                return (script) -> {
                    node.outputs.get(0).object = node.gameObject.transform.localRotation;
                };
            }
            case Scale: {
                return (script) -> {
                    node.outputs.get(0).object = node.gameObject.transform.localScale;
                };
            }
        }

        return (script) -> {};
    }

}
