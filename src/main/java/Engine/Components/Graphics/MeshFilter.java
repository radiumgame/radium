package Engine.Components.Graphics;

import Editor.Console;
import Engine.Component;
import Engine.Graphics.Material;
import Engine.Graphics.Mesh;
import Engine.Graphics.Shader;
import Engine.Graphics.Texture;
import Engine.PerformanceImpact;
import imgui.ImGui;

import java.nio.file.Files;
import java.nio.file.Paths;

public class MeshFilter extends Component {

    public Mesh mesh;

    public Texture texture;
    public float materialShininess = 1;

    public MeshFilter() {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        mesh = null;

        description = "Stores mesh data for renderers to render";
        impact = PerformanceImpact.Low;
        submenu = "Graphics";
    }

    public MeshFilter(Mesh mesh) {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        this.mesh = mesh;

        description = "Stores mesh data for renderers to render";
        impact = PerformanceImpact.Low;

        ApplyTexture();
    }

    public void SentMaterialToShader(Shader shader) {
        shader.SetUniform("material.reflectivity", materialShininess);
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {

    }

    @Override
    public void Stop() {

    }

    @Override
    public void OnAdd() {

    }

    @Override
    public void OnRemove() {
        if (mesh != null) {
            mesh.DestroyMesh();
        }
    }

    @Override
    public void UpdateVariable() {
        ApplyTexture();
    }

    @Override
    public void GUIRender() {

    }

    private void ApplyTexture() {
        if (mesh == null) return;
        if (texture == null) {
            texture = new Texture("EngineAssets/Textures/Misc/box.jpg");
        }

        mesh.DestroyMesh();

        Material newMaterial = new Material(texture.filepath);
        mesh = new Mesh(mesh.GetVertices(), mesh.GetIndices(), newMaterial);
    }

}
