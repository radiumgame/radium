package Engine;

import Editor.Console;
import Engine.EventSystem.EventListener;
import Engine.EventSystem.EventSystem;
import Engine.EventSystem.Events.Event;
import Engine.EventSystem.Events.EventType;
import Engine.Objects.GameObject;
import Engine.SceneManagement.SceneManager;
import Runtime.Runtime;

import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public final class Application implements EventListener {

    public static float FPS = 0;
    public static boolean Playing = false;

    public void Initialize() {
        EventSystem.RegisterEventListener(this);
    }

    @Override
    public void OnEvent(GameObject object, Event event) {
        if (event.GetType() == EventType.Play) {
            SceneManager.GetCurrentScene().Start();
            Playing = true;
        } else if (event.GetType() == EventType.Stop) {
            Playing = false;
        }
    }

    public void Build(String path) {
        File runtimeFile = new File("src/main/java/Runtime/Runtime.java");
        ReplaceInFile(runtimeFile, "private static boolean IsBuild = false;", "private static boolean IsBuild = true;");

        ReplaceInFile(runtimeFile, "private static boolean IsBuild = true;", "private static boolean IsBuild = false;");
    }

    private void ReplaceInFile(File file, String original, String replacement) {
        try {
            BufferedReader runtimeReader = new BufferedReader(new FileReader(file));

            String line = runtimeReader.readLine();
            String runtimeData = "";
            while (line != null) {
                runtimeData += line + "\n";
                line = runtimeReader.readLine();
            }
            runtimeReader.close();

            FileWriter runtimeWriter = new FileWriter(file, false);
            runtimeData = runtimeData.replace(original, replacement);

            runtimeWriter.write(runtimeData);
            runtimeWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
