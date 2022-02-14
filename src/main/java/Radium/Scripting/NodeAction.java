package Radium.Scripting;

import Radium.Math.Vector.Vector3;
import Radium.Time;
import RadiumEditor.Console;

public class NodeAction {

    protected NodeAction() {}

    public static Runnable ActionFromType(ScriptingNode node) {
        switch (node.nodeType) {
            case Add -> {
                return () -> {
                    float a = (float)node.inputs.get(1).object;
                    float b = (float)node.inputs.get(2).object;
                    node.outputs.get(1).object = a + b;
                };
            }
            case Subtract -> {
                return () -> {
                    float a = (float)node.inputs.get(1).object;
                    float b = (float)node.inputs.get(2).object;
                    node.outputs.get(1).object = a - b;
                };
            }
            case Multiply -> {
                return () -> {
                    float a = (float)node.inputs.get(1).object;
                    float b = (float)node.inputs.get(2).object;
                    node.outputs.get(1).object = a * b;
                };
            }
            case Divide -> {
                return () -> {
                    float a = (float)node.inputs.get(1).object;
                    float b = (float)node.inputs.get(2).object;
                    node.outputs.get(1).object = a / b;
                };
            }
            case Log -> {
                return () -> {
                    Console.Log(node.inputs.get(1).object);
                };
            }
            case Position -> {
                return () -> {
                    node.gameObject.transform.localPosition = (Vector3)node.inputs.get(1).object;
                };
            }
        }

        return () -> {};
    }

    public static Runnable UpdateFromType(ScriptingNode node) {
        switch (node.nodeType) {
            case Time: {
                return () -> {
                    node.outputs.get(0).object = Time.deltaTime;
                };
            }
        }

        return () -> {};
    }

}
