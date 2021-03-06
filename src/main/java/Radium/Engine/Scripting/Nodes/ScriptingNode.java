package Radium.Engine.Scripting.Nodes;

import Radium.Engine.Objects.GameObject;
import imgui.ImVec2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScriptingNode {

    public String name;
    public List<NodeInput> inputs = new ArrayList<>();
    public List<NodeInput> outputs = new ArrayList<>();
    public transient Consumer<NodeScript> action = (script) -> {};
    public transient Consumer<NodeScript> start = (script) -> {};
    public transient Consumer<NodeScript> update = (script) -> {};
    public transient Consumer<NodeScript> display = (script) -> {};

    public int ID = Nodes.IDGen.NewID();
    public NodeType nodeType = NodeType.Start;

    public transient boolean alive = true;

    public ImVec2 position = new ImVec2(0, 0);

    public transient GameObject gameObject = new GameObject(false);

    public ScriptingNode() {
        Initialize();
    }

    private void Initialize() {
        inputs.add(Nodes.InputAction(this));
        outputs.add(Nodes.OutputAction(this));
    }

    public void Start(NodeScript script) {
        start.accept(script);

        for (NodeInput output : outputs) {
            output.UpdateLinks();
        }
    }

    public void Update(NodeScript script) {
        for (NodeInput output : outputs) {
            output.Update();
        }

        update.accept(script);
    }

    public void Delete(NodeScript script) {
        for (int i = 0; i < script.links.size(); i++) {
            NodeInput[] links = script.links.get(i);
            if (outputs.contains(links[0])) {
                links[0].links.remove(links[1]);
                script.links.remove(i);
            } else if (inputs.contains(links[1])) {
                links[1].links.remove(links[0]);
                script.links.remove(i);
            }
        }

        script.nodes.remove(this);
        alive = false;
    }

    public List<NodeInput> GetTriggerOutput() {
        List<NodeInput> result = new ArrayList<>();
        for (NodeInput output : outputs) {
            if (output.type == NodeTrigger.class) {
                result.add(output);
            }
        }

        return result;
    }

    public NodeInput GetInput(String name) {
        for (NodeInput input : inputs) {
            if (input.name == name) {
                return input;
            }
        }

        return null;
    }

    public NodeInput GetOutput(String name) {
        for (NodeInput output : outputs) {
            if (output.name == name) {
                return output;
            }
        }

        return null;
    }

    public static ScriptingNode NodeFromType(NodeType type) {
        switch (type) {
            case Integer -> { return Nodes.Integer(); }
            case Float -> { return Nodes.Float(); }
            case Boolean -> { return Nodes.Boolean(); }
            case String -> { return Nodes.String(); }
            case Vector2 -> { return Nodes.Vector2(); }
            case Vector3 -> { return Nodes.Vector3(); }
            case Color -> { return Nodes.Color(); }
            case Texture -> { return Nodes.Texture(); }
            case Add -> { return Nodes.AddNode(); }
            case Subtract -> { return Nodes.SubtractNode(); }
            case Multiply -> { return Nodes.MultiplyNode(); }
            case Divide -> { return Nodes.DivideNode(); }
            case Sine -> { return Nodes.SineNode(); }
            case Cosine -> { return Nodes.CosineNode(); }
            case Normalize -> { return Nodes.Normalize(); }
            case Vector3Add -> { return Nodes.Vector3AddNode(); }
            case Vector3Subtract -> { return Nodes.Vector3SubtractNode(); }
            case Vector3Multiply -> { return Nodes.Vector3MultiplyNode(); }
            case Vector3Divide -> { return Nodes.Vector3DivideNode(); }
            case Vector3Lerp -> { return Nodes.Vector3LerpNode(); }
            case ColorToVector3 -> { return Nodes.ColorToVector3(); }
            case Vector3ToColor -> { return Nodes.Vector3ToColor(); }
            case ComposeVector -> { return Nodes.ComposeVector(); }
            case DecomposeVector -> { return Nodes.DecomposeVector(); }
            case Log -> { return Nodes.Log(); }
            case Time -> { return Nodes.Time(); }
            case Position -> { return Nodes.Position(); }
            case SetPosition -> { return Nodes.SetPosition(); }
            case Rotation -> { return Nodes.Rotation(); }
            case SetRotation -> { return Nodes.SetRotation(); }
            case Scale -> { return Nodes.Scale(); }
            case SetScale -> { return Nodes.SetScale(); }
            case Translate -> { return Nodes.Translate(); }
            case Rotate -> { return Nodes.Rotate(); }
            case Scaling -> { return Nodes.Scaling(); }
            case DestroyMesh -> { return Nodes.DestroyMesh(); }
            case SetMaterialTexture -> { return Nodes.SetMaterialTexture(); }
            case SetMaterialNormalMap -> { return Nodes.SetMaterialNormalMap(); }
            case SetMaterialSpecularMap -> { return Nodes.SetMaterialSpecularMap(); }
            case ToggleNormalMap -> { return Nodes.ToggleNormalMap(); }
            case ToggleSpecularMap -> { return Nodes.ToggleSpecularMap(); }
            case ToggleSpecularLighting -> { return Nodes.ToggleSpecularLighting(); }
            case ToggleCullFaces -> { return Nodes.ToggleCullFaces(); }
            case SetOutlineWidth -> { return Nodes.OutlineWidth(); }
            case SetOutlineColor -> { return Nodes.OutlineColor(); }
            case SetRotatorAxis -> { return Nodes.RotatorAxis(); }
            case SetRotatorSpeed -> { return Nodes.RotatorSpeed(); }
            case RigidbodyMass -> { return Nodes.RigidbodyMass(); }
            case RigidbodyGravity -> { return Nodes.RigidbodyGravity(); }
            case CameraFOV -> { return Nodes.CameraFOV(); }
            case CameraNear -> { return Nodes.CameraNear(); }
            case CameraFar -> { return Nodes.CameraFar(); }
            case LightColor -> { return Nodes.LightColor(); }
            case LightIntensity -> { return Nodes.LightIntensity(); }
            case LightAttenuation -> { return Nodes.LightAttenuation(); }
            case AudioPlay -> { return Nodes.AudioPlay(); }
            case AudioStop -> { return Nodes.AudioStop(); }
            case AudioPause -> { return Nodes.AudioPause(); }
            case AudioPitch -> { return Nodes.AudioPitch(); }
            case AudioLoop -> { return Nodes.AudioLoop(); }
            case AudioPlayOnAwake -> { return Nodes.AudioPlayOnAwake(); }
        }

        return null;
    }

}
