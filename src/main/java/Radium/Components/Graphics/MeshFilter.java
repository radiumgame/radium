package Radium.Components.Graphics;

import Radium.Application;
import Radium.Component;
import Radium.Graphics.*;
import Radium.Graphics.Renderers.Renderers;
import Radium.Math.Vector.Vector3;
import Radium.PerformanceImpact;
import RadiumEditor.Annotations.RunInEditMode;
import RadiumEditor.Console;
import RadiumEditor.EditorGUI;
import RadiumEditor.MousePicking.MeshCollider;

/**
 * Component to load and contain a mesh and material
 */
@RunInEditMode
public class MeshFilter extends Component {

    /**
     * Mesh for the MeshRenderer to load
     */
    public Mesh mesh;
    /**
     * Rendering settings for the renderer to use
     */
    public Material material;

    private boolean selected;
    private Vector3 selectedColor = new Vector3(1f, 0.78f, 0.3f);
    private float selectedWidth = 0.3f;

    private transient MeshCollider meshCollider;

    /**
     * Create an empty mesh filter component with no mesh
     */
    public MeshFilter() {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        mesh = null;
        this.material = new Material("EngineAssets/Textures/Misc/blank.jpg");

        name = "Mesh Filter";
        description = "Stores mesh data for renderers to render";
        impact = PerformanceImpact.Low;
        submenu = "Graphics";
    }

    /**
     * Create a mesh filter component with a predefined mesh
     * @param mesh New mesh
     */
    public MeshFilter(Mesh mesh) {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        this.mesh = mesh;
        this.material = new Material("EngineAssets/Textures/Misc/blank.jpg");

        description = "Stores mesh data for renderers to render";
        impact = PerformanceImpact.Low;
    }

    /**
     * Create a mesh filter component with a predefined mesh and material
     * @param mesh New mesh
     * @param material New material
     */
    public MeshFilter(Mesh mesh, Material material) {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        this.mesh = mesh;
        this.material = material;

        description = "Stores mesh data for renderers to render";
        impact = PerformanceImpact.Low;
    }

    /**
     * Sends the material variables to the bound shader
     * @param shader Shader to send the variables to
     */
    public void SendMaterialToShader(Shader shader) {
        if (material == null) return;

        shader.SetUniform("specularLighting", material.specularLighting);
        shader.SetUniform("useNormalMap", material.useNormalMap);
        shader.SetUniform("useSpecularMap", material.useSpecularMap);
        shader.SetUniform("material.reflectivity", material.reflectivity);
        shader.SetUniform("material.shineDamper", material.shineDamper);

        shader.SetUniform("outlineColor", selectedColor);
        shader.SetUniform("outlineWidth", selectedWidth);
        shader.SetUniform("outline", selected);
    }

    private boolean selectedAtRuntime = false;

    @Override
    public void Start() {
        selectedAtRuntime = selected;
        UnSelect();
    }

    @Override
    public void Update() {

    }

    @Override
    public void Stop() {
        if (selectedAtRuntime) {
            Select();
            selectedAtRuntime = false;
        }
    }

    @Override
    public void OnAdd() {
        if (mesh != null) {
            mesh.Destroy();
            mesh.CreateMesh();

            meshCollider = new MeshCollider(gameObject, mesh);
        }

        if (material != null) {
            material.CreateMaterial();
            material.CreateMaterial();
        }
    }

    @Override
    public void OnRemove() {
        if (mesh != null) {
            mesh.Destroy();
        }
        if (material != null) {
            material.DestroyMaterial();
        }
    }

    @Override
    public void UpdateVariable() {

    }

    @Override
    public void GUIRender() {

    }

    /**
     * Enables outline in editor game object is selected
     */
    public void Select() {
        if (Application.Playing) return;

        selected = true;
    }

    /**
     * Disables outline in editor
     */
    public void UnSelect() {
        if (Application.Playing) return;

        selected = false;
    }

}
