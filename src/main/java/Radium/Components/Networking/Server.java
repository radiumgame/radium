package Radium.Components.Networking;

import RadiumEditor.Console;
import Radium.Color;
import Radium.Component;
import Radium.PerformanceImpact;

public class Server extends Component {

    public int Port = 444;

    public Server() {
        impact = PerformanceImpact.Low;
        description = "Opens a server";
        submenu = "Networking";
    }

    @Override
    public void Start() {
        Radium.Networking.Server.Server.Start(Port);
        Console.Write("Server opened on port " + Port, Color.Green());
    }

    @Override
    public void Update() {

    }

    @Override
    public void Stop() {
        Radium.Networking.Server.Server.Close();
        Console.Write("Server closed", Color.Green());
    }

    @Override
    public void OnAdd() {
        if (Radium.Networking.Server.Server.Open) {
            Console.Error("Scene can only contain one server");

            gameObject.RemoveComponent(Server.class);
            return;
        }
    }

    @Override
    public void OnRemove() {

    }

    @Override
    public void UpdateVariable() {

    }

    @Override
    public void GUIRender() {

    }

}
