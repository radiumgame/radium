package RadiumEditor.EditorWindows;

import Radium.Window;
import RadiumEditor.Console;
import RadiumEditor.EditorWindow;
import RadiumEditor.Gui;
import imgui.ImFont;
import imgui.ImGui;
import imgui.type.ImInt;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A simple asset manager
 */
public class AssetManager extends EditorWindow {

    private List<Package> packages;

    private String[] packagesArray;
    private ImInt selectedIndex = new ImInt(0);

    private String downloadLog = "";
    private float progress = 0;
    private boolean showProgress = false;
    private boolean finishedDownloading = false;

    /**
     * Creates empty instance
     */
    public AssetManager() {
        MenuName = "Asset Manager";

        LoadPackages();
    }

    @Override
    public void Start() {

    }

    @Override
    public void RenderGUI() {
        ImGui.setNextItemWidth(ImGui.getWindowWidth() / 3);
        ImGui.listBox("##NoLabelListBox", selectedIndex, packagesArray);

        ImGui.sameLine();
        ImGui.beginChild("AssetManagerChild", ImGui.getWindowWidth() / 1.5f, ImGui.getWindowHeight() - 75);
        ImGui.pushFont(Gui.largeFont);
        ImGui.text(packagesArray[selectedIndex.get()]);
        ImGui.popFont();

        ImGui.text(packages.get(selectedIndex.get()).description);
        if (ImGui.button("Download")) {
            packages.get(selectedIndex.get()).Download();
        }
        ImGui.sameLine();
        if (ImGui.button("Remove")) {
            packages.get(selectedIndex.get()).Remove();
        }
        ImGui.endChild();

        if (showProgress) {
            ImGui.openPopup("Package Downloader");
            showProgress = false;
        }
        RenderProgress();
    }

    private void RenderProgress() {
        ImGui.setNextWindowSize(600, 500);
        ImGui.setNextWindowPos((Window.width / 2) - 300, (Window.height / 2) - 250);
        if (ImGui.beginPopupModal("Package Downloader")) {
            ImGui.beginChildFrame(1, 300, 400);
            ImGui.text(downloadLog);
            ImGui.endChildFrame();

            ImGui.sameLine();
            ImGui.progressBar(progress);

            if (finishedDownloading) {
                if (ImGui.button("Done")) {
                    downloadLog = "";
                    progress = 0;
                    finishedDownloading = false;
                    ImGui.closeCurrentPopup();
                }
            }

            ImGui.endPopup();
        }
    }

    private void LoadPackages() {
        packages = new ArrayList<>();

        Package texturesPackage = new Package("https://github.com/radiumgame/radium-packages/raw/master/SampleTextures.zip", "Sample Textures", "Basic textures from ambientcg.com");
        packages.add(texturesPackage);

        UpdatePackages();
    }

    private void UpdatePackages() {
        packagesArray = new String[packages.size()];
        for (int i = 0; i < packagesArray.length; i++) {
            packagesArray[i] = packages.get(i).name;
        }
    }

    private class Package {

        public String name;
        public String url;
        public String description;

        public Package(String url, String name) {
            this.url = url;
            this.name = name;
            this.description = "A Radium Asset Package.";
        }

        public Package(String url, String name, String description) {
            this.url = url;
            this.name = name;
            this.description = description;
        }

        public void Download() {
            if (Files.exists(Paths.get("Assets/Packages/" + name + "/"))) {
                Console.Error("Package has already been downloaded.");
                return;
            }

            Thread downloadThread = new Thread(() -> {
                showProgress = true;

                try (BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream())) {
                    String[] splitURL = url.split("/");
                    String fileName = splitURL[splitURL.length - 1];

                    new File("Assets/Packages/" + name + "/").mkdir();
                    new File("Assets/Packages/" + name + "/" + fileName).createNewFile();

                    FileOutputStream fileOS = new FileOutputStream("Assets/Packages/" + name + "/" + fileName);
                    byte data[] = new byte[1024];
                    int byteContent;
                    downloadLog += "Downloading content...\n";

                    InputStream streamClone = new BufferedInputStream(new URL(url).openStream());
                    int length = streamClone.readAllBytes().length;
                    float fraction = 1024f / length;
                    while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                        fileOS.write(data, 0, byteContent);
                        progress += fraction;
                    }
                    fileOS.close();

                    progress = 0;
                    Extract(new File("Assets/Packages/" + name + "/" + fileName));
                    progress = 1;

                    finishedDownloading = true;
                    Thread.currentThread().stop();
                } catch (Exception e) {
                    Console.Error(e);
                }
            });
            downloadThread.start();
        }

        public void Remove() {
            if (!Files.exists(Paths.get("Assets/Packages/" + name + "/"))) {
                Console.Error("Package isn't installed");
                return;
            }

            DeleteDirectory("Assets/Packages/" + name + "/");
        }

        // https://www.journaldev.com/830/java-delete-file-directory#:~:text=Java%20delete%20file%201%20Java%20File%20delete%20%28%29,directory%20is%20by%20using%20Files.walkFileTree%20%28%29%20method.%20
        private void DeleteDirectory(String path) {
            try {
                Path directory = Paths.get(path);
                Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                        Files.delete(file); // this will work because it's always a File
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir); //this will work because Files in the directory are already deleted
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (Exception e) {
                Console.Error(e);
            }
        }

        private void Extract(File file) {
            if (!file.getName().endsWith(".zip")) {
                return;
            }

            downloadLog += "Unzipping contents...\n";

            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
                ZipEntry zipEntry = zis.getNextEntry();
                while (zipEntry != null) {
                    progress += 0.05f;

                    boolean isDirectory = false;
                    if (zipEntry.getName().endsWith(File.separator)) {
                        isDirectory = true;
                    }

                    Path newPath = ZipSlipProtect(zipEntry, Paths.get(file.getParent()));
                    if (isDirectory) {
                        Files.createDirectories(newPath);
                        downloadLog += "Creating directory " + newPath.getFileName() + "\n";
                    } else {
                        if (newPath.getParent() != null) {
                            if (Files.notExists(newPath.getParent())) {
                                Files.createDirectories(newPath.getParent());
                            }

                            downloadLog += "Creating file " + newPath.getFileName() + "\n";
                        }

                        Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                    }

                    zipEntry = zis.getNextEntry();

                }
                zis.closeEntry();
            } catch (Exception e) {
                Console.Error(e);
            }

            downloadLog += "Deleting original ZIP file...\n";
            boolean delete = file.delete();
            if (!delete) {
                Console.Error("Failed to delete original ZIP file");
            }

            downloadLog += "Finished!\n";
        }

        private Path ZipSlipProtect(ZipEntry zipEntry, Path targetDir) throws IOException {
            Path targetDirResolved = targetDir.resolve(zipEntry.getName());

            Path normalizePath = targetDirResolved.normalize();
            if (!normalizePath.startsWith(targetDir)) {
                throw new IOException("Bad zip entry: " + zipEntry.getName());
            }

            return normalizePath;
        }

    }

}
