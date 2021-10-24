package Engine.Networking.Server;

import Editor.Console;
import Engine.Networking.Packet;

public class ServerHandle {

    public ServerClient client;

    public ServerHandle(ServerClient client) {
        this.client = client;
    }

}
