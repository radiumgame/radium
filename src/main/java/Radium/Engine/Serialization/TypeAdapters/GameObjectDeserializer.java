package Radium.Engine.Serialization.TypeAdapters;

import Radium.Editor.Console;
import Radium.Engine.Component;
import Radium.Engine.Components.Graphics.MeshFilter;
import Radium.Engine.Components.Graphics.MeshRenderer;
import Radium.Engine.Components.Scripting.PythonScripting;
import Radium.Engine.Components.UI.Button;
import Radium.Engine.Graphics.RendererType;
import Radium.Engine.Math.Transform;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.Objects.Groups.Group;
import Radium.Engine.Scripting.Python.PythonScript;
import Radium.Engine.Serialization.Serializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * Serializes game objects into json
 */
public class GameObjectDeserializer extends StdDeserializer<GameObject> {

    private boolean runtime = false;

    public GameObjectDeserializer(boolean runtime) {
        super(GameObject.class);
        this.runtime = runtime;
    }
    
    public GameObject deserialize(JsonParser p, DeserializationContext context) {
        try {
            TreeNode node = p.readValueAsTree();
            String name = Serializer.ReadString(node.get("name"));
            Transform transform = node.get("transform").traverse(p.getCodec()).readValueAs(Transform.class);

            GameObject newObject = new GameObject(!runtime);
            newObject.id = Serializer.ReadString(node.get("id"));
            newObject.transform = transform;
            newObject.transform.Update(newObject);
            newObject.name = name;

            String group = Serializer.ReadString(node.get("group"));
            newObject.group = Group.CreateGroup(group);

            Component[] components = node.get("components").traverse(p.getCodec()).readValueAs(Component[].class);
            for (Component c : components) {
                if (c == null) continue;
                if (c.getClass() == MeshRenderer.class) {
                    MeshRenderer renderer = (MeshRenderer) c;
                    if (renderer.renderType == RendererType.Custom) {
                        renderer.CreateRenderer(renderer.shader, renderer.s.GetUniforms());
                    }
                } else if (c.getClass() == PythonScripting.class) {
                    PythonScripting scripting = (PythonScripting) c;
                    scripting.gameObject = newObject;
                    for (PythonScript script : scripting.scripts) {
                        script.gameObject = newObject;
                        script.Initialize();
                    }
                } else if (c.getClass() == Button.class) {
                    Button button = (Button) c;
                    button.needToAdd = false;
                }

                newObject.AddComponent(c);
            }

            String parentID = Serializer.ReadString(node.get("parentID"));
            if (!Objects.equals(parentID, "")) {
                newObject.tempId = parentID;
            }

            return newObject;
        } catch (Exception e) {
            e.printStackTrace();
            return new GameObject();
        }
    }

}