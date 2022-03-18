package Radium.Components.Rendering;

import Radium.Color;
import Radium.Component;
import Radium.Graphics.Shader;
import Radium.PerformanceImpact;
import Radium.PostProcessing.CustomPostProcessingEffect;
import Radium.PostProcessing.PostProcessingEffect;
import Radium.System.FileExplorer;
import RadiumEditor.*;
import RadiumEditor.Annotations.RangeFloat;
import RadiumEditor.Annotations.RangeInt;
import RadiumEditor.EditorWindows.ShaderEditor;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import org.apache.commons.text.WordUtils;

import java.io.File;

import java.io.FileWriter;
import java.lang.reflect.Field;

public class PostProcessing extends Component {

    public PostProcessing() {
        LoadIcon("post-processing.png");

        name = "Post Processing";
        submenu = "Rendering";

        impact = PerformanceImpact.Low;
        description = "Can apply visual effects to scene";
    }

    @Override
    public void GUIRender() {
        int flags = ImGuiTreeNodeFlags.SpanAvailWidth;

        if (ImGui.treeNodeEx("Effects", flags)) {
            for (int i = 0; i < Radium.PostProcessing.PostProcessing.GetEffects().size(); i++) {
                PostProcessingEffect effect = Radium.PostProcessing.PostProcessing.GetEffects().get(i);

                if (ImGui.button("Remove##" + i)) {
                    Radium.PostProcessing.PostProcessing.RemoveEffect(effect);
                }
                ImGui.sameLine();
                if (ImGui.treeNodeEx(WordUtils.capitalize(effect.name), flags)) {
                    for (Field field : effect.fields) {
                        RenderField(effect, field);
                    }

                    ImGui.treePop();
                }
            }
            for (int i = 0; i < Radium.PostProcessing.PostProcessing.customEffects.size(); i++) {
                CustomPostProcessingEffect effect = Radium.PostProcessing.PostProcessing.customEffects.get(i);

                if (ImGui.button("Remove##" + i)) {
                    Radium.PostProcessing.PostProcessing.customEffects.remove(effect);
                }
                ImGui.sameLine();
                if (ImGui.button("Open##" + i)) {
                    OpenFile(effect.shaderPath, effect);
                }
                ImGui.sameLine();
                if (ImGui.treeNodeEx(WordUtils.capitalize(effect.name), flags)) {
                    ImGui.treePop();
                }
            }

            ImGui.treePop();
        }

        if (ImGui.button("Add Effect", ImGui.getWindowWidth() - 30, 25)) {
            ImGui.openPopup("AddEffect");
        }

        AddPopup();
    }

    private void OpenFile(String path, CustomPostProcessingEffect effect) {
        ShaderEditor shaderEditor = null;
        for (EditorWindow window : Editor.GetAllEditorWindows()) {
            if (window.getClass() == ShaderEditor.class) {
                shaderEditor = (ShaderEditor)window;
                break;
            }
        }

        shaderEditor.Render = true;
        shaderEditor.Open(path, effect);
    }

    private void AddPopup() {
        ImGui.setNextWindowSize(250, 200);
        if (ImGui.beginPopup("AddEffect")) {
            for (PostProcessingEffect effect : Radium.PostProcessing.PostProcessing.effectList) {
                if (ImGui.treeNodeEx(WordUtils.capitalize(effect.name), ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Leaf)) {
                    ImGui.treePop();
                }

                if (ImGui.isItemClicked(0)) {
                    try {
                        PostProcessingEffect newEffect = effect.getClass().getDeclaredConstructor().newInstance();
                        Radium.PostProcessing.PostProcessing.AddEffect(newEffect);
                    } catch (Exception e) {
                        Console.Error(e);
                    }
                }
            }

            if (ImGui.treeNodeEx("Custom Effect", ImGuiTreeNodeFlags.SpanAvailWidth)) {
                if (ImGui.treeNodeEx("Create", ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Leaf)) {
                    ImGui.treePop();
                }
                if (ImGui.isItemClicked(0)) {
                    String customFrag = FileExplorer.Create("glsl");
                    if (customFrag != null) {
                        CreateShader(customFrag);
                    }

                    ProjectExplorer.Refresh();
                    ImGui.closeCurrentPopup();
                }

                if (ImGui.treeNodeEx("Choose", ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Leaf)) {
                    ImGui.treePop();
                }
                if (ImGui.isItemClicked(0)) {
                    String customFrag = FileExplorer.Choose("glsl");
                    if (customFrag != null) {
                        Radium.PostProcessing.PostProcessing.customEffects.add(new CustomPostProcessingEffect(customFrag));
                    }

                    ProjectExplorer.Refresh();
                    ImGui.closeCurrentPopup();
                }

                ImGui.treePop();
            }

            ImGui.endPopup();
        }
    }

    private String basicShader = "#version 330\n\nin vec2 texCoords;\n\nuniform sampler2D screenTexture;\nuniform float time;\n\nout vec4 outColor;\n\nvoid main() {\noutColor = texture(screenTexture, texCoords);\n}";
    private void CreateShader(String path) {
        try {
            File file = new File(path);
            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            writer.write(basicShader);
            writer.close();

            Shader shader = Compile(file);
            CustomPostProcessingEffect effect = new CustomPostProcessingEffect(file.getPath(), shader);
            Radium.PostProcessing.PostProcessing.customEffects.add(effect);
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    private Shader Compile(File file) {
        return new Shader("EngineAssets/Shaders/PostProcessing/vert.glsl", file.getPath());
    }

    private void RenderField(PostProcessingEffect effect, Field field) {
        try {
            Object value = field.get(effect);
            String name = WordUtils.capitalize(field.getName());
            boolean range = field.isAnnotationPresent(RangeInt.class) || field.isAnnotationPresent(RangeFloat.class);

            if (value.getClass() == Integer.class) {
                if (range) {
                    RangeInt intRange = field.getAnnotation(RangeInt.class);
                    int val = EditorGUI.SliderInt(name, (int)value, intRange.min(), intRange.max());
                    field.set(effect, val);
                    return;
                }

                int val = EditorGUI.DragInt(name, (int)value);
                field.set(effect, val);
            } else if (value.getClass() == Float.class) {
                if (range) {
                    RangeFloat floatRange = field.getAnnotation(RangeFloat.class);
                    float val = EditorGUI.SliderFloat(name, (float)value, floatRange.min(), floatRange.max());
                    field.set(effect, val);
                    return;
                }

                float val = EditorGUI.DragFloat(name, (float)value);
                field.set(effect, val);
            } else if (value.getClass() == Color.class) {
                Color val = EditorGUI.ColorField(name, (Color)value);
                field.set(effect, val);
            }
        } catch (Exception e) {
            Console.Error(e);
        }
    }

}
