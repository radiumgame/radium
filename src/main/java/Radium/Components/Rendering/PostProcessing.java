package Radium.Components.Rendering;

import Radium.Color;
import Radium.Component;
import Radium.PerformanceImpact;
import Radium.PostProcessing.Effects.Invert;
import Radium.PostProcessing.Effects.Tint;
import Radium.PostProcessing.PostProcessingEffect;
import RadiumEditor.Annotations.RangeFloat;
import RadiumEditor.Annotations.RangeInt;
import RadiumEditor.Console;
import RadiumEditor.EditorGUI;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import org.apache.commons.text.WordUtils;

import java.lang.reflect.Field;

public class PostProcessing extends Component {

    public PostProcessing() {
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

            ImGui.treePop();
        }

        if (ImGui.button("Add Effect", ImGui.getWindowWidth() - 30, 25)) {
            ImGui.openPopup("AddEffect");
        }

        AddPopup();
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

            ImGui.endPopup();
        }
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
