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

    public String textureFilepath = "EngineAssets/Textures/box.jpg";
    public float materialShininess = 1;

    public MeshFilter() {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        mesh = null;

        RunInEditMode = true;
        description = "Stores mesh data for renderers to render";
        impact = PerformanceImpact.Low;
    }

    public MeshFilter(Mesh mesh) {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        this.mesh = mesh;

        RunInEditMode = true;
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

    }

    @Override
    public void UpdateVariable() {

    }

    @Override
    public void GUIRender() {
        if (ImGui.button("Apply Texture")) {
            ApplyTexture();
        }
    }

    private void ApplyTexture() {
        if (Files.exists(Paths.get(textureFilepath))) {
            mesh.DestroyMesh();

            Material newMaterial = new Material(textureFilepath);
            mesh = new Mesh(mesh.GetVertices(), mesh.GetIndices(), newMaterial);
        } else {
            Console.Error("Filepath \"" + textureFilepath + "\" does not exist.");
        }
    }

}
