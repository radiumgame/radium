package Radium.Components.UI;

import Radium.Component;
import Radium.Graphics.Texture;
import Radium.Math.Vector.Vector2;
import Radium.UI.Legacy.UIMesh;
import Radium.UI.Legacy.UIRenderer;
import RadiumEditor.Annotations.HideInEditor;
import RadiumEditor.EditorGUI;
import java.io.File;

public class Image extends Component {

    /**
     * The UI mesh of the component
     */
    public UIMesh mesh;

    @HideInEditor
    public Vector2 position = new Vector2(0, 0);
    @HideInEditor
    public Vector2 size = new Vector2(100, 100);

    /**
     * Create empty image component
     */
    public Image() {
        LoadIcon("image.png");
        submenu = "UI";
    }

    
    public void Start() {

    }

    
    public void Update() {
        UIRenderer.Render(mesh);
    }

    
    public void Stop() {

    }

    
    public void OnAdd() {
        mesh = UIMesh.Quad();
        mesh.Position = position;
        mesh.Size = size;
    }

    
    public void OnRemove() {

    }

    
    public void UpdateVariable() {

    }

    
    public void GUIRender() {
        mesh.Position = EditorGUI.DragVector2("Position", mesh.Position);
        mesh.Size = EditorGUI.DragVector2("Size", mesh.Size);
        position = mesh.Position;
        size = mesh.Size;

        File newTex = EditorGUI.FileReceive(new String[] { "png", "jpg", "bmp" }, "Texture", new File(mesh.texture.filepath));
        if (newTex != null) {
            mesh.texture = new Texture(newTex.getAbsolutePath());
        }

        mesh.color = EditorGUI.ColorField("Color", mesh.color);
    }

}
