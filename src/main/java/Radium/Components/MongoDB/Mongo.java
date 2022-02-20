package Radium.Components.MongoDB;

import Integration.MongoDB.MongoDB;
import Radium.Component;
import imgui.ImGui;

public class Mongo extends Component {

    public String connectionURL = "";

    private transient MongoDB mongo;

    @Override
    public void Start() {
        mongo.Connect(connectionURL);
    }

    @Override
    public void Update() {

    }

    @Override
    public void Stop() {

    }

    @Override
    public void OnAdd() {
        mongo = new MongoDB();
    }

    @Override
    public void OnRemove() {
        mongo.Disconnect();
    }

    @Override
    public void UpdateVariable() {

    }

    @Override
    public void GUIRender() {
        ImGui.setCursorPosX(ImGui.getWindowWidth() / 2 - 160);
        if (ImGui.button("Connect", 320, 30)) {
            mongo.Connect(connectionURL);
        }
    }

}
