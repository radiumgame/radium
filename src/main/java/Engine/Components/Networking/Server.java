package Engine.Components.Networking;

import Editor.Console;
import Engine.Color;
import Engine.Component;

public class Server extends Component {

    public int Port = 444;

    @Override
    public void Start() {
        Engine.Networking.Server.Server.Start(Port);
        Console.Write("Server opened on port " + Port, Color.Green());
    }

    @Override
    public void Update() {

    }

    @Override
    public void Stop() {
        Engine.Networking.Server.Server.Close();
        Console.Write("Server closed", Color.Green());
    }

    @Override
    public void OnAdd() {
        if (Engine.Networking.Server.Server.Open) {
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
