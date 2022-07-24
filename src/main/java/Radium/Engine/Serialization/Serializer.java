package Radium.Engine.Serialization;

import Radium.Engine.Component;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.Serialization.TypeAdapters.ClassTypeAdapter;
import Radium.Engine.Serialization.TypeAdapters.ComponentTypeAdapter;
import Radium.Engine.Serialization.TypeAdapters.GameObjectTypeAdapter;
import Radium.Engine.Serialization.TypeAdapters.TextureTypeAdapter;
import Radium.Integration.Project.Project;
import Radium.Engine.Util.FileUtility;
import Radium.Editor.Console;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

public class Serializer {

    protected Serializer() {}

    public static final Gson Serializer = new GsonBuilder().registerTypeAdapter(GameObject .class, new GameObjectTypeAdapter())
            .registerTypeAdapter(Component .class, new ComponentTypeAdapter())
            .registerTypeAdapter(Class.class, new ClassTypeAdapter())
            .registerTypeAdapter(Texture .class, new TextureTypeAdapter())
            .create();;

    public static void Save(Object obj, String filepath) {
        try {
            File f = new File(filepath);
            if (!f.exists()) f.createNewFile();

            Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create();
            String json = gson.toJson(obj);
            FileUtility.Write(f, json);
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public static void SaveInProject(Object obj, String filepath) {
        try {
            File f = new File(Project.Current().root + "/" + filepath);
            if (!f.exists()) f.createNewFile();

            Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create();
            String json = gson.toJson(obj);
            FileUtility.Write(f, json);
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public static Object Load(String path, Class type) {
        File f = new File(path);
        if (!f.exists()) {
            Console.Error("Path doesn't exist: " + path);
            return null;
        }

        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create();
        return gson.fromJson(FileUtility.ReadFile(f), type);
    }

    public static Object LoadFromProject(String path, Class type) {
        File f = new File(Project.Current().root + "/" + path);
        if (!f.exists()) {
            Console.Error("Path doesn't exist: " + path);
            return null;
        }

        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create();
        return gson.fromJson(FileUtility.ReadFile(f), type);
    }

}
