package Radium.Components.Graphics;

import Radium.Component;
import Radium.Graphics.Material;
import Radium.Graphics.Mesh;
import Radium.Graphics.Shader;
import Radium.Graphics.Texture;
import Radium.PerformanceImpact;
import imgui.ImGui;

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
