package Radium.Scripting;

import Radium.Color;
import Radium.Component;
import Radium.Graphics.Texture;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.Time;
import Radium.Util.IDGenerator;
import RadiumEditor.Console;

public class Nodes {

    protected Nodes() {}

    public static boolean NodePlay = false;
    public static IDGenerator IDGen = new IDGenerator();

    private static void AssignDisplay(ScriptingNode node) {
        node.display = NodeAction.DisplayFromType(node);
    }

    public static NodeInput InputAction(ScriptingNode node) {
        NodeInput input = new NodeInput(node);
        input.name = "Trigger In";
        input.type = NodeTrigger.class;

        return input;
    }

    public static NodeInput OutputAction(ScriptingNode node) {
        NodeInput input = new NodeInput(node);
        input.name = "Trigger Out";
        input.type = NodeTrigger.class;

        return input;
    }

    public static ScriptingNode Start() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Start";
        node.nodeType = NodeType.Update;
        node.inputs.clear();

        return node;
    }

    public static ScriptingNode Update() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Update";
        node.nodeType = NodeType.Update;
        node.inputs.clear();

        return node;
    }

    public static ScriptingNode GetComponent() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Get Component";
        node.nodeType = NodeType.GetComponent;

        NodeInput type = new NodeInput(node);
        type.name = "Component Type";
        type.type = Component.class;
        node.inputs.add(type);

        NodeInput output = new NodeInput(node);
        output.name = "Component";
        output.type = Component.class;
        node.outputs.add(output);

        AssignDisplay(node);

        return node;
    }

    public static ScriptingNode Integer() {
        ScriptingNode node = new ScriptingNode();
        node.name = "int";
        node.nodeType = NodeType.Integer;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput output = new NodeInput(node);
        output.name = "";
        output.type = Integer.class;
        output.object = 0;
        node.outputs.add(output);

        AssignDisplay(node);

        return node;
    }

    public static ScriptingNode Float() {
        ScriptingNode node = new ScriptingNode();
        node.name = "float";
        node.nodeType = NodeType.Float;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput output = new NodeInput(node);
        output.name = "";
        output.type = Float.class;
        output.object = 0f;
        node.outputs.add(output);

        AssignDisplay(node);

        return node;
    }

    public static ScriptingNode Boolean() {
        ScriptingNode node = new ScriptingNode();
        node.name = "bool";
        node.nodeType = NodeType.Boolean;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput output = new NodeInput(node);
        output.name = "";
        output.type = Boolean.class;
        output.object = false;
        node.outputs.add(output);

        AssignDisplay(node);

        return node;
    }

    public static ScriptingNode String() {
        ScriptingNode node = new ScriptingNode();
        node.name = "string";
        node.nodeType = NodeType.String;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput output = new NodeInput(node);
        output.name = "";
        output.type = String.class;
        output.object = "";
        node.outputs.add(output);

        AssignDisplay(node);

        return node;
    }

    public static ScriptingNode Vector2() {
        ScriptingNode node = new ScriptingNode();
        node.name = "vec2";
        node.nodeType = NodeType.Vector2;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput output = new NodeInput(node);
        output.name = "";
        output.type = Vector2.class;
        output.object = new Vector2(0, 0);
        node.outputs.add(output);

        AssignDisplay(node);

        return node;
    }

    public static ScriptingNode Vector3() {
        ScriptingNode node = new ScriptingNode();
        node.name = "vec3";
        node.nodeType = NodeType.Vector3;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput output = new NodeInput(node);
        output.name = "";
        output.type = Vector3.class;
        output.object = new Vector3(0, 0, 0);
        node.outputs.add(output);

        AssignDisplay(node);

        return node;
    }

    public static ScriptingNode Color() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Color";
        node.nodeType = NodeType.Color;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput outColor = new NodeInput(node);
        outColor.name = "Color";
        outColor.type = Color.class;
        outColor.object = new Color(1f, 1f, 1f, 1f);
        node.outputs.add(outColor);

        AssignDisplay(node);

        return node;
    }

    public static ScriptingNode AddNode() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Add";
        node.nodeType = NodeType.Add;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput a = new NodeInput(node);
        a.name = "A";
        a.type = Float.class;

        NodeInput b = new NodeInput(node);
        b.name = "B";
        b.type = Float.class;

        NodeInput output = new NodeInput(node);
        output.name = "Output";
        output.type = Float.class;

        node.inputs.add(a);
        node.inputs.add(b);
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode SubtractNode() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Subtract";
        node.nodeType = NodeType.Subtract;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput a = new NodeInput(node);
        a.name = "A";
        a.type = Float.class;

        NodeInput b = new NodeInput(node);
        b.name = "B";
        b.type = Float.class;

        NodeInput output = new NodeInput(node);
        output.name = "Output";
        output.type = Float.class;

        node.inputs.add(a);
        node.inputs.add(b);
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode MultiplyNode() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Multiply";
        node.nodeType = NodeType.Multiply;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput a = new NodeInput(node);
        a.name = "A";
        a.type = Float.class;

        NodeInput b = new NodeInput(node);
        b.name = "B";
        b.type = Float.class;

        NodeInput output = new NodeInput(node);
        output.name = "Output";
        output.type = Float.class;

        node.inputs.add(a);
        node.inputs.add(b);
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode DivideNode() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Divide";
        node.nodeType = NodeType.Divide;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput a = new NodeInput(node);
        a.name = "A";
        a.type = Float.class;

        NodeInput b = new NodeInput(node);
        b.name = "B";
        b.type = Float.class;

        NodeInput output = new NodeInput(node);
        output.name = "Output";
        output.type = Float.class;

        node.inputs.add(a);
        node.inputs.add(b);
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode SineNode() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Sine";
        node.nodeType = NodeType.Sine;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput input = new NodeInput(node);
        input.name = "X";
        input.type = Float.class;
        node.inputs.add(input);

        NodeInput output = new NodeInput(node);
        output.name = "Sine";
        output.type = Float.class;
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode CosineNode() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Sine";
        node.nodeType = NodeType.Cosine;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput input = new NodeInput(node);
        input.name = "X";
        input.type = Float.class;
        node.inputs.add(input);

        NodeInput output = new NodeInput(node);
        output.name = "Cosine";
        output.type = Float.class;
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode Normalize() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Normalize(0, 1)";
        node.nodeType = NodeType.Normalize;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput input = new NodeInput(node);
        input.name = "Value";
        input.type = Float.class;
        node.inputs.add(input);

        NodeInput output = new NodeInput(node);
        output.name = "Normalized";
        output.type = Float.class;
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode Vector3AddNode() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Vector3 Add";
        node.nodeType = NodeType.Vector3Add;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput a = new NodeInput(node);
        a.name = "A";
        a.type = Vector3.class;

        NodeInput b = new NodeInput(node);
        b.name = "B";
        b.type = Vector3.class;

        NodeInput output = new NodeInput(node);
        output.name = "Output";
        output.type = Vector3.class;

        node.inputs.add(a);
        node.inputs.add(b);
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode Vector3SubtractNode() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Vector3 Subtract";
        node.nodeType = NodeType.Vector3Subtract;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput a = new NodeInput(node);
        a.name = "A";
        a.type = Vector3.class;

        NodeInput b = new NodeInput(node);
        b.name = "B";
        b.type = Vector3.class;

        NodeInput output = new NodeInput(node);
        output.name = "Output";
        output.type = Vector3.class;

        node.inputs.add(a);
        node.inputs.add(b);
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode Vector3MultiplyNode() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Vector3 Multiply";
        node.nodeType = NodeType.Vector3Multiply;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput a = new NodeInput(node);
        a.name = "A";
        a.type = Vector3.class;

        NodeInput b = new NodeInput(node);
        b.name = "B";
        b.type = Vector3.class;

        NodeInput output = new NodeInput(node);
        output.name = "Output";
        output.type = Vector3.class;

        node.inputs.add(a);
        node.inputs.add(b);
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode Vector3DivideNode() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Vector3 Divide";
        node.nodeType = NodeType.Vector3Divide;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput a = new NodeInput(node);
        a.name = "A";
        a.type = Vector3.class;

        NodeInput b = new NodeInput(node);
        b.name = "B";
        b.type = Vector3.class;

        NodeInput output = new NodeInput(node);
        output.name = "Output";
        output.type = Vector3.class;

        node.inputs.add(a);
        node.inputs.add(b);
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode Vector3LerpNode() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Vector3 Lerp";
        node.nodeType = NodeType.Vector3Lerp;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput a = new NodeInput(node);
        a.name = "A";
        a.type = Vector3.class;

        NodeInput b = new NodeInput(node);
        b.name = "B";
        b.type = Vector3.class;

        NodeInput time = new NodeInput(node);
        time.name = "Time";
        time.type = Float.class;

        NodeInput output = new NodeInput(node);
        output.name = "Output";
        output.type = Vector3.class;

        node.inputs.add(a);
        node.inputs.add(b);
        node.inputs.add(time);
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode Vector3ToColor() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Vector3 to Color";
        node.nodeType = NodeType.Vector3ToColor;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput input = new NodeInput(node);
        input.name = "Vector";
        input.type = Vector3.class;
        node.inputs.add(input);

        NodeInput output = new NodeInput(node);
        output.name = "Color";
        output.type = Color.class;
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode ColorToVector3() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Color to Vector3";
        node.nodeType = NodeType.ColorToVector3;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput input = new NodeInput(node);
        input.name = "Color";
        input.type = Color.class;
        node.inputs.add(input);

        NodeInput output = new NodeInput(node);
        output.name = "Vector";
        output.type = Vector3.class;
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode DecomposeVector() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Decompose Vector";
        node.nodeType = NodeType.DecomposeVector;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput input = new NodeInput(node);
        input.name = "Vector3";
        input.type = Vector3.class;
        input.object = new Vector3(0, 0, 0);
        node.inputs.add(input);

        NodeInput x = new NodeInput(node);
        x.name = "X";
        x.type = Float.class;
        node.outputs.add(x);

        NodeInput y = new NodeInput(node);
        y.name = "Y";
        y.type = Float.class;
        node.outputs.add(y);

        NodeInput z = new NodeInput(node);
        z.name = "Z";
        z.type = Float.class;
        node.outputs.add(z);

        return node;
    }

    public static ScriptingNode ComposeVector() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Compose Vector";
        node.nodeType = NodeType.ComposeVector;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput x = new NodeInput(node);
        x.name = "X";
        x.type = Float.class;
        x.object = 0f;
        node.inputs.add(x);

        NodeInput y = new NodeInput(node);
        y.name = "Y";
        y.type = Float.class;
        y.object = 0f;
        node.inputs.add(y);

        NodeInput z = new NodeInput(node);
        z.name = "Z";
        z.type = Float.class;
        z.object = 0f;
        node.inputs.add(z);

        NodeInput output = new NodeInput(node);
        output.name = "Vector";
        output.type = Vector3.class;
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode Log() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Log";
        node.nodeType = NodeType.Log;

        NodeInput input = new NodeInput(node);
        input.name = "Message";
        input.type = Object.class;
        node.inputs.add(input);

        return node;
    }

    public static ScriptingNode Time() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Time";
        node.nodeType = NodeType.Time;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput time = new NodeInput(node);
        time.name = "Time";
        time.type = Float.class;
        time.object = Time.time;
        node.outputs.add(time);

        NodeInput deltaTime = new NodeInput(node);
        deltaTime.name = "Delta Time";
        deltaTime.type = Float.class;
        deltaTime.object = Time.deltaTime;
        node.outputs.add(deltaTime);

        return node;
    }

    public static ScriptingNode Position() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Position";
        node.nodeType = NodeType.Position;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput pos = new NodeInput(node);
        pos.name = "Position";
        pos.type = Vector3.class;
        node.outputs.add(pos);

        return node;
    }

    public static ScriptingNode SetPosition() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Position";
        node.nodeType = NodeType.SetPosition;

        NodeInput pos = new NodeInput(node);
        pos.name = "Position";
        pos.type = Vector3.class;
        pos.object = Vector3.Zero();
        node.inputs.add(pos);

        return node;
    }

    public static ScriptingNode Rotation() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Rotation";
        node.nodeType = NodeType.Rotation;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput pos = new NodeInput(node);
        pos.name = "Rotation";
        pos.type = Vector3.class;
        node.outputs.add(pos);

        return node;
    }

    public static ScriptingNode SetRotation() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Rotation";
        node.nodeType = NodeType.SetRotation;

        NodeInput pos = new NodeInput(node);
        pos.name = "Rotation";
        pos.type = Vector3.class;
        pos.object = Vector3.Zero();
        node.inputs.add(pos);

        return node;
    }

    public static ScriptingNode Scale() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Scale";
        node.nodeType = NodeType.Scale;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput pos = new NodeInput(node);
        pos.name = "Scale";
        pos.type = Vector3.class;
        node.outputs.add(pos);

        return node;
    }

    public static ScriptingNode SetScale() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Scale";
        node.nodeType = NodeType.SetScale;

        NodeInput pos = new NodeInput(node);
        pos.name = "Scale";
        pos.type = Vector3.class;
        pos.object = Vector3.Zero();
        node.inputs.add(pos);

        return node;
    }

    public static ScriptingNode Translate() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Translate";
        node.nodeType = NodeType.Translate;

        NodeInput translation = new NodeInput(node);
        translation.name = "Translation";
        translation.type = Vector3.class;
        node.inputs.add(translation);

        return node;
    }

    public static ScriptingNode Rotate() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Rotate";
        node.nodeType = NodeType.Rotate;

        NodeInput rotation = new NodeInput(node);
        rotation.name = "Rotation";
        rotation.type = Vector3.class;
        node.inputs.add(rotation);

        return node;
    }

    public static ScriptingNode Scaling() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Scaling";
        node.nodeType = NodeType.Scaling;

        NodeInput scale = new NodeInput(node);
        scale.name = "Scale";
        scale.type = Vector3.class;
        node.inputs.add(scale);

        return node;
    }

    public static ScriptingNode Texture() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Texture";
        node.nodeType = NodeType.Texture;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput tex = new NodeInput(node);
        tex.name = "Texture";
        tex.type = Texture.class;
        tex.object = new Texture("EngineAssets/Misc/blank.jpg");
        node.outputs.add(tex);

        AssignDisplay(node);

        return node;
    }

    public static ScriptingNode DestroyMesh() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Destroy Mesh";
        node.nodeType = NodeType.DestroyMesh;

        return node;
    }

    public static ScriptingNode SetMaterialTexture() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Material Texture";
        node.nodeType = NodeType.SetMaterialTexture;

        NodeInput input = new NodeInput(node);
        input.name = "Texture";
        input.type = Texture.class;
        input.object = new Texture("EngineAssets/Textures/blank.jpg");
        node.inputs.add(input);

        return node;
    }

    public static ScriptingNode SetMaterialNormalMap() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Material Normal Map";
        node.nodeType = NodeType.SetMaterialNormalMap;

        NodeInput input = new NodeInput(node);
        input.name = "Normal Map";
        input.type = Texture.class;
        input.object = new Texture("EngineAssets/Textures/blank.jpg");
        node.inputs.add(input);

        return node;
    }

    public static ScriptingNode SetMaterialSpecularMap() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Material Specular Map";
        node.nodeType = NodeType.SetMaterialSpecularMap;

        NodeInput input = new NodeInput(node);
        input.name = "Specular Map";
        input.type = Texture.class;
        input.object = new Texture("EngineAssets/Textures/blank.jpg");
        node.inputs.add(input);

        return node;
    }

    public static ScriptingNode ToggleNormalMap() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Toggle Normal Map";
        node.nodeType = NodeType.ToggleNormalMap;

        NodeInput enabled = new NodeInput(node);
        enabled.name = "Enabled";
        enabled.type = Boolean.class;
        enabled.object = false;
        node.inputs.add(enabled);

        return node;
    }

    public static ScriptingNode ToggleSpecularMap() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Toggle Specular Map";
        node.nodeType = NodeType.ToggleSpecularMap;

        NodeInput enabled = new NodeInput(node);
        enabled.name = "Enabled";
        enabled.type = Boolean.class;
        enabled.object = false;
        node.inputs.add(enabled);

        return node;
    }

    public static ScriptingNode ToggleSpecularLighting() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Toggle Specular Lighting";
        node.nodeType = NodeType.ToggleSpecularLighting;

        NodeInput enabled = new NodeInput(node);
        enabled.name = "Enabled";
        enabled.type = Boolean.class;
        enabled.object = false;
        node.inputs.add(enabled);

        return node;
    }

    public static ScriptingNode ToggleCullFaces() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Toggle Cull Faces";
        node.nodeType = NodeType.ToggleCullFaces;

        NodeInput enabled = new NodeInput(node);
        enabled.name = "Enabled";
        enabled.type = Boolean.class;
        enabled.object = false;
        node.inputs.add(enabled);

        return node;
    }

    public static ScriptingNode OutlineWidth() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Outline Width";
        node.nodeType = NodeType.SetOutlineWidth;

        NodeInput width = new NodeInput(node);
        width.name = "Width";
        width.type = Float.class;
        node.inputs.add(width);

        return node;
    }

    public static ScriptingNode OutlineColor() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Outline Color";
        node.nodeType = NodeType.SetOutlineColor;

        NodeInput width = new NodeInput(node);
        width.name = "Color";
        width.type = Color.class;
        node.inputs.add(width);

        return node;
    }

}
