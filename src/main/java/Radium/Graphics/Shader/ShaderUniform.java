package Radium.Graphics.Shader;

import Radium.Graphics.Shader.Type.ShaderLight;
import Radium.Graphics.Shader.Type.ShaderMaterial;
import Radium.Graphics.Texture;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import RadiumEditor.Console;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

public class ShaderUniform {

    public String name;
    public Class type;
    public Object value;

    public int temp;

    public transient Shader shader;

    public ShaderUniform(String name, Class type, Shader shader) {
        this.name = name;
        this.type = type;
        this.shader = shader;

        if (type == Integer.class) {
            value = 0;
        } else if (type == Float.class) {
            value = 0.0f;
        } else if (type == Boolean.class) {
            value = false;
        } else if (type == Vector2.class) {
            value = Vector2.Zero();
        } else if (type == Vector3.class) {
            value = Vector3.Zero();
        }
    }

    public void UpdateType() {
        if (value == null) {
            if (type == ShaderMaterial.class) value = new ShaderMaterial();
            if (type == ShaderLight.class) value = new ShaderLight();
        }

        if (value.getClass() == LinkedTreeMap.class) {
            value = new GsonBuilder().create().fromJson(value.toString(), type);
        } else if (value.getClass() == Double.class) {
            value = ((Double) value).floatValue();
        } else if (value.getClass() == String.class) {
            value = new Texture((String)value);
        }
    }

    public void Set() {
        UpdateType();

        if (type == Integer.class) {
            shader.SetUniform(name, (int)value);
        } else if (type == Float.class) {
            shader.SetUniform(name, (float)value);
        } else if (type == Boolean.class) {
            shader.SetUniform(name, (boolean)value);
        } else if (type == Vector2.class) {
            shader.SetUniform(name, (Vector2)value);
        } else if (type == Vector3.class) {
            shader.SetUniform(name, (Vector3)value);
        } else if (type == ShaderMaterial.class) {
            shader.SetUniform(name + ".shineDamper", ((ShaderMaterial)value).shineDamper);
            shader.SetUniform(name + ".reflectivity", ((ShaderMaterial)value).reflectivity);
        } else if (type == ShaderLight.class) {
            shader.SetUniform(name + ".position", ((ShaderLight)value).position);
            shader.SetUniform(name + ".color", ((ShaderLight)value).color.ToVector3());
            shader.SetUniform(name + ".intensity", ((ShaderLight)value).intensity);
            shader.SetUniform(name + ".attenuation", ((ShaderLight)value).attenuation);
            shader.SetUniform(name + ".lightType", ((ShaderLight)value).lightType.ordinal());
        }
    }

}
