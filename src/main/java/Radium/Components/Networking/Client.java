package Radium.Components.Networking;

import Radium.Component;
import Radium.PerformanceImpact;

/**
 * A client that can connect to a dedicated server
 */
public class Client extends Component {

    /**
     * IP to connect to
     */
    public String IP = "127.0.0.1";
    /**
     * Port to connect to
     */
    public int Port = 444;

    private transient Radium.Networking.Client.Client client;

    /**
     * Create empty client component
     */
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
