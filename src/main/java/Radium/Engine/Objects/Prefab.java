package Radium.Engine.Objects;

import Radium.Editor.Console;
import Radium.Engine.Component;
import Radium.Engine.Serialization.Serializer;
import Radium.Engine.Serialization.TypeAdapters.ComponentSerializer;
import Radium.Engine.Serialization.TypeAdapters.GameObjectDeserializer;
import Radium.Engine.Util.FileUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Prefab {

    public String filepath;
    public File file;

    public Prefab(String filepath) {
        this.filepath = filepath;
        this.file = new File(filepath);
    }

    public GameObject Create() {
        try {
            ObjectMapper mapper = Serializer.GetMapper();
            String content = FileUtility.ReadFile(file);

            return mapper.readValue(content, GameObject.class);
        } catch (Exception e) {
            Console.Error(e);
            return new GameObject();
        }
    }

    public static void Save(GameObject gameObject, String path) {
        try {
            if (!Files.exists(Paths.get(path))) {
                FileUtility.Create(path);
            }

            ObjectMapper mapper = Serializer.GetMapper();
            String content = mapper.writeValueAsString(gameObject);
            FileUtility.Write(new File(path), content);
        } catch (Exception e) {
            Console.Error(e);
        }
    }

}
