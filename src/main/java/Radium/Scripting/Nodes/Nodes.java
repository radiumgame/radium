package Radium.Scripting.Nodes;

import Radium.Color;
import Radium.Graphics.Texture;
import Radium.Math.Axis;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.Time;
import Radium.Util.IDGenerator;

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
        input.object = new NodeTrigger();

        return input;
    }

    public static NodeInput OutputAction(ScriptingNode node) {
        NodeInput input = new NodeInput(node);
        input.name = "Trigger Out";
        input.type = NodeTrigger.class;
        input.object = new NodeTrigger();

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

    public static ScriptingNode If() {
        ScriptingNode node = new ScriptingNode();
        node.name = "If";
        node.nodeType = NodeType.If;

        node.outputs.clear();

        NodeInput bool = new NodeInput(node);
        bool.name = "Condition";
        bool.type = Boolean.class;
        bool.object = false;
        node.inputs.add(bool);

        NodeInput out = new NodeInput(node);
        out.name = "If";
        out.object = new NodeTrigger();
        out.type = NodeTrigger.class;
        node.outputs.add(out);

        NodeInput els = new NodeInput(node);
        els.name = "Else";
        els.object = new NodeTrigger();
        els.type = NodeTrigger.class;
        node.outputs.add(els);

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

    public static ScriptingNode ColorLerpNode() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Color Lerp";
        node.nodeType = NodeType.ColorLerp;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput a = new NodeInput(node);
        a.name = "A";
        a.type = Color.class;

        NodeInput b = new NodeInput(node);
        b.name = "B";
        b.type = Color.class;

        NodeInput time = new NodeInput(node);
        time.name = "Time";
        time.type = Float.class;

        NodeInput output = new NodeInput(node);
        output.name = "Output";
        output.type = Color.class;

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

    public static ScriptingNode Axis() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Axis";
        node.nodeType = NodeType.Axis;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput out = new NodeInput(node);
        out.name = "Axis";
        out.type = Axis.class;
        out.object = Axis.X;
        node.outputs.add(out);

        AssignDisplay(node);

        return node;
    }

    public static ScriptingNode RotatorAxis() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Rotator Axis";
        node.nodeType = NodeType.SetRotatorAxis;

        NodeInput axis = new NodeInput(node);
        axis.type = Axis.class;
        axis.object = Axis.X;
        node.inputs.add(axis);

        return node;
    }

    public static ScriptingNode RotatorSpeed() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Rotator Speed";
        node.nodeType = NodeType.SetRotatorSpeed;

        NodeInput speed = new NodeInput(node);
        speed.type = Float.class;
        speed.object = 30f;
        node.inputs.add(speed);

        return node;
    }

    public static ScriptingNode RigidbodyMass() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Rigidbody Mass";
        node.nodeType = NodeType.RigidbodyMass;

        NodeInput mass = new NodeInput(node);
        mass.type = Float.class;
        mass.object = 1f;
        node.inputs.add(mass);

        return node;
    }

    public static ScriptingNode RigidbodyGravity() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Rigidbody Gravity";
        node.nodeType = NodeType.RigidbodyGravity;

        NodeInput gravity = new NodeInput(node);
        gravity.type = Boolean.class;
        gravity.object = true;
        node.inputs.add(gravity);

        return node;
    }

    public static ScriptingNode CameraFOV() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Camera FOV";
        node.nodeType = NodeType.CameraFOV;

        NodeInput fov = new NodeInput(node);
        fov.type = Float.class;
        fov.object = 70f;
        node.inputs.add(fov);

        return node;
    }

    public static ScriptingNode CameraNear() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Camera Near";
        node.nodeType = NodeType.CameraNear;

        NodeInput near = new NodeInput(node);
        near.type = Float.class;
        near.object = 0.1f;
        node.inputs.add(near);

        return node;
    }

    public static ScriptingNode CameraFar() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Camera Far";
        node.nodeType = NodeType.CameraFar;

        NodeInput far = new NodeInput(node);
        far.type = Float.class;
        far.object = 100f;
        node.inputs.add(far);

        return node;
    }

    public static ScriptingNode LightColor() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Light Color";
        node.nodeType = NodeType.LightColor;

        NodeInput col = new NodeInput(node);
        col.type = Color.class;
        col.object = new Color(1f, 1f, 1f, 1f);
        node.inputs.add(col);

        return node;
    }

    public static ScriptingNode LightIntensity() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Light Intensity";
        node.nodeType = NodeType.LightIntensity;

        NodeInput intensity = new NodeInput(node);
        intensity.type = Float.class;
        intensity.object = 1f;
        node.inputs.add(intensity);

        return node;
    }

    public static ScriptingNode LightAttenuation() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Light Attenuation";
        node.nodeType = NodeType.LightAttenuation;

        NodeInput attenuation = new NodeInput(node);
        attenuation.type = Float.class;
        attenuation.object = 0.045f;
        node.inputs.add(attenuation);

        return node;
    }

    public static ScriptingNode AudioPlay() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Play Source";
        node.nodeType = NodeType.AudioPlay;

        return node;
    }

    public static ScriptingNode AudioStop() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Stop Source";
        node.nodeType = NodeType.AudioStop;

        return node;
    }

    public static ScriptingNode AudioPause() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Pause Source";
        node.nodeType = NodeType.AudioPause;

        return node;
    }

    public static ScriptingNode AudioPitch() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Source Pitch";
        node.nodeType = NodeType.AudioPitch;

        NodeInput pitch = new NodeInput(node);
        pitch.name = "Pitch";
        pitch.type = Float.class;
        pitch.object = 1f;
        node.inputs.add(pitch);

        return node;
    }

    public static ScriptingNode AudioLoop() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Source Loop";
        node.nodeType = NodeType.AudioLoop;

        NodeInput loop = new NodeInput(node);
        loop.name = "Loop";
        loop.type = Boolean.class;
        loop.object = true;
        node.inputs.add(loop);

        return node;
    }

    public static ScriptingNode AudioPlayOnAwake() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Source Play on Awake";
        node.nodeType = NodeType.AudioPlayOnAwake;

        NodeInput awake = new NodeInput(node);
        awake.name = "Play";
        awake.type = Boolean.class;
        awake.object = true;
        node.inputs.add(awake);

        return node;
    }

    public static ScriptingNode ImagePosition() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Image Position";
        node.nodeType = NodeType.ImagePosition;

        NodeInput pos = new NodeInput(node);
        pos.name = "Position";
        pos.type = Vector2.class;
        pos.object = new Vector2(0, 0);
        node.inputs.add(pos);

        return node;
    }

    public static ScriptingNode ImageSize() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Image Size";
        node.nodeType = NodeType.ImageSize;

        NodeInput pos = new NodeInput(node);
        pos.name = "Size";
        pos.type = Vector2.class;
        pos.object = new Vector2(0, 0);
        node.inputs.add(pos);

        return node;
    }

    public static ScriptingNode ImageTexture() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Image Texture";
        node.nodeType = NodeType.ImageTexture;

        NodeInput pos = new NodeInput(node);
        pos.name = "Texture";
        pos.type = Texture.class;
        pos.object = new Texture("EngineAssets/Textures/Misc/blank.jpg");
        node.inputs.add(pos);

        return node;
    }

    public static ScriptingNode TextPosition() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Text Position";
        node.nodeType = NodeType.TextPosition;

        NodeInput pos = new NodeInput(node);
        pos.name = "Position";
        pos.type = Vector2.class;
        pos.object = new Vector2(0, 0);
        node.inputs.add(pos);

        return node;
    }

    public static ScriptingNode TextColor() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Text Color";
        node.nodeType = NodeType.TextColor;

        NodeInput pos = new NodeInput(node);
        pos.name = "Color";
        pos.type = Color.class;
        pos.object = new Color(1f, 1f, 1f, 1f);
        node.inputs.add(pos);

        return node;
    }

    public static ScriptingNode TextContent() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Text Content";
        node.nodeType = NodeType.TextContent;

        NodeInput pos = new NodeInput(node);
        pos.name = "Content";
        pos.type = String.class;
        pos.object = "Placeholder";
        node.inputs.add(pos);

        return node;
    }

    public static ScriptingNode TextSize() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Text Size";
        node.nodeType = NodeType.TextSize;

        NodeInput pos = new NodeInput(node);
        pos.name = "Size";
        pos.type = Integer.class;
        pos.object = 48;
        node.inputs.add(pos);

        return node;
    }

}
