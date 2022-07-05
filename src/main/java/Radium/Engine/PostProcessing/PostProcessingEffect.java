package Radium.Engine.PostProcessing;

import Radium.Engine.Graphics.Shader.Shader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostProcessingEffect {

    public transient String id = UUID.randomUUID().toString();
    public String name = "effect";

    public transient List<Field> fields = new ArrayList<>();
    public Class effectType = null;

    protected boolean enabled = true;

    public PostProcessingEffect() {
        if (effectType == null) effectType = getClass();
        SetFields();
    }

    public void SetFields() {
        for (Field field : effectType.getFields()) {
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
