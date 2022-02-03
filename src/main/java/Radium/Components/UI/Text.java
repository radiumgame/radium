package Radium.Components.UI;

import Radium.Color;
import Radium.Component;
import Radium.Math.Mathf;
import Radium.Math.Vector.Vector2;
import Radium.UI.Text.CFont;
import Radium.UI.UIMesh;
import Radium.UI.UIRenderer;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class Text extends Component {

    public Vector2 Position = Vector2.Zero();
    public String text = "Lets go";
    public Color color = new Color(1f, 1f, 1f, 1f);

    private transient List<UIMesh> characters = new ArrayList<>();
    private transient CFont font;

    public Text() { }

    public Text(String text) {
        this.text = text;
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        for (UIMesh mesh : characters) {
            UIRenderer.Render(mesh);
        }
    }

    @Override
    public void Stop() {

    }

    @Override
    public void OnAdd() {
        font = new CFont("C:/Windows/Fonts/Arial.ttf", 128);
        CreateMeshes();
    }

    @Override
    public void OnRemove() {

    }

    @Override
    public void UpdateVariable() {
        float addWidth = 0;
        for (UIMesh character : characters) {
            character.color = color;

            character.Position = Vector2.Add(Position, new Vector2(addWidth, 0));
            addWidth += character.Size.x;

            if (character.Size.x <= 36) {
                character.Position.x -= 16;
            }
        }
    }

    @Override
    public void GUIRender() {
        if (ImGui.button("Update Text")) {
            CreateMeshes();
        }
    }

    private void CreateMeshes() {
        characters.clear();

        float addWidth = 0;
        for (char character : text.toCharArray()) {
            UIMesh charMesh = UIMesh.Character(font, font.GetCharacter(character));
            charMesh.Position = Vector2.Add(Position, new Vector2(addWidth, 0));

            if (charMesh.Size.x <= 36) {
                charMesh.Position.x -= 16;
            }

            characters.add(charMesh);
            addWidth += charMesh.Size.x;
        }
    }

}
