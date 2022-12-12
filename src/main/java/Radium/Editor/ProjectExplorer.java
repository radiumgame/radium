package Radium.Editor;

import Radium.Integration.Project.AssetsListener;
import Radium.Integration.Project.Project;
import Radium.Integration.Project.ProjectFiles;
import Radium.Engine.Audio.AudioClip;
import Radium.Engine.Color.Color;
import Radium.Engine.Components.Graphics.MeshFilter;
import Radium.Engine.Graphics.Mesh;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.ModelLoader;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.SceneManagement.Scene;
import Radium.Engine.SceneManagement.SceneManager;
import Radium.Engine.System.FileExplorer;
import Radium.Engine.Util.FileUtility;
import Radium.Editor.Im3D.Im3D;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;

import java.awt.*;
import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Where files and folder are rendered
 */
public class ProjectExplorer {

    private static File currentDirectory;
    private static List<File> filesInCurrentDirectory = new ArrayList<>();

    /**
     * Currently selected file
     */
    public static File SelectedFile;
    private static File lastSelectedFile;

    private static int File, Folder, BackArrow;
    private static Hashtable<String, Integer> FileIcons = new Hashtable<>();
    private static Hashtable<String, Consumer<File>> FileActions = new Hashtable<>();

    /**
     * When selected will try to get the GUI rendering information about file
     */
    public static Hashtable<String, Consumer<File>> FileGUIRender = new Hashtable<>();

    private static final Color SelectedColor = new Color(80 / 255f, 120 / 255f, 237 / 255f);

    private static boolean RightClickMenu = false;
    private static ProjectFiles assets;

    private static final Hashtable<File, Texture> Textures = new Hashtable<>();
    public static final Hashtable<File, Integer> Im3DMeshes = new Hashtable<>();
    private static final Hashtable<File, AudioClip> Audio = new Hashtable<>();

    private static List<File> folders = new ArrayList<>();
    private static Hashtable<File, String> files = new Hashtable<>();

    protected ProjectExplorer() {}

    /**
     * Initialize textures
     */
    public static void Initialize() {
        currentDirectory = new File(Project.Current().assets);
        CreateListener();

        File = new Texture("EngineAssets/Editor/Explorer/file.png").GetTextureID();
        Folder = new Texture("EngineAssets/Editor/Explorer/folder.png").GetTextureID();
        BackArrow = new Texture("EngineAssets/Editor/Explorer/backarrow.png").GetTextureID();
        RegisterExtensions();
        RegisterActions();
        RegisterFileGUI();

        UpdateDirectory();
    }

    /**
     * Refreshes files
     */
    public static void Refresh() {
        UpdateDirectory();
    }

    /**
     * Render editor window
     */
    public static void Render() {
        ImGui.begin("Project Explorer", ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.AlwaysAutoResize);

        RenderMenuBar();
        ImGui.beginChild("##PROJECT_EXPLORER_CHILD");

        RenderFiles();
        if (ImGui.isWindowHovered() && ImGui.isMouseReleased(0)) {
            SelectedFile = null;
        }

        ImGui.endChild();
        if (ImGui.isItemClicked(1)) {
            RightClickMenu = true;
            ImGui.openPopup("WindowRightClick");
        }

        if (RightClickMenu) {
            RenderRightClick();
        }

        if (lastSelectedFile != SelectedFile) {
            if (lastSelectedFile != null) OnChangeSelected(lastSelectedFile);
            lastSelectedFile = SelectedFile;
        }
        ImGui.end();
    }

    private static void RenderMenuBar() {
        ImGui.beginMenuBar();

        if (ImGui.imageButton(BackArrow, 20, 17)) {
            String[] back = currentDirectory.getPath().split(Pattern.quote("\\"));

            if (back.length > 2) {
                currentDirectory = currentDirectory.getParentFile();
                UpdateDirectory();
                CreateListener();
            }
        }

        if (ImGui.button("Home")) {
            currentDirectory = Project.Current().assetsDirectory;
            UpdateDirectory();
            CreateListener();
        }
        if (ImGui.button("Reload")) {
            UpdateDirectory();
        }

        String path = currentDirectory.getPath().replace("\\", " > ");
        ImGui.text(path);

        ImGui.endMenuBar();
    }

    private static void RenderFiles() {
        int index = 0;
        for (java.io.File folder : folders) {
            RenderFile(folder, false,index);
            index++;
        }
        for (java.io.File file : files.keySet()) {
            RenderFile(file, true,index);
            index++;
        }
    }

    private static void RenderFile(File file, boolean isFile, int i) {
        float remainingSpace = ImGui.getContentRegionAvail().x - 20;
        if (remainingSpace < 100) {
            ImGui.newLine();
        }

        if (file == SelectedFile) {
            ImGui.pushStyleColor(ImGuiCol.FrameBg, ImColor.floatToColor(SelectedColor.r, SelectedColor.g, SelectedColor.b));
        }

        ImGui.beginChildFrame(i + 1, 100, 110);
        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(file);

            ImGui.text("File: " + file.getName());
            ImGui.text("Type: " + FileUtility.GetFileExtension(file));

            ImGui.endDragDropSource();
        }

