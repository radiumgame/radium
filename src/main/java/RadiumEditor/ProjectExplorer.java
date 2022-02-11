package RadiumEditor;

import Radium.Color;
import Radium.Graphics.Texture;
import Radium.Input.Input;
import Radium.SceneManagement.Scene;
import Radium.SceneManagement.SceneManager;
import Radium.Util.FileUtility;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiWindowFlags;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Where files and folder are rendered
 */
public class ProjectExplorer {

    private static File currentDirectory = new File("./Assets/");
    private static List<File> filesInCurrentDirectory = new ArrayList<>();

    /**
     * Currently selected file
     */
    public static File SelectedFile;

    private static int File, Folder, BackArrow;
    private static Hashtable<String, Integer> FileIcons = new Hashtable<>();
    private static Hashtable<String, Consumer<File>> FileActions = new Hashtable<>();

    /**
     * When selected will try to get the GUI rendering information about file
     */
    public static Hashtable<String, Consumer<File>> FileGUIRender = new Hashtable<>();

    private static File root = new File("./Assets/");

    private static Color SelectedColor = new Color(80 / 255f, 120 / 255f, 237 / 255f);

    private static int SelectedImage = 0;

    private static boolean RightClickMenu = false;

    protected ProjectExplorer() {}

    /**
     * Initialize textures
     */
    public static void Initialize() {
        File = new Texture("EngineAssets/Editor/Explorer/file.png").textureID;
        Folder = new Texture("EngineAssets/Editor/Explorer/folder.png").textureID;
        BackArrow = new Texture("EngineAssets/Editor/Explorer/backarrow.png").textureID;
        RegisterExtensions();
        RegisterActions();
        RegisterFileGUI();

        UpdateDirectory();
    }

    /**
     * Render editor window
     */
    public static void Render() {
        ImGui.begin("Project Explorer", ImGuiWindowFlags.MenuBar);

        RenderMenuBar();

        if (SelectedFile == null) {
            SelectedImage = 0;
        }

        if (ImGui.isMouseClicked(1)) {
            if (ImGui.isWindowHovered()) {
                ImGui.openPopup("RightClickMenu");
                RightClickMenu = true;
            }
        }

        if (RightClickMenu) {
            if (ImGui.beginPopup("RightClickMenu")) {
                ImGui.endPopup();
            }
        }

        RenderFiles();

        ImGui.end();
    }

    private static void RenderMenuBar() {
        ImGui.beginMenuBar();

        if (ImGui.imageButton(BackArrow, 20, 17)) {
            String[] back = currentDirectory.getPath().split(Pattern.quote("\\"));

            if (back.length > 2) {
                currentDirectory = currentDirectory.getParentFile();
                UpdateDirectory();
            }
        }

        if (ImGui.button("Home")) {
            currentDirectory = new File("./Assets/");
            UpdateDirectory();
        }
        if (ImGui.button("Reload")) {
            UpdateDirectory();
        }

        String path = currentDirectory.getPath().replace("\\", " > ");
        ImGui.text(path);

        ImGui.endMenuBar();
    }

    private static void RenderFiles() {
        int index = 1;
        for (int i = 0; i < filesInCurrentDirectory.size(); i++) {
            File file = filesInCurrentDirectory.get(i);

            if (file == SelectedFile) {
                ImGui.pushStyleColor(ImGuiCol.FrameBg, ImColor.floatToColor(SelectedColor.r, SelectedColor.g, SelectedColor.b));
            }

            ImGui.beginChildFrame(index, 100, 110);
            if (ImGui.beginDragDropSource()) {
                ImGui.setDragDropPayload(file);
                ImGui.text("File: " + file.getName());
                ImGui.text("Type: " + FileUtility.GetFileExtension(file));

                ImGui.endDragDropSource();
            }

                int icon = file.isFile() ? GetIcon(file) : Folder;
                if (icon == 0) icon = File;

                ImGui.image(icon, 90, 80);
                ImGui.text(file.getName());

                if (file == SelectedFile) {
                    ImGui.popStyleColor();
                }

                if (ImGui.isMouseClicked(1)) {
                    if (ImGui.isWindowHovered() && SelectedFile != null) {
                        ImGui.openPopup("FileRightClick");
                        RightClickMenu = true;
                    }
                }
                if (RightClickMenu) {
                    RenderRightClick();
                }

                ImGui.endChildFrame();
                ImGui.sameLine();

            CheckActions(file);

            index++;
        }
    }

