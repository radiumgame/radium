package Radium.Components.Networking;

import Radium.Component;
import Radium.PerformanceImpact;

public class Client extends Component {

    public String IP = "127.0.0.1";
    public int Port = 444;

    private transient Radium.Networking.Client.Client client;

    public Client() {
        impact = PerformanceImpact.Low;
        description = "Connects a client to a dedicated server";
        submenu = "Networking";
    }

    @Override
    public void Start() {
        client.Connect(IP, Port);
    }

    @Override
    public void Update() {

    }

    @Override
    public void Stop() {
        client.Disconnect();
    }

    @Override
    public void OnAdd() {
        client = new Radium.Networking.Client.Client();
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
