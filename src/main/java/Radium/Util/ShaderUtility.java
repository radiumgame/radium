package Radium.Util;

import Radium.Color;
import Radium.Graphics.Shader.Shader;
import Radium.Graphics.Shader.ShaderUniform;
import Radium.Graphics.Shader.Type.ShaderLight;
import Radium.Graphics.Shader.Type.ShaderMaterial;
import Radium.Graphics.Texture;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import RadiumEditor.Console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;

public class ShaderUtility {

    protected ShaderUtility() {}

    public static List<ShaderUniform> GetFragmentUniforms(Shader shader, String[] ignore) {
        List<ShaderUniform> returns = new ArrayList<>();
        List<ShaderUniform> currentUniforms = shader.GetUniforms();
        String content = shader.fragmentFile;
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.contains("uniform")) {
                String[] parts = line.split(" ");
                String type = parts[1];
                String name = parts[2].replace(";", "");

                boolean exists = false;
                ShaderUniform e = null;
                for (ShaderUniform uniform : currentUniforms) {
                    if (uniform.name.equals(name)) {
                        e = uniform;
                        exists = true;
                        break;
                    }
                }

                List<String> ignores = Arrays.asList(ignore);
                if (!ignores.contains(name)) {
                    Class typeClass = FromType(type);

                    if (typeClass != null && !exists) {
                        returns.add(new ShaderUniform(name, typeClass, shader));
                    } else if (typeClass != null && exists) {
                        if (e.type != typeClass) {
                            returns.add(new ShaderUniform(name, typeClass, shader));
                        } else {
                            returns.add(e);
                        }
                    }
                }
            }
        }

        return returns;
    }

    public static Class FromType(String type) {
        Class typeClass;
        if (type.equals("int")) {
            typeClass = Integer.class;
        } else if (type.equals("float")) {
            typeClass = Float.class;
        } else if (type.equals("vec2")) {
            typeClass = Vector2.class;
        } else if (type.equals("vec3")) {
            typeClass = Vector3.class;
        } else if (type.equals("bool")) {
            typeClass = Boolean.class;
        } else if (type.equals("sampler2D")) {
            typeClass = Texture.class;
        } else if (type.equals("Material")) {
            typeClass = ShaderMaterial.class;
        } else if (type.equals("Light")) {
            typeClass = ShaderLight.class;
        } else if (type.equals("Color")) {
            typeClass = Color.class;
        } else {
            typeClass = null;
        }

        return typeClass;
    }

}
