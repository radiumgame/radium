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

    
    public void Start() {
        Radium.Networking.Server.Server.Start(Port);
        Console.Write("Server opened on port " + Port, Color.Green());
    }

    
    public void Update() {

    }

    
    public void Stop() {
        Radium.Networking.Server.Server.Close();
        Console.Write("Server closed", Color.Green());
    }

    
    public void OnAdd() {
        if (Radium.Networking.Server.Server.Open) {
            Console.Error("Scene can only contain one server");

            gameObject.RemoveComponent(Server.class);
            return;
        }
    }

    
    public void OnRemove() {

    }

    
    public void UpdateVariable(String update) {

    }

    
    public void GUIRender() {

    }

}
