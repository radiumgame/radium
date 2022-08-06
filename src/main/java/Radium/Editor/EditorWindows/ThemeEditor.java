package Radium.Editor.EditorWindows;

import Radium.Engine.Color.Color;
import Radium.Engine.System.FileExplorer;
import Radium.Engine.Util.FileUtility;
import Radium.Editor.Console;
import Radium.Editor.EditorGUI;
import Radium.Editor.Theme;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;

import java.io.File;

public class ThemeEditor {

    public static boolean Render = false;

    private static Color textColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private static Color headerColor = new Color(0.09f, 0.12f, 0.14f, 1.00f);
    private static Color areaColor = new Color(0.20f, 0.25f, 0.29f, 1.00f);
    private static Color bodyColor = new Color(0.11f, 0.15f, 0.17f, 1.00f);
    private static Color tabColor = new Color(0.09f, 0.12f, 0.14f, 1.00f);
    private static Color popupColor = new Color(0.11f, 0.11f, 0.11f, 0.94f);

    protected ThemeEditor() {}

    public static void Render() {
        if (!Render) return;

        ImGui.begin("Theme Editor");

        textColor = EditorGUI.ColorField("Text Color", textColor);
        headerColor = EditorGUI.ColorField("Header Color", headerColor);
        areaColor = EditorGUI.ColorField("Area Color", areaColor);
        bodyColor = EditorGUI.ColorField("Body Color", bodyColor);
        tabColor = EditorGUI.ColorField("Tab Color", tabColor);
        popupColor = EditorGUI.ColorField("Popup Color", popupColor);

        if (ImGui.button("Create Theme")) {
            String path = FileExplorer.Create("thm");
            if (FileExplorer.IsPathValid(path)) {
                try {
                    File f = new File(path);
                    f.createNewFile();

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(new Theme(textColor, headerColor, areaColor, bodyColor, tabColor, popupColor));
                    FileUtility.Write(f, json);
                } catch (Exception e) {
                    Console.Error(e);
                }

                Render = false;
            }
        }

        ImGui.end();
    }

}
