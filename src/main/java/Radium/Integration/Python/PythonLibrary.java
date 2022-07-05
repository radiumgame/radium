package Radium.Integration.Python;

import Radium.Engine.Util.FileUtility;

import java.io.File;

public class PythonLibrary {

    public File src;
    public String content;

    public PythonLibrary(File src) {
        this.src = src;
        this.content = FileUtility.ReadFile(this.src);
    }

}
