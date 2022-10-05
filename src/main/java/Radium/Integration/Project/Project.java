package Radium.Integration.Project;

import Radium.Editor.Console;
import Radium.Engine.Math.Transform;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.SceneManagement.Scene;
import Radium.Engine.SceneManagement.SceneManager;
import Radium.Engine.Serialization.Serializer;
import Radium.Engine.Serialization.TypeAdapters.FileDeserializer;
import Radium.Engine.Serialization.TypeAdapters.FileSerializer;
import Radium.Engine.Serialization.TypeAdapters.StringDeserializer;
import Radium.Engine.Serialization.TypeAdapters.StringSerializer;
import Radium.Engine.System.FileExplorer;
import Radium.Engine.System.Popup;
import Radium.Engine.Util.FileUtility;
import Radium.Engine.Variables;
import Radium.Engine.Window;
import Radium.Editor.EditorWindows.Lighting;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Project {

    public String name;

    public File rootDirectory;
    public String root;

    public File assetsDirectory;
    public String assets;

    public File config;
    public ProjectConfiguration configuration;

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
            if (!FileExplorer.IsPathValid(directory)) {
                Window.Close();
                System.exit(0);
            }
            new Project(directory);

            return;
        }

        LoadConfiguration();
        if (configuration == null) {
            configuration = new ProjectConfiguration();
            configuration.editorCameraTransform = new Transform();

            File f = new File(Project.Current().assets + "/" + UUID.randomUUID().toString() + ".radium");
            try {
                f.createNewFile();
                FileUtility.Write(f, Serializer.GetMapper().writeValueAsString(new GameObject[0]));
            } catch (Exception e) { e.printStackTrace(); }
            configuration.openScene = f.getAbsolutePath();
        }
    }

    public void SaveConfiguration() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
            SimpleModule module = new SimpleModule();
            module.addSerializer(new FileSerializer());
            module.addSerializer(new StringSerializer());
            mapper.registerModule(module);
            String json = mapper.writeValueAsString(configuration);

            FileUtility.Write(config, json);
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public void LoadConfiguration() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
            SimpleModule module = new SimpleModule();
            module.addDeserializer(File.class, new FileDeserializer());
            module.addDeserializer(String.class, new StringDeserializer());
            mapper.registerModule(module);
            String src = FileUtility.ReadFile(config);

            configuration = mapper.readValue(src, ProjectConfiguration.class);
            if (configuration.openScene == null) {
                File sceneFile = ScopeProject("radium");
                if (sceneFile != null) {
                    configuration.openScene = sceneFile.getAbsolutePath();
                } else {
                    File f = new File(assets + "/scene.radium");
                    try {
                        f.createNewFile();
                    } catch (Exception e) { e.printStackTrace(); }
                    FileUtility.Write(f, "[]");
                }
            }
            if (configuration.projectName == null) {
                configuration.projectName = name;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ApplyConfiguration() {
        SceneManager.SwitchScene(new Scene(configuration.openScene));
        Variables.EditorCamera.transform = configuration.editorCameraTransform;

        Lighting.LoadLightingSettings();
    }

    private File ScopeProject(String fileExtension) {
        return ScopeFolder(assets, fileExtension);
    }

    private File ScopeFolder(String directory, String fileExtension) {
        File folder = new File(directory);

        List<File> files = new ArrayList<>();
        List<File> directories = new ArrayList<>();
        for (File f : folder.listFiles()) {
            if (f.isFile()) files.add(f);
            else directories.add(f);
        }

        for (File f : files) {
            if (FileUtility.GetFileExtension(f).equals(fileExtension)) {
                return f;
            }
        }

        for (File f : directories) {
            File returnFile = ScopeFolder(f.getAbsolutePath(), fileExtension);
            if (returnFile != null) return returnFile;
        }

        return null;
    }

    public static Project Current() {
        return INSTANCE;
    }

}
