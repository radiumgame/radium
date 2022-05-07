package Radium.Serialization.TypeAdapters;

import Radium.Component;
import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.MeshRenderer;
import Radium.Components.Scripting.PythonScripting;
import Radium.Components.UI.Button;
import Radium.Graphics.Material;
import Radium.Graphics.RendererType;
import Radium.Math.Transform;
import Radium.Objects.GameObject;
import Radium.Scripting.Python.PythonScript;
import RadiumEditor.Console;
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
        newObject.transform = transform;
        newObject.name = name;

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