package Radium.Scripting;

import Radium.Math.Vector.Vector3;
import Radium.Time;
import RadiumEditor.Console;

import java.util.function.Consumer;

public class NodeAction {

    protected NodeAction() {}

    public static Consumer<NodeScript> ActionFromType(ScriptingNode node) {
        switch (node.nodeType) {
            case Log: {
                return (script) -> {
                    Console.Log(node.inputs.get(1).object);
                };
            }
            case SetPosition: {
                return (script) -> {
                    node.gameObject.transform.localPosition = (Vector3)node.inputs.get(1).object;
                };
            }
            case SetRotation: {
                return (script) -> {
                    node.gameObject.transform.localRotation = (Vector3)node.inputs.get(1).object;
                };
            }
            case SetScale: {
                return (script) -> {
                    node.gameObject.transform.localScale = (Vector3)node.inputs.get(1).object;
                };
            }
            case Translate: {
                return((script) -> {
                    node.gameObject.transform.localPosition = Vector3.Add(node.gameObject.transform.localPosition, (Vector3)node.inputs.get(1).object);
                });
            }
            case Rotate: {
                return((script) -> {
                    node.gameObject.transform.localRotation = Vector3.Add(node.gameObject.transform.localRotation, (Vector3)node.inputs.get(1).object);
                });
            }
            case Scaling: {
                return((script) -> {
                    node.gameObject.transform.localScale = Vector3.Add(node.gameObject.transform.localScale, (Vector3)node.inputs.get(1).object);
                });
            }
        }

        return (script) -> {};
    }

    public static Consumer<NodeScript> StartFromType(ScriptingNode node) {
        switch (node.nodeType) {
            case DecomposeVector: {
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

    public static Consumer<NodeScript> UpdateFromType(ScriptingNode node) {
        switch (node.nodeType) {
            case Add: {
                return (script) -> {
                    float a = (float)node.inputs.get(0).Value();
                    float b = (float)node.inputs.get(1).Value();
                    node.outputs.get(0).object = a + b;
                };
            }
            case Subtract: {
                return (script) -> {
                    float a = (float)node.inputs.get(0).Value();
                    float b = (float)node.inputs.get(1).Value();
                    node.outputs.get(0).object = a - b;
                };
            }
            case Multiply: {
                return (script) -> {
                    float a = (float)node.inputs.get(0).Value();
                    float b = (float)node.inputs.get(1).Value();
                    node.outputs.get(0).object = a * b;
                };
            }
            case Divide: {
                return (script) -> {
                    float a = (float)node.inputs.get(0).Value();
                    float b = (float)node.inputs.get(1).Value();
                    node.outputs.get(0).object = a / b;
                };
            }
            case Time: {
                return (script) -> {
                    node.outputs.get(0).object = Time.deltaTime;
                };
            }
            case DecomposeVector: {
                Vector3 vector = (Vector3)node.inputs.get(0).Value();
                if (vector == null) {
                    return (script) -> {};
                }

                return (script) -> {
                    node.outputs.get(0).object = vector.x;
                    node.outputs.get(1).object = vector.y;
                    node.outputs.get(2).object = vector.z;
                };
            }
            case ComposeVector: {
                float x = (float)node.inputs.get(0).Value();
                float y = (float)node.inputs.get(1).Value();
                float z = (float)node.inputs.get(2).Value();

                return((script) -> {
                    node.outputs.get(0).object = new Vector3(x, y, z);
                });
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
