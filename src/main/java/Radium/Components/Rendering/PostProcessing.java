package Radium.Components.Rendering;

import Radium.Color;
import Radium.Component;
import Radium.Graphics.Shader;
import Radium.Math.Random;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.PerformanceImpact;
import Radium.PostProcessing.CustomPostProcessingEffect;
import Radium.PostProcessing.EffectUniform;
import Radium.PostProcessing.Effects.Tint;
import Radium.PostProcessing.PostProcessingEffect;
import Radium.PostProcessing.UniformType;
import Radium.System.FileExplorer;
import Radium.Util.EnumUtility;
import Radium.Util.FileUtility;
import RadiumEditor.*;
import RadiumEditor.Annotations.RangeFloat;
import RadiumEditor.Annotations.RangeInt;
import RadiumEditor.Annotations.RunInEditMode;
import RadiumEditor.EditorWindows.ShaderEditor;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImInt;
import org.apache.commons.text.WordUtils;
import org.lwjgl.system.CallbackI;

import java.io.File;

import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@RunInEditMode
public class PostProcessing extends Component {

    public List<PostProcessingEffect> effects = null;
    public List<CustomPostProcessingEffect> custom = null;

    private String[] defaultUniforms = {
            "time", "screenTexture"
    };

    public PostProcessing() {
        LoadIcon("post-processing.png");

        name = "Post Processing";
        submenu = "Rendering";

        impact = PerformanceImpact.Low;
        description = "Can apply visual effects to scene";
    }

    
    public void Update() {
        effects = Radium.PostProcessing.PostProcessing.GetEffects();
        custom = Radium.PostProcessing.PostProcessing.customEffects;
    }

    
    public void OnRemove() {
        for (int i = 0; i < effects.size(); i++) {
            Radium.PostProcessing.PostProcessing.RemoveEffect(effects.get(i));
        }
        Radium.PostProcessing.PostProcessing.customEffects.clear();
    }

    
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
                    for (int j = 0; j < effect.uniforms.size(); j++) {
                        EffectUniform uniform = effect.uniforms.get(j);
                        if (ImGui.button("Remove")) {
                            effect.uniforms.remove(j);
                            continue;
                        }
                        ImGui.sameLine();
                        if (ImGui.treeNodeEx(uniform.id, ImGuiTreeNodeFlags.None, uniform.name)) {
                            uniform.name = EditorGUI.InputString("Name##" + uniform.id, uniform.name);
                            RenderUniformType(uniform);
                            RenderUniformField(uniform);

                            ImGui.treePop();
                        }
                    }

                    if (ImGui.button("Create Field")) {
                        EffectUniform uniform = new EffectUniform();
                        uniform.name = "New Uniform";
                        uniform.type = Integer.class;
                        uniform.value = 0;

                        effect.uniforms.add(uniform);
                    }
                    ImGui.sameLine();
                    if (ImGui.button("Detect Fields")) {
                        String data = FileUtility.ReadFile(new File(effect.shaderPath));

                        String[] lines = data.split("\n");
                        for (String line : lines) {
                            String[] kw = line.split(" ");
                            List<String> keywords = List.of(kw);
                            if (!keywords.contains("uniform")) continue;

                            String type = keywords.get(1);
                            String name = keywords.get(2);

                            if (name.endsWith(";")) {
                                name = name.split(";")[0];
                            }
                            if (List.of(defaultUniforms).contains(name)) {
                                continue;
                            }

                            Object originalValue = null;
                            for (int j = 0; j < effect.uniforms.size(); j++) {
                                if (effect.uniforms.get(j).name.equals(name)) {
                                    originalValue = effect.uniforms.get(j).value;
                                    effect.uniforms.remove(j);
                                }
                            }

                            EffectUniform uniform = new EffectUniform();
                            uniform.name = name;

                            if (type.equals("int")) {
                                uniform.type = Integer.class;
                            } else if (type.equals("float")) {
                                uniform.type = Float.class;
                            } else if (type.equals("bool")) {
                                uniform.type = Boolean.class;
                            } else if (type.equals("vec2")) {
                                uniform.type = Vector2.class;
                            } else if (type.equals("vec3")) {
                                uniform.type = Vector3.class;
                            }

                            if (originalValue != null && originalValue.getClass().isAssignableFrom(uniform.type)) {
                                uniform.value = originalValue;
                            } else {
                                uniform.AssignDefaultValue();
                            }
                            uniform.AssignType();

                            effect.uniforms.add(uniform);
                        }
                    }

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

    private void RenderUniformType(EffectUniform uniform) {
        UniformType type = (UniformType)EditorGUI.EnumSelect("Type##" + uniform.id, uniform.selectedType, UniformType.class);
        uniform.selectedType = type.ordinal();
        switch (type) {
            case Integer -> uniform.type = Integer.class;
            case Float -> uniform.type = Float.class;
            case Boolean -> uniform.type = Boolean.class;
            case Vector2 -> uniform.type = Vector2.class;
            case Vector3 -> uniform.type = Vector3.class;
        }

        if (!uniform.value.getClass().isAssignableFrom(uniform.type)) {
            Class t = uniform.type;
            if (t == Integer.class) uniform.value = 0;
            if (t == Float.class) uniform.value = 0f;
            if (t == String.class) uniform.value = "";
            if (t == Boolean.class) uniform.value = false;
            if (t == Vector2.class) uniform.value = Vector2.Zero();
            if (t == Vector3.class) uniform.value = Vector3.Zero();
        }
    }

    private void RenderUniformField(EffectUniform uniform) {
        if (uniform.type == Integer.class) {
            uniform.value = EditorGUI.DragInt("Value##" + uniform.id, (int)uniform.value);
        } else if (uniform.type == Float.class) {
            uniform.value = EditorGUI.DragFloat("Value##" + uniform.id, (float)uniform.value);
        } else if (uniform.type == Boolean.class) {
            uniform.value = EditorGUI.Checkbox("Value##" + uniform.id, (boolean)uniform.value);
        } else if (uniform.type == Vector2.class) {
            uniform.value = EditorGUI.DragVector2("Value##" + uniform.id, (Vector2)uniform.value);
        } else if (uniform.type == Vector3.class) {
            uniform.value = EditorGUI.DragVector3("Value##" + uniform.id, (Vector3)uniform.value);
        }
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

                        ImGui.closeCurrentPopup();
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