        int icon = isFile ? GetIcon(file) : Folder;
        if (icon == 0) icon = File;

        if (isFile) {
            String extensions = files.get(file);
            if (extensions.equals("png") || extensions.equals("jpg") || extensions.equals("jpeg")) {
                if (Textures.containsKey(file)) {
                    icon = Textures.get(file).GetTextureID();
                } else {
                    Texture texture = new Texture(file.getPath());
                    Textures.put(file, texture);
                    icon = texture.GetTextureID();
                }
            }
            if (extensions.equals("fbx") || extensions.equals("obj") || extensions.equals("gltf")) {
                if (!Im3DMeshes.containsKey(file)) {
                    GameObject obj = ModelLoader.LoadModelNoMultiThread(file.getPath(), false);
                    Mesh m = ScopeMesh(obj);

                    if (obj.ContainsComponent(MeshFilter.class)) {
                        m = obj.GetComponent(MeshFilter.class).mesh;
                    }

                    Im3DMeshes.put(file, Im3D.AddMesh(m));
                }
            }
            if (extensions.equals("ogg") || extensions.equals("wav")) {
                if (!Audio.containsKey(file)) {
                    int source = Radium.Engine.Audio.Audio.LoadAudio(file.getPath());

                    Audio.put(file, new AudioClip(source, file));
                }
            }
        }

        ImGui.image(icon, 90, 80);
        ImGui.text(file.getName());

        if (file == SelectedFile) {
            ImGui.popStyleColor();
        }

        if (ImGui.isMouseReleased(1)) {
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
    }

    private static Mesh ScopeMesh(GameObject obj) {
        if (obj == null) return null;

        MeshFilter meshFilter = obj.GetComponent(MeshFilter.class);
        if (meshFilter != null && meshFilter.mesh != null) {
            return meshFilter.mesh;
        }

        for (GameObject child : obj.GetChildren()) {
            MeshFilter mf = child.GetComponent(MeshFilter.class);
            if (mf != null && mf.mesh != null) {
                return mf.mesh;
            }

            Mesh m = ScopeMesh(child);
            if (m != null) return m;
        }

        return Mesh.Empty();
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
        if (ImGui.beginPopup("WindowRightClick")) {
            if (ImGui.beginMenu("New")) {
                if (ImGui.menuItem("Python Script")) {
                    ImGui.closeCurrentPopup();
                    CreateFile("py", "def start():\n    pass\n\ndef update():\n    pass");
                }

                ImGui.endMenu();
            }
            if (ImGui.menuItem("Show In Explorer")) {
                try {
                    Desktop.getDesktop().open(currentDirectory);
                } catch (Exception e) {
                    Console.Error(e);
                }
            }

            ImGui.endPopup();
        }
    }

    private static void CheckActions(File file) {
        if (ImGui.isMouseDragging(0)) return;
        if (ImGui.isMouseReleased(0) && ImGui.isItemHovered()) {
            if (file.isDirectory()) {
                SelectedFile = file;
            } else {
                SelectedFile = file;
                SceneHierarchy.current = null;
            }
        }

        if (ImGui.isMouseDoubleClicked(0) && ImGui.isItemHovered()) {
            if (SelectedFile.isDirectory()) {
                currentDirectory = SelectedFile;
                UpdateDirectory();
                CreateListener();
            }
        }
    }

    private static void UpdateDirectory() {
        if (!currentDirectory.isDirectory()) return;
        filesInCurrentDirectory.clear();
        SelectedFile = null;

        for (File f : currentDirectory.listFiles()) {
            String extension = FileUtility.GetFileExtension(f);
            if (extension.equals("metadata")) continue;

            filesInCurrentDirectory.add(f);
        }

        for (int i = 0; i < filesInCurrentDirectory.size(); i++) {
            File file = filesInCurrentDirectory.get(i);
            if (file.isFile()) {
                files.put(file, FileUtility.GetFileExtension(file));
            } else {
                folders.add(file);
            }
        }
    }

    private static void CreateFile(String extension, String content) {
        String path = FileExplorer.Create("py", currentDirectory.getAbsolutePath());
        if (FileExplorer.IsPathValid(path)) {
            try {
                File f = new File(path);
                f.createNewFile();
                FileUtility.Write(f, content);
            } catch (Exception e) {
                Console.Error(e);
            }
        }
    }

    private static int GetIcon(File file) {
        return FileIcons.getOrDefault(FileUtility.GetFileExtension(file), 0);
    }

