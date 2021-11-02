package Engine.Components.Networking;

import Engine.Component;

public class Client extends Component {

    public String IP = "127.0.0.1";
    public int Port = 444;

    private transient Engine.Networking.Client.Client client;

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
        client = new Engine.Networking.Client.Client();
    }

    @Override
    public void OnRemove() {

    }

    @Override
    public void OnVariableUpdate() {

    }

    @Override
    public void GUIRender() {

    }

}
