package Integration.Project;

import Radium.SceneManagement.Scene;
import Radium.SceneManagement.SceneManager;
import Radium.System.FileExplorer;
import Radium.System.Popup;
import Radium.Util.FileUtility;
import Radium.Variables;
import Radium.Window;
import RadiumEditor.Console;
import RadiumEditor.EditorWindows.Lighting;
import RadiumEditor.Metadata.FileMetadata;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Project {

    public String name;

    public File rootDirectory;
    public String root;

    public File assetsDirectory;
    public String assets;

    public File config;
    public ProjectConfiguration configuration;

    public HashMap<File, FileMetadata> metadata = new HashMap<>();

    private static Project INSTANCE;

    public Project(String root) {
        INSTANCE = this;

        this.rootDirectory = new File(root);
        this.root = root;

        this.assets = root + "/Assets/";
        this.assetsDirectory = new File(this.assets);

        this.name = rootDirectory.getName();

        config = new File(root + "/" + name + ".config");
        if (!config.exists()) {
            Popup.ErrorPopup("This is not a Radium project, please select another folder.");

            String directory = FileExplorer.ChooseDirectory();
            if (directory == null) {
                Window.Close();
                System.exit(0);
            }
            new Project(directory);

            return;
        }

        LoadConfiguration();
        CreateMetadata();
    }

    public void SaveConfiguration() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(configuration);

        FileUtility.Write(config, json);
    }

    public void LoadConfiguration() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String src = FileUtility.ReadFile(config);

        configuration = gson.fromJson(src, ProjectConfiguration.class);
    }

    public void ApplyConfiguration() {
        SceneManager.SwitchScene(new Scene(configuration.openScene));
        Variables.EditorCamera.transform = configuration.editorCameraTransform;

        Lighting.LoadLightingSettings();
    }

    public void CreateMetadata() {
        SearchDirectory(assetsDirectory);
    }

    public void SearchDirectory(File directory) {
        for (File f : directory.listFiles()) {
            String extension = FileUtility.GetFileExtension(f);
            if (extension.equals("metadata")) continue;

            if (f.isDirectory()) {
                SearchDirectory(f);
                continue;
            }

            File metadata = new File(f.getPath() + ".metadata");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            if (metadata.exists()) {
                String json = FileUtility.ReadFile(metadata);
                this.metadata.put(metadata, gson.fromJson(json, FileMetadata.class));

                continue;
            }

            try {
                metadata.createNewFile();
                FileMetadata fileMetadata = new FileMetadata(f);
                FileUtility.Write(metadata, gson.toJson(fileMetadata));

                this.metadata.put(metadata, fileMetadata);
            } catch (Exception e) {
                Console.Error(e);
            }
        }
    }

    public static FileMetadata GetMetadata(File f) {
        return INSTANCE.metadata.getOrDefault(f, new FileMetadata(f));
    }

    public void UpdateAllMetadata() {
        for (File f : metadata.keySet()) {
            UpdateMetadata(f);
        }
    }

    public void UpdateMetadata(File f) {
        try {
            if (!metadata.containsKey(f)) {
                CreateMetadata(f);
                return;
            }

            if (Files.deleteIfExists(Paths.get(f.getPath() + ".metadata"))) return;

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileMetadata metadata = this.metadata.get(f);
            FileUtility.Write(f, gson.toJson(metadata));
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    private void CreateMetadata(File f) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileMetadata md = new FileMetadata(f);
        File newFile = new File(f.getPath() + ".metadata");
        newFile.createNewFile();
        FileUtility.Write(newFile, gson.toJson(md));

        metadata.put(f, md);
    }

    public static boolean IsProjectFile(File f) {
        return INSTANCE.metadata.containsKey(f);
    }

    public static Project Current() {
        return INSTANCE;
    }

}
