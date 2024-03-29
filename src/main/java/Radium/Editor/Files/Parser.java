package Radium.Editor.Files;

import Radium.Engine.Graphics.Texture;
import Radium.Engine.Util.FileUtility;
import Radium.Integration.Project.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    public static List<File> all = new ArrayList<>();

    public static List<File> images = new ArrayList<>();
    public static List<Integer> loadedImages = new ArrayList<>();
    private static final String[] imageExtensions = new String[] { "png", "jpg", "bmp" };

    public static List<File> animations = new ArrayList<>();
    private static final String[] animationExtensions = new String[] { "anim" };

    public static List<File> audio = new ArrayList<>();
    private static final String[] audioExtensions = new String[] { "wav", "ogg" };

    public static List<File> prefabs = new ArrayList<>();
    private static final String[] prefabExtension = new String[] { "prefab" };

    public static List<File> nodeGraphs = new ArrayList<>();
    private static final String[] nodeGraphExtensions = new String[] { "graph" };

    protected Parser() {}

    public static void ParseAll() {
        File projectRoot = Project.Current().assetsDirectory;
        FileSearch(projectRoot, all);

        for (File f : all) {
            String extension = FileUtility.GetFileExtension(f);

            if (FileUtility.IsFileType(f, extension, imageExtensions)) {
                images.add(f);
            } else if (FileUtility.IsFileType(f, extension, animationExtensions)) {
                animations.add(f);
            } else if (FileUtility.IsFileType(f, extension, audioExtensions)) {
                audio.add(f);
            } else if (FileUtility.IsFileType(f, extension, prefabExtension)) {
                prefabs.add(f);
            } else if (FileUtility.IsFileType(f, extension, nodeGraphExtensions)) {
                nodeGraphs.add(f);
            }
        }
    }

    public static void LoadImages() {
        for (File image : images) {
            loadedImages.add(new Texture(image.getAbsolutePath(), true).GetTextureID());
        }
    }

    public static void UpdateGraphs() {
        nodeGraphs.clear();
        File projectRoot = Project.Current().assetsDirectory;

        List<File> newGraphs = new ArrayList<>();
        FileSearch(projectRoot, newGraphs);

        for (File f : newGraphs) {
            if (FileUtility.IsFileType(f, nodeGraphExtensions)) {
                nodeGraphs.add(f);
            }
        }
    }

    private static void FileSearch(File directory, List<File> allFiles) {
        for (File f : directory.listFiles()) {
            if (f.isFile()) {
                allFiles.add(f);
            } else {
                FileSearch(f, allFiles);
            }
        }
    }

}
