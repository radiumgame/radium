package Radium.Engine.Components.Graphics;

import Radium.Editor.Debug.Debug;
import Radium.Editor.Im3D.Im3D;
import Radium.Editor.Im3D.Im3DMesh;
import Radium.Editor.ProjectExplorer;
import Radium.Engine.Application;
import Radium.Engine.Color.Color;
import Radium.Engine.Component;
import Radium.Engine.Components.Rendering.Light;
import Radium.Engine.FrustumCulling.AABB;
import Radium.Engine.FrustumCulling.FrustumFilter;
import Radium.Engine.Graphics.*;

import Radium.Engine.Graphics.Lighting.LightCalculationMode;
import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.Math.Matrix4;
import Radium.Engine.Math.Random;
import Radium.Engine.Math.Vector.Vector3;
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
import org.joml.Matrix4f;
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

    @HideInEditor
    public String meshName;

    public transient AABB aabb;
    @HideInEditor
    public transient boolean inFrustum = true;

    private static int ModelTexture;

    public static int VertexCount;
    public static int TriangleCount;

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
        inFrustum = FrustumFilter.InsideFrustumAABB(aabb);

        VertexCount += mesh.GetVertices().length;
        TriangleCount += mesh.GetIndices().length / 3;
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
        }

        CalculateAABB();
    }

    
    public void OnRemove() {
        if (mesh != null) {
            mesh.Destroy();
        }
        if (material != null) {
            material.DestroyMaterial();
        }

        Light.UpdateShadows();
    }

    @Override
    public void OnTransformChanged() {
        CalculateAABB();
    }

    public void UpdateVariable(String update) {
        if (DidFieldChange(update, "meshName")) {
            SetMesh(meshName);
        }
    }

    public void CalculateAABB() {
        Vector3 min = new Vector3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        Vector3 max = new Vector3(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
        Vector3 scale = gameObject.transform.WorldScale();

        if (mesh == null) {
            aabb = new AABB(min, max);
            return;
        }

        for (Vertex vertex : mesh.GetVertices()) {
            Vector3 position = vertex.GetPosition();

            if (position.x * scale.x < min.x) {
                min.x = position.x * scale.x;
            } else if (position.x * scale.x > max.x) {
                max.x = position.x * scale.x;
            }

            if (position.y * scale.y < min.y) {
                min.y = position.y * scale.y;
            } else if (position.y * scale.y > max.y) {
                max.y = position.y * scale.y;
            }

            if (position.z * scale.z < min.z) {
                min.z = position.z * scale.z;
            } else if (position.z * scale.z > max.z) {
                max.z = position.z * scale.z;
            }
        }

        min = Vector3.Add(min, gameObject.transform.WorldPosition());
        max = Vector3.Add(max, gameObject.transform.WorldPosition());
        aabb = new AABB(min, max);
    }

    public void GUIRender() {
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
        if (ImGui.button("Choose Mesh")) {
            ImGui.openPopup("Choose Mesh");
            RenderMeshSelection();
        }

        RenderMeshSelection();
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

    public void SetMesh(String mesh) {
        if (this.mesh != null) {
            this.mesh.Destroy();
        }
        this.mesh = GetMeshFromName(mesh);
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
                if (mesh != null) {
                    mesh.Destroy();
                }
                mesh = GetMeshFromName(name);
                Light.UpdateShadows();

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
                if (this.mesh != null) this.mesh.Destroy();
                this.mesh = mesh;
                Light.UpdateShadows();

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
