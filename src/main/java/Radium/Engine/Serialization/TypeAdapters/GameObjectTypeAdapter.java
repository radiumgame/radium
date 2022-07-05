package Radium.Engine.Serialization.TypeAdapters;

import Radium.Engine.Component;
import Radium.Engine.Components.Graphics.MeshFilter;
import Radium.Engine.Components.Graphics.MeshRenderer;
import Radium.Engine.Components.Scripting.PythonScripting;
import Radium.Engine.Components.UI.Button;
import Radium.Engine.Graphics.RendererType;
import Radium.Engine.Math.Transform;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.Scripting.Python.PythonScript;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Serializes game objects into json
 */
public class GameObjectTypeAdapter implements JsonDeserializer<GameObject> {

    
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String name = object.get("name").getAsString();
        JsonArray components = object.get("components").getAsJsonArray();
        JsonElement transformElement = object.get("transform");
        Transform transform = context.deserialize(transformElement, Transform.class);

        GameObject newObject = new GameObject();
        newObject.id = object.get("id").getAsString();
        newObject.transform = transform;
        newObject.name = name;

        JsonElement parentID = object.get("parentID");
        if (parentID != null) {
            String id = parentID.getAsString();
            GameObject parent = GameObject.Find(id);
            if (parent != null) {
                newObject.SetParent(parent);
            }
        }

        for (JsonElement e : components) {
            Component c = context.deserialize(e, Component.class);

            if (c.getClass() == MeshFilter.class) {
                MeshFilter filter = (MeshFilter)c;

                MeshFilter meshFilter = new MeshFilter(filter.mesh, filter.material);
                newObject.AddComponent(meshFilter);

                continue;
            }
            if (c.getClass() == MeshRenderer.class) {
                MeshRenderer renderer = (MeshRenderer)c;
                if (renderer.renderType == RendererType.Custom) {
                    renderer.CreateRenderer(renderer.shader, renderer.s.GetUniforms());
                }
            }
            if (c.getClass() == PythonScripting.class) {
                PythonScripting scripting = (PythonScripting)c;
                scripting.gameObject = newObject;
                for (PythonScript script : scripting.scripts) {
                    script.gameObject = newObject;
                    script.Initialize();
                }
            }
            if (c.getClass() == Button.class) {
                Button button = (Button)c;
                button.needToAdd = false;
            }

            newObject.AddComponent(c);
        }

        return newObject;
    }

}