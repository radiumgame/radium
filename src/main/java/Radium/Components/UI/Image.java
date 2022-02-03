package Radium.Components.UI;

import Radium.Component;
import Radium.Graphics.Texture;
import Radium.UI.UIMesh;
import Radium.UI.UIRenderer;
import RadiumEditor.EditorGUI;

public class Image extends Component {

    public UIMesh mesh;

    public Image() {
        submenu = "UI";
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        UIRenderer.Render(mesh);
    }

    @Override
    public void Stop() {

    }

    @Override
    public void OnAdd() {
        mesh = UIMesh.Quad();
    }

    @Override
    public void OnRemove() {

    }

    @Override
    public void UpdateVariable() {

    }

    @Override
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
