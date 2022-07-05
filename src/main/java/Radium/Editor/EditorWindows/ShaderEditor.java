package Radium.Editor.EditorWindows;

import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.PostProcessing.CustomPostProcessingEffect;
import Radium.Engine.Util.FileUtility;
import Radium.Editor.EditorWindow;
import imgui.ImGui;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorLanguageDefinition;

import java.io.File;

public class ShaderEditor extends EditorWindow {

    private TextEditor editor;
    private File openFile;
    private CustomPostProcessingEffect openEffect;

    public ShaderEditor() {
        MenuName = "Shader Editor";

        editor = new TextEditor();
        editor.setLanguageDefinition(TextEditorLanguageDefinition.glsl());
    }

    
    public void Start() {

    }

    
    public void RenderGUI() {
        if (openFile == null) {
            ImGui.text("Please Open a File");
            return;
        }

        ImGui.beginMenuBar();
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save/Compile")) {
                Save();
            }

            ImGui.endMenu();
        }
        ImGui.endMenuBar();

        editor.render("Shader Editor");
    }

    public void Open(String path, CustomPostProcessingEffect effect) {
        File f = new File(path);
        String contents = FileUtility.ReadFile(f);
        editor.setText(contents);

        openFile = f;
        openEffect = effect;

        SetWindowName("Shader Editor | " + openFile.getName());
    }

    private void Save() {
        String contents = editor.getText();
        FileUtility.Write(openFile, contents);

        openEffect.shader = new Shader("EngineAssets/Shaders/PostProcessing/vert.glsl", openEffect.shaderPath);
    }

}
