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

    
    public void Start() {
        client.Connect(IP, Port);
    }

    
    public void Update() {

    }

    
    public void Stop() {
        client.Disconnect();
    }

    
    public void OnAdd() {
        client = new Radium.Networking.Client.Client();
    }

    
    public void OnRemove() {

    }

    
    public void UpdateVariable() {

    }

    
    public void GUIRender() {

    }

}
