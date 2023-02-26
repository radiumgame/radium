package Radium.Editor.Files;

import Radium.Editor.Console;
import Radium.Editor.EditorGUI;
import Radium.Engine.Graphics.Texture;
import imgui.ImColor;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FileSelector {

    private static boolean Open;
    private static List<File> currentOptions;
    private static List<Integer> currentIcons;
    private static IconType iconType;
    private static int iconIndex;
    private static File selectedFile;
    private static Consumer<File> callback;

    private static int DefaultIcon;
    private static int SearchIcon;
    private static String searchBar = "";
    private static boolean filtering = false;
    private static List<File> filteredFiles = new ArrayList<>();

    protected FileSelector() {}

    public static void Initialize() {
        DefaultIcon = new Texture("EngineAssets/Editor/Explorer/file.png", true).GetTextureID();
        SearchIcon = new Texture("EngineAssets/Editor/search.png", true).GetTextureID();
    }

    public static void Render() {
        if (!Open) return;

        ImGui.begin("File Selector");

        ImGui.image(SearchIcon, 23, 23);
        ImGui.sameLine();
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        String search = EditorGUI.InputString("##SearchBar", searchBar);
        if (!searchBar.equals(search)) {
            Search(search);
            searchBar = search;
        }
        filtering = !searchBar.equals("");

        ImGui.beginChildFrame("FileSelectionList".hashCode(), ImGui.getWindowWidth(), ImGui.getWindowHeight() - 100);
        boolean close = false;
        for (File f : (filtering ? filteredFiles : currentOptions)) {
            int icon = 0;
            if (iconType == IconType.None) icon = DefaultIcon;
            else if (iconType == IconType.Single) icon = currentIcons.get(0);
            else if (iconType == IconType.List) {
                icon = currentIcons.get(iconIndex);
                iconIndex++;
            }

            close = RenderFile(f, f.equals(selectedFile), icon, close);
        }
        ImGui.endChildFrame();
        if (ImGui.button("Cancel")) {
            Close();
        }
        ImGui.sameLine();
        if (ImGui.button("Select")) {
            callback.accept(selectedFile);
            Close();
        }

        if (close) {
            Close();
        }

        iconIndex = 0;
        ImGui.end();
    }

    private static final int FileFlags = ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.FramePadding;
    private static final int SelectedColor = ImColor.rgba(11f / 255f, 90f / 255f, 113f / 255f, 1f);

    private static boolean RenderFile(File file, boolean selected, int icon, boolean close) {
        if (selected) {
            ImGui.pushStyleColor(ImGuiCol.Header, SelectedColor);
            ImGui.pushStyleColor(ImGuiCol.HeaderActive, SelectedColor);
        }
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, SelectedColor);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, 5);

        final float padding = 25;
        ImVec2 ccp = ImGui.getCursorScreenPos();
        ImGui.setCursorScreenPos(ccp.x + padding, ccp.y);
        if (ImGui.treeNodeEx(file.getName(), FileFlags | (selected ? ImGuiTreeNodeFlags.Selected : 0))) {
            ImGui.treePop();
        }

        if (ImGui.isItemClicked()) {
            selectedFile = file;

            if (ImGui.isMouseDoubleClicked(0)) {
                callback.accept(selectedFile);
                close = true;
            }
        }

        ImVec2 ccp2 = ImGui.getCursorScreenPos();
        ImGui.setCursorScreenPos(ccp.x, ccp.y + 1.5f);
        ImGui.image(icon, 25, 25);
        ImGui.setCursorScreenPos(ccp2.x, ccp2.y);

        if (selected) {
            ImGui.popStyleColor(2);
        }
        ImGui.popStyleColor(1);
        ImGui.popStyleVar();

        return close;
    }

    private static void Search(String search) {
        filteredFiles.clear();

        for (File f : currentOptions) {
            if (f.getName().contains(search)) {
                filteredFiles.add(f);
            }
        }
    }

    public static void Open(List<File> selection, Consumer<File> cb) {
        if (Open) Close();

        Open = true;
        currentOptions = selection;
        callback = cb;
        iconType = IconType.None;
    }

    public static void Open(List<File> selection, int icon, Consumer<File> cb) {
        if (Open) Close();

        Open = true;
        currentOptions = selection;
        currentIcons = new ArrayList<>();
        currentIcons.add(icon);
        callback = cb;
        iconType = IconType.Single;
    }

    public static void Open(List<File> selection, List<Integer> icons, Consumer<File> cb) {
        if (Open) Close();

        Open = true;
        currentOptions = selection;
        currentIcons = icons;
        callback = cb;
        iconType = IconType.List;
    }

    public static void Close() {
        Open = false;
        selectedFile = null;
        callback = null;

        currentOptions = null;
        currentIcons = null;

        searchBar = "";
        filteredFiles.clear();
    }

    private static enum IconType {

        None,
        Single,
        List

    }

}
