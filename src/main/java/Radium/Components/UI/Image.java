package Radium.Components.UI;

import Radium.Component;
import Radium.Graphics.Texture;
import Radium.UI.UIMesh;
import Radium.UI.UIRenderer;
import RadiumEditor.EditorGUI;

public class Image extends Component {

    /**
     * The UI mesh of the component
     */
    public UIMesh mesh;

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
    }

    
    public void OnRemove() {

    }

    
    public void UpdateVariable() {

    }

    
    public void GUIRender() {
        mesh.Position = EditorGUI.DragVector2("Position", mesh.Position);
        mesh.Size = EditorGUI.DragVector2("Size", mesh.Size);

        Texture newTex = EditorGUI.TextureField(mesh.texture);
        if (newTex != null) {
            mesh.texture = newTex;
        }

        mesh.color = EditorGUI.ColorField("Color", mesh.color);
    }

}
