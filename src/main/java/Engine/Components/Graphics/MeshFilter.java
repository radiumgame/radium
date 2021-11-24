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

    public Material material = Material.FromSource("EngineAssets/Materials/Default.radiummat");

    public MeshFilter() {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        mesh = null;

        RunInEditMode = true;
        description = "Stores mesh data for renderers to render";
        impact = PerformanceImpact.Low;
        submenu = "Graphics";

        UpdateMaterial();
    }

    public MeshFilter(Mesh mesh) {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        this.mesh = mesh;

        RunInEditMode = true;
        description = "Stores mesh data for renderers to render";
        impact = PerformanceImpact.Low;

        UpdateMaterial();
    }

    public void SentMaterialToShader(Shader shader) {
        shader.SetUniform("material.reflectivity", material.reflectivity);
        shader.SetUniform("material.shineDamper", material.shineDamper);
        shader.SetUniform("material.reflective", material.cubeMapReflections);
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
        if (mesh != null && material != null) {
            String path = (!Files.exists(Paths.get(material.materialFile.getPath()))) ? "EngineAssets/Materials/Default.radiummat" : material.materialFile.getPath();
            material = Material.FromSource(path);

            mesh.material = material;
            mesh.CreateMesh();
        }
    }

}
