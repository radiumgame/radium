package Radium.Engine.Serialization;

import Radium.Engine.Component;
import Radium.Engine.Graphics.Mesh;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.Scripting.Nodes.NodeInput;
import Radium.Engine.Serialization.TypeAdapters.*;
import Radium.Integration.Project.Project;
import Radium.Engine.Util.FileUtility;
import Radium.Editor.Console;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.File;

public class Serializer {

    private static ObjectMapper mapper;
    private static ObjectMapper runtimeMapper;

    protected Serializer() {}

    public static ObjectMapper GetMapper() {
        if (mapper != null) return mapper;

        mapper = new ObjectMapper();
        mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
        SimpleModule module = new SimpleModule();
        module.addSerializer(Component.class, new ComponentSerializer());
        module.addDeserializer(Component.class, new ComponentDeserializer());
        module.addSerializer(GameObject.class, new GameObjectSerializer());
        module.addSerializer(String.class, new StringSerializer());
        module.addDeserializer(String.class, new StringDeserializer());
        module.addSerializer(File.class, new FileSerializer());
        module.addDeserializer(File.class, new FileDeserializer());
        module.addDeserializer(GameObject.class, new GameObjectDeserializer(false));
        module.addDeserializer(Texture.class, new TextureDeserializer());
        module.addDeserializer(NodeInput.class, new NodeInputDeserializer());
        module.addDeserializer(Mesh.class, new MeshDeserializer());
        mapper.registerModule(module);

        return mapper;
    }

    public static ObjectMapper GetRuntimeMapper() {
        if (runtimeMapper != null) return runtimeMapper;

        runtimeMapper = new ObjectMapper();
        runtimeMapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
        SimpleModule module = new SimpleModule();
        module.addSerializer(Component.class, new ComponentSerializer());
        module.addDeserializer(Component.class, new ComponentDeserializer());
        module.addSerializer(GameObject.class, new GameObjectSerializer());
        module.addDeserializer(GameObject.class, new GameObjectDeserializer(true));
        module.addSerializer(String.class, new StringSerializer());
        module.addDeserializer(String.class, new StringDeserializer());
        module.addSerializer(File.class, new FileSerializer());
        module.addDeserializer(File.class, new FileDeserializer());
        module.addDeserializer(Texture.class, new TextureDeserializer());
        module.addDeserializer(NodeInput.class, new NodeInputDeserializer());
        module.addDeserializer(Mesh.class, new MeshDeserializer());
        runtimeMapper.registerModule(module);

        return runtimeMapper;
    }

    public static void Save(Object obj, String filepath) {
        try {
            File f = new File(filepath);
            if (!f.exists()) f.createNewFile();

            ObjectMapper mapper = new ObjectMapper();
            mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
            String json = mapper.writeValueAsString(obj);
            FileUtility.Write(f, json);
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public static void SaveInProject(Object obj, String filepath) {
        try {
            File f = new File(Project.Current().root + "/" + filepath);
            if (!f.exists()) f.createNewFile();

            ObjectMapper mapper = new ObjectMapper();
            mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
            String json = mapper.writeValueAsString(obj);
            FileUtility.Write(f, json);
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public static Object Load(String path, Class type) throws Exception {
        File f = new File(path);
        if (!f.exists()) {
            Console.Error("Path doesn't exist: " + path);
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
        return mapper.readValue(FileUtility.ReadFile(f), type);
    }

    public static Object LoadFromProject(String path, Class type) {
        try {
            File f = new File(Project.Current().root + "/" + path);
            if (!f.exists()) {
                Console.Error("Path doesn't exist: " + path);
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
            return mapper.readValue(FileUtility.ReadFile(f), type);
        } catch (Exception e) {
            Console.Error(e);
            return null;
        }
    }

    public static String ReadString(TreeNode node) {
        String val = node.toString();
        val = val.replaceAll("\"", "");

        return val;
    }

    public static void RegisterFileAdapter(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(String.class, new StringSerializer());
        module.addDeserializer(String.class, new StringDeserializer());
        module.addSerializer(File.class, new FileSerializer());
        module.addDeserializer(File.class, new FileDeserializer());
        mapper.registerModule(module);
    }

}
