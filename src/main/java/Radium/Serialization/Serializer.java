package Radium.Serialization;

import Integration.Project.Project;
import Radium.Util.FileUtility;
import RadiumEditor.Console;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.lang.reflect.Type;

public class Serializer {

    protected Serializer() {}

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
