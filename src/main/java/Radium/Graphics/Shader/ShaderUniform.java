package Radium.Graphics.Shader;

import Radium.Graphics.Texture;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
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

    public void Set() {
        if (value.getClass() == LinkedTreeMap.class) {
            value = new GsonBuilder().create().fromJson(value.toString(), type);
        } else if (value.getClass() == Double.class) {
            value = ((Double) value).floatValue();
        }

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
        }
    }

}
