package Radium.Editor.Im3D;

import Radium.Engine.Graphics.Mesh;
import Radium.Engine.Input.Input;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Editor.EditorWindows.ModelViewSettings;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Random;

public class Im3D {

    private static Im3DRenderer renderer;
    public static HashMap<Integer, Im3DMesh> meshes = new HashMap<>();

    protected Im3D() {}

    public static void Initialize() {
        renderer = new Im3DRenderer();
    }

    public static int AddMesh(Mesh mesh) {
        int id = new Random().nextInt();
        while (meshes.containsKey(id)) {
            id = new Random().nextInt();
        }

        meshes.put(id, new Im3DMesh(mesh));
        return id;
    }

    public static void RemoveMesh(int id) {
        meshes.remove(id);
    }

    public static void Update() {
        for (Im3DMesh mesh : meshes.values()) {
            if (!mesh.render) continue;

            GL11.glClearColor(0.156862745f, 0.156862745f, 0.341176471f, 1);
            mesh.framebuffer.Bind();
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
            GL11.glClearColor(0.156862745f, 0.156862745f, 0.341176471f, 1);
            GL11.glLoadIdentity();
            renderer.Render(mesh.mesh, ModelViewSettings.base);
            mesh.framebuffer.Unbind();
        }
    }

    public static void Viewer(int mesh, Vector2 size) {
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        //ImGui.begin("3D Viewer", ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoScrollbar);

        ImGui.beginChild("3D_VIEWER_CHILD##" + mesh, size.x, size.y, false, ImGuiWindowFlags.NoMove);
        CheckInputs();

        ImGui.image(meshes.get(mesh).framebuffer.GetTextureID(), size.x, size.y);

        ImGui.endChild();
        //ImGui.end();
        ImGui.popStyleVar(2);
    }

    public static void SetRenderMesh(int mesh, boolean render) {
        meshes.get(mesh).render = render;
    }

    private static ImVec2 lastDelta = new ImVec2(0, 0);
    private static final float scaleClamp = 0.2f;
    private static void CheckInputs() {
        if (ImGui.isMouseDragging(0)) {
            ImVec2 md = ImGui.getMouseDragDelta(0);
            float xDif = md.x - lastDelta.x;
            float yDif = md.y - lastDelta.y;
            lastDelta = md;

            xDif *= 0.25f;
            yDif *= 0.25f;

            renderer.objectTransform.rotation.x += -yDif;
            renderer.objectTransform.rotation.y += xDif;
        } else {
            lastDelta = new ImVec2(0, 0);
        }

        float scroll = (float)Input.GetScrollY();
        if (scroll != 0) {
            Vector3 scale = renderer.objectTransform.scale;
            if (scale.x < scaleClamp) scale.x = scaleClamp;
            if (scale.y < scaleClamp) scale.y = scaleClamp;
            if (scale.z < scaleClamp) scale.z = scaleClamp;

            renderer.objectTransform.scale.x += scroll * 0.25f;
            renderer.objectTransform.scale.y += scroll * 0.25f;
            renderer.objectTransform.scale.z += scroll * 0.25f;
        }
        Input.ResetScroll();
    }

}
