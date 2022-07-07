package Radium.Engine.Components.Graphics;

import Radium.Editor.Im3D.Im3D;
import Radium.Editor.Im3D.Im3DMesh;
import Radium.Editor.ProjectExplorer;
import Radium.Engine.Application;
import Radium.Engine.Color.Color;
import Radium.Engine.Component;
import Radium.Engine.Graphics.*;

import Radium.Engine.Graphics.Lighting.LightCalculationMode;
import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.ModelLoader;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.PerformanceImpact;
import Radium.Engine.System.FileExplorer;
import Radium.Engine.System.Popup;
import Radium.Engine.Util.ThreadUtility;
import Radium.Editor.Annotations.HideInEditor;
import Radium.Editor.Annotations.RunInEditMode;
import Radium.Editor.Console;
import Radium.Editor.EditorGUI;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import org.lwjgl.util.par.ParShapes;

import java.io.File;

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

    @HideInEditor
    public transient boolean selected;

    private static int ModelTexture;

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
        name = "Mesh Filter";

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
        name = "Mesh Filter";

        description = "Stores mesh data for renderers to render";
        impact = PerformanceImpact.Low;
    }

    /**
     * Sends the material variables to the bound shader
     * @param shader Shader to send the variables to
     */
    public void SendMaterialToShader(Shader shader) {
        if (material == null) return;
        if (material.lightCalculationMode == null) {
            material.lightCalculationMode = LightCalculationMode.Normal;
        }

        shader.SetUniform("specularLighting", material.specularLighting);
        shader.SetUniform("useNormalMap", material.useNormalMap);
        shader.SetUniform("useSpecularMap", material.useSpecularMap);
        shader.SetUniform("lightCalcMode", material.lightCalculationMode.ordinal());

        shader.SetUniform("material.reflectivity", material.reflectivity);
        shader.SetUniform("material.shineDamper", material.shineDamper);

        shader.SetUniform("material.metallic", material.metallic);
        shader.SetUniform("material.alpha", material.glossiness);
        shader.SetUniform("material.baseReflectivity", material.fresnel);
    }

    private boolean selectedAtRuntime = false;

    
    public void Start() {
        selectedAtRuntime = selected;
        UnSelect();
    }

    
    public void Update() {

    }

    
    public void Stop() {
        if (selectedAtRuntime) {
            Select();
            selectedAtRuntime = false;
        }
    }

    
    public void OnAdd() {
        if (mesh != null) {
            mesh.Destroy();
            mesh.CreateMesh();
        } else {
            mesh = Mesh.Empty();
        }

        if (material != null) {
            material.CreateMaterial();
            material.CreateMaterial();
        }
    }

    
    public void OnRemove() {
        if (mesh != null) {
            mesh.Destroy();
        }
        if (material != null) {
            material.DestroyMaterial();
        }
    }

    
    public void UpdateVariable(String update) {
        if (update.equals("meshType")) {
            switch (meshType) {
                case Cube -> {
                    GameObject cube = ModelLoader.LoadModel("EngineAssets/Models/Cube.fbx", false).GetChildren().get(0).GetChildren().get(0);
                    mesh = cube.GetComponent(MeshFilter.class).mesh;
                }
                case Sphere -> {
                    GameObject sphere = ModelLoader.LoadModel("EngineAssets/Models/Sphere.fbx", false).GetChildren().get(0).GetChildren().get(0);
                    mesh = sphere.GetComponent(MeshFilter.class).mesh;
                }
                case Plane -> {
                    mesh = Mesh.Plane(1, 1);
                }
                case None -> {
                    mesh = null;
                }
            }
        }
    }

    @HideInEditor
    public MeshType meshType = MeshType.None;
    public void GUIRender() {
        if (ImGui.button("Choose Mesh")) {
            ImGui.openPopup("Choose Mesh");
            RenderMeshSelection();
        }

        if (ImGui.collapsingHeader("Mesh Data", ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.indent();

            if (mesh != null) {
                ImGui.text("Vertex Count: " + mesh.GetVertices().length);
                ImGui.text("Triangle Count: " + mesh.GetIndices().length / 3);
            } else {
                ImGui.text("Must have mesh to view data");
            }

            ImGui.unindent();
        }

        RenderMeshSelection();
    }

    public void SetMeshType(MeshType meshtype) {
        this.meshType = meshtype;
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

    private void RenderMeshSelection() {
        if (ModelTexture == 0) ModelTexture = new Texture("EngineAssets/Editor/Explorer/model.png").textureID;

        ImGui.setNextWindowSize(400, 300);
        if (ImGui.beginPopup("Choose Mesh")) {
            RenderPrimitive("Cube");
            RenderPrimitive("Sphere");
            RenderPrimitive("Cone");
            RenderPrimitive("Torus");
            for (File f : ProjectExplorer.Im3DMeshes.keySet()) {
                Im3DMesh mesh = Im3D.meshes.get(ProjectExplorer.Im3DMeshes.get(f));
                RenderMesh(mesh.mesh, f.getName());
            }

            ImGui.endPopup();
        }

        availSpace = 400;
    }

    private String selectedID = "";
    private static final Color SelectedColor = new Color(80 / 255f, 120 / 255f, 237 / 255f);
    private float availSpace = 400;
    private void RenderPrimitive(String name) {
        float size = 90;
        CheckAvailSpace(size);

        boolean selected = selectedID.equals(name);
        if (selected) {
            ImGui.pushStyleColor(ImGuiCol.FrameBg, SelectedColor.r, SelectedColor.g, SelectedColor.b, SelectedColor.a);
        }

        ImGui.beginChildFrame(name.hashCode(), size, size, ImGuiWindowFlags.NoScrollbar);
        ImGui.image(ModelTexture, size * 0.7f, size * 0.7f);
        ImGui.text(name);
        ImGui.endChildFrame();

        if (selected) {
            ImGui.popStyleColor();
        }

        if (ImGui.isItemHovered()) {
            if (ImGui.isMouseClicked(0)) {
                selectedID = name;
            }
            if (ImGui.isMouseDoubleClicked(0)) {
                mesh = GetMeshFromName(name);
                selectedID = "";
                ImGui.closeCurrentPopup();
            }
        }
    }

    private void RenderMesh(Mesh mesh, String name) {
        float size = 90;
        CheckAvailSpace(size);

        boolean selected = selectedID.equals(name);
        if (selected) {
            ImGui.pushStyleColor(ImGuiCol.FrameBg, SelectedColor.r, SelectedColor.g, SelectedColor.b, SelectedColor.a);
        }

        ImGui.beginChildFrame(name.hashCode(), size, size, ImGuiWindowFlags.NoScrollbar);
        ImGui.image(ModelTexture, size * 0.7f, size * 0.7f);
        ImGui.text(name);
        ImGui.endChildFrame();

        if (selected) {
            ImGui.popStyleColor();
        }

        if (ImGui.isItemHovered()) {
            if (ImGui.isMouseClicked(0)) {
                selectedID = name;
            }
            if (ImGui.isMouseDoubleClicked(0)) {
                this.mesh = mesh;
                selectedID = "";
                ImGui.closeCurrentPopup();
            }
        }
    }

    private void CheckAvailSpace(float size) {
        availSpace -= size;
        if (availSpace < size) {
            availSpace = 400;
            ImGui.newLine();
        } else {
            ImGui.sameLine();
        }
    }

    private Mesh GetMeshFromName(String name) {
        switch (name) {
            case "Cube" -> {
                return Mesh.Cube();
            }
            case "Sphere" -> {
                return ModelLoader.LoadModelNoMultiThread("EngineAssets/Models/Sphere.fbx", false).GetChildren().get(0).GetChildren().get(0).GetComponent(MeshFilter.class).mesh;
            }
            case "Cone" -> {
                return Mesh.GetMesh(ParShapes.par_shapes_create_cone(30, 30));
            }
            case "Torus" -> {
                return Mesh.GetMesh(ParShapes.par_shapes_create_torus(30, 30, 0.4f));
            }
            default -> {
                return Mesh.Empty();
            }
        }
    }

}