    private static void RenderRightClick() {
        if (ImGui.beginPopup("FileRightClick"))
        {
            if (ImGui.menuItem("Show In Explorer")) {
                try {
                    Desktop.getDesktop().open(SelectedFile.getParentFile());
                } catch (Exception e) {
                    Console.Error(e);
                }
            }

            if (ImGui.menuItem( "Delete")) {
                boolean deleted = SelectedFile.delete();
                SelectedFile = null;

                if (!deleted) {
                    Console.Log("Failed to delete file");
                }

                UpdateDirectory();
            }

            ImGui.endPopup();
        }
    }

    private static void CheckActions(File file) {
        if (Input.GetMouseButton(0) && ImGui.isItemHovered()) {
            if (file.isDirectory()) {
                SelectedFile = file;
            } else {
                String extension = FileUtility.GetFileExtension(file);

                SelectedFile = file;

                if (extension.equals("png") || extension.equals("jpg") || extension.equals("bmp")) {
                    SelectedImage = new Texture(SelectedFile.getPath()).textureID;
                }

                SceneHierarchy.current = null;
            }
        }

        if (ImGui.isMouseDoubleClicked(0) && ImGui.isItemHovered()) {
            if (SelectedFile.isDirectory()) {
                currentDirectory = SelectedFile;
                UpdateDirectory();
            } else {
                FileActions.getOrDefault(FileUtility.GetFileExtension(file), (File) -> {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (Exception e) {
                        Console.Error(e);
                    }
                }).accept(file);
            }
        }
    }

    private static void UpdateDirectory() {
        if (!currentDirectory.isDirectory()) return;
        filesInCurrentDirectory.clear();
        SelectedFile = null;

        for (File file : currentDirectory.listFiles()) {
            filesInCurrentDirectory.add(file);
        }
    }

    private static int GetIcon(File file) {
        return FileIcons.getOrDefault(file.getName().split("[.]")[1], 0);
    }

    private static void RegisterExtensions() {
        FileIcons.put("java", LoadTexture("EngineAssets/Editor/Explorer/java.png"));
        FileIcons.put("glsl", LoadTexture("EngineAssets/Editor/Explorer/shader.png"));

        FileIcons.put("fbx", LoadTexture("EngineAssets/Editor/Explorer/model.png"));
        FileIcons.put("obj", LoadTexture("EngineAssets/Editor/Explorer/model.png"));

        FileIcons.put("radiumscene", LoadTexture("EngineAssets/Textures/Icon/iconwhite.png"));
        FileIcons.put("radiummat", LoadTexture("EngineAssets/Editor/Explorer/material.png"));

        FileIcons.put("ttf", LoadTexture("EngineAssets/Editor/Explorer/font.png"));

        FileIcons.put("png", LoadTexture("EngineAssets/Editor/Explorer/picture.png"));
        FileIcons.put("jpg", LoadTexture("EngineAssets/Editor/Explorer/picture.png"));
        FileIcons.put("bmp", LoadTexture("EngineAssets/Editor/Explorer/picture.png"));

        FileIcons.put("mp3", LoadTexture("EngineAssets/Editor/Explorer/audio.png"));
        FileIcons.put("ogg", LoadTexture("EngineAssets/Editor/Explorer/audio.png"));
        FileIcons.put("wav", LoadTexture("EngineAssets/Editor/Explorer/audio.png"));
    }

    private static void RegisterActions() {
        FileActions.put("radiumscene", (File file) -> {
            SceneManager.SwitchScene(new Scene(file.getPath()));
        });
    }

    private static void RegisterFileGUI() {
        FileGUIRender.put("png", (File file) -> {
            ImGui.beginChildFrame(1, 300, 300);
            ImGui.image(SelectedImage, 300, 290);
            ImGui.endChildFrame();
        });
        FileGUIRender.put("jpg", (File file) -> {
            ImGui.beginChildFrame(1, 300, 300);
            ImGui.image(SelectedImage, 300, 290);
            ImGui.endChildFrame();
        });
        FileGUIRender.put("bmp", (File file) -> {
            ImGui.beginChildFrame(1, 300, 300);
            ImGui.image(SelectedImage, 300, 290);
            ImGui.endChildFrame();
        });

        FileGUIRender.put("radiumscene", (File file) -> {});
    }

    private static int LoadTexture(String path) {
        return new Texture(path).textureID;
    }

}