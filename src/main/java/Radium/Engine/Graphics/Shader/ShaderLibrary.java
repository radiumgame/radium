package Radium.Engine.Graphics.Shader;

import Radium.Engine.Util.FileUtility;

import java.io.File;

public class ShaderLibrary {

    public String name;
    public File source;
    public String content;

    public ShaderLibrary(String file) {
        this.source = new File(file);
        this.name = this.source.getName();
        this.content = FileUtility.ReadFile(this.source);
    }

}
