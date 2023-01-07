package Radium.Engine.Serialization.TypeAdapters;

import Radium.Editor.Console;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.OGLCommands;
import Radium.Engine.Serialization.Serializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.lang.reflect.Type;

public class TextureDeserializer extends StdDeserializer<Texture> {

    public TextureDeserializer() {
        super(Texture.class);
    }

    @Override
    public Texture deserialize(JsonParser p, DeserializationContext context) {
        try {
            TreeNode node = p.readValueAsTree();
            String path = Serializer.ReadString(node.get("path"));

            return new Texture(path, false);
        } catch (Exception e) {
            Console.Error(e);
            return new Texture("EngineAssets/Textures/Misc/blank.jpg", false);
        }
    }

}
