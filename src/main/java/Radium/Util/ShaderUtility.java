package Radium.Util;

import Radium.Graphics.Shader.Shader;
import Radium.Graphics.Shader.ShaderUniform;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;

public class ShaderUtility {

    protected ShaderUtility() {}

    public static List<ShaderUniform> GetFragmentUniforms(Shader shader, String[] ignore) {
        List<ShaderUniform> returns = new ArrayList<>();
        String content = shader.fragmentFile;
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.contains("uniform")) {
                String[] parts = line.split(" ");
                String type = parts[1];
                String name = parts[2].replace(";", "");

                List<String> ignores = Arrays.asList(ignore);
                if (!ignores.contains(name)) {
                    Class typeClass = null;
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
                    }

                    if (typeClass != null) {
                        returns.add(new ShaderUniform(name, typeClass, shader));
                    }
                }
            }
        }

        return returns;
    }

}