    private static void RegisterExtensions() {
        FileIcons.put("java", LoadTexture("EngineAssets/Editor/Explorer/java.png"));
        FileIcons.put("glsl", LoadTexture("EngineAssets/Editor/Explorer/shader.png"));

        FileIcons.put("fbx", LoadTexture("EngineAssets/Editor/Explorer/model.png"));
        FileIcons.put("obj", LoadTexture("EngineAssets/Editor/Explorer/model.png"));

        FileIcons.put("radium", LoadTexture("EngineAssets/Textures/Icon/icon.png"));
        FileIcons.put("py", LoadTexture("EngineAssets/Editor/Icons/python.png"));
        FileIcons.put("ttf", LoadTexture("EngineAssets/Editor/Explorer/font.png"));

        FileIcons.put("png", LoadTexture("EngineAssets/Editor/Explorer/picture.png"));
        FileIcons.put("jpg", LoadTexture("EngineAssets/Editor/Explorer/picture.png"));
        FileIcons.put("bmp", LoadTexture("EngineAssets/Editor/Explorer/picture.png"));

        FileIcons.put("mp3", LoadTexture("EngineAssets/Editor/Explorer/audio.png"));
        FileIcons.put("ogg", LoadTexture("EngineAssets/Editor/Explorer/audio.png"));
        FileIcons.put("wav", LoadTexture("EngineAssets/Editor/Explorer/audio.png"));

        FileIcons.put("anim", LoadTexture("EngineAssets/Editor/Explorer/animation.png"));
    }

    private static void RegisterActions() {
        FileActions.put("radium", (File file) -> {
            SceneManager.SwitchScene(new Scene(file.getPath()));
        });
    }

    private static void RegisterFileGUI() {
        FileGUIRender.put("png", (File file) -> {
            Texture tex = Textures.get(file);
            Vector2 size = CalcImageSize(tex);

            ImGui.image(Textures.get(file).GetTextureID(), size.x, size.y);
        });
        FileGUIRender.put("jpg", (File file) -> {
            Texture tex = Textures.get(file);
            Vector2 size = CalcImageSize(tex);

            ImGui.image(Textures.get(file).GetTextureID(), size.x, size.y);
        });
        FileGUIRender.put("bmp", (File file) -> {
            Texture tex = Textures.get(file);
            Vector2 size = CalcImageSize(tex);

            ImGui.image(Textures.get(file).GetTextureID(), size.x, size.y);
        });
        FileGUIRender.put("fbx", (File file) -> {
            Im3D.SetRenderMesh(Im3DMeshes.get(file), true);

            Im3D.Viewer(Im3DMeshes.get(file), new Vector2(356, 200));
            ImGui.textColored(1.0f, 1.0f, 0.0f, 1.0f, "Warning: Only Locates 1st Mesh");
        });
        FileGUIRender.put("obj", (File file) -> {
            Im3D.SetRenderMesh(Im3DMeshes.get(file), true);

            Im3D.Viewer(Im3DMeshes.get(file), new Vector2(356, 200));
            ImGui.textColored(1.0f, 1.0f, 0.0f, 1.0f, "Warning: Only Locates 1st Mesh");
        });
        FileGUIRender.put("ogg", (File file) -> {
            EditorGUI.AudioPlayer(Audio.get(file));
        });
        FileGUIRender.put("wav", (File file) -> {
            EditorGUI.AudioPlayer(Audio.get(file));
        });
        FileGUIRender.put("mp3", (File file) -> {
            ImGui.text("MP3 is not supported, to use you must convert the file.");
            if (ImGui.button("Convert To WAV")) {
                Radium.Engine.Audio.Audio.Mp3ToWav(file);
                Console.Log("Conversion of " + file.getName() + " to WAV successful");
            }
        });

        FileGUIRender.put("radium", (File file) -> {});
    }

    private static Vector2 CalcImageSize(Texture texture) {
        float availWidth = ImGui.getContentRegionAvailX();
        float width = texture.width;
        float height = texture.height;

        return new Vector2(availWidth, height * (availWidth / width));
    }

    private static int LoadTexture(String path) {
        return new Texture(path).GetTextureID();
    }

    private static void OnChangeSelected(File f) {
        if (Im3DMeshes.containsKey(f)) {
            Im3D.SetRenderMesh(Im3DMeshes.get(f), false);
        }

        if (Audio.containsKey(f)) {
            AudioClip clip = Audio.get(f);
            if (clip.playing) {
                clip.Stop();
            }
        }
    }

    private static void CreateListener() {
        if (assets != null) {
            assets.Destroy();
        }

        assets = new ProjectFiles();
        assets.Initialize(currentDirectory.getPath());
        assets.RegisterListener(new AssetsListener() {
            @Override
            public void OnFileCreated(java.io.File file) {
                UpdateDirectory();
            }

            @Override
            public void OnFileDeleted(java.io.File file) {
                UpdateDirectory();
            }

            @Override
            public void OnFileChanged(java.io.File file) {

            }
        });
    }

}