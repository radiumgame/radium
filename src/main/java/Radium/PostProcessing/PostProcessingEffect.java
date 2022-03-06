package Radium.PostProcessing;

import Radium.Graphics.Shader;
import RadiumEditor.Console;
import RadiumEditor.EditorWindow;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostProcessingEffect {

    public String id = UUID.randomUUID().toString();
    public String name = "effect";

    public List<Field> fields = new ArrayList<>();

    private boolean enabled = true;

    public PostProcessingEffect() {
        for (Field field : getClass().getFields()) {
            if (field.isAnnotationPresent(EffectField.class)) {
                fields.add(field);
            }
        }
    }

    public void SetUniforms(Shader shader) {}

    public void Enable(Shader shader) {
        shader.Bind();
        shader.SetUniform(name, true);
        shader.Unbind();
        enabled = true;
    }

    public void Disable(Shader shader) {
        shader.Bind();
        shader.SetUniform(name, false);
        shader.Unbind();
        enabled = false;
    }

    public String GetName() {
        return name;
    }

    public boolean Enabled() {
        return enabled;
    }

}
