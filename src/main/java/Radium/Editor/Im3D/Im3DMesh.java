package Radium.Editor.Im3D;

import Radium.Engine.Graphics.Framebuffer.Framebuffer;
import Radium.Engine.Graphics.Mesh;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Window;

public class Im3DMesh {

    public Mesh mesh;
    public Framebuffer framebuffer;
    public boolean render = false;

    public Im3DMesh(Mesh mesh) {
        this.mesh = mesh;
        framebuffer = new Framebuffer(Window.width, Window.height);
        Window.ResizeFramebuffer.add(framebuffer);
    }

}
