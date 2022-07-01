package RadiumEditor.Im3D;

import Radium.Graphics.Framebuffer.Framebuffer;
import Radium.Graphics.Mesh;
import Radium.Math.Vector.Vector2;

public class Im3DMesh {

    public Mesh mesh;
    public Framebuffer framebuffer;
    public boolean render = false;

    public static Vector2 Resolution = new Vector2(1920, 1080);

    public Im3DMesh(Mesh mesh) {
        this.mesh = mesh;
        framebuffer = new Framebuffer((int)Resolution.x, (int)Resolution.y);
    }

}
