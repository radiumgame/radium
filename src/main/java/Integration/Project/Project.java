package Integration.Project;

import Radium.SceneManagement.Scene;
import Radium.SceneManagement.SceneManager;
import Radium.Util.FileUtility;
import Radium.Variables;
import RadiumEditor.EditorWindows.Lighting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

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

        LoadConfiguration();
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

    public static Project Current() {
        return INSTANCE;
    }

}
