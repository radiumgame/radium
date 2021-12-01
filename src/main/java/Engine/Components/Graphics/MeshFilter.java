package Engine.Components.Graphics;

import Editor.Console;
import Editor.MousePicking;
import Engine.Component;
import Engine.Graphics.Material;
import Engine.Graphics.Mesh;
import Engine.Graphics.Shader;
import Engine.Graphics.Texture;
import Engine.PerformanceImpact;
import imgui.ImGui;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MeshFilter extends Component {

    public Mesh mesh;
    public Material material;

    private boolean mousePicked = false;

    public MeshFilter() {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        mesh = null;

        RunInEditMode = true;
        description = "Stores mesh data for renderers to render";
        impact = PerformanceImpact.Low;
        submenu = "Graphics";
    }

    public MeshFilter(Mesh mesh) {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        this.mesh = mesh;

        RunInEditMode = true;
        description = "Stores mesh data for renderers to render";
        impact = PerformanceImpact.Low;
    }

    public void SentMaterialToShader(Shader shader) {
        if (material == null) return;

        shader.SetUniform("material.reflectivity", material.reflectivity);
        shader.SetUniform("material.shineDamper", material.shineDamper);
        shader.SetUniform("material.reflective", material.cubeMapReflections);
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        if (mesh != null && !mousePicked) {
            MousePicking.AddObject(gameObject, mesh);
            mousePicked = true;
        }
    }

    @Override
    public void Stop() {

    }

    @Override
    public void OnAdd() {
        UpdateMaterial();
    }

    @Override
    public void OnRemove() {
        if (mesh != null) {
            mesh.DestroyMesh();
        }
    }

    @Override
    public void UpdateVariable() {
        if (material != null) {
            UpdateMaterial();
        }
    }

    @Override
    public void GUIRender() {
        if (ImGui.button("Update Material")) {
            UpdateMaterial();
        }
    }

    public void UpdateMaterial() {
        if (material != null && mesh != null) {
            mesh.material = material;
            mesh.CreateMesh();
        } else {
            material = Material.Default();
        }
    }

}
