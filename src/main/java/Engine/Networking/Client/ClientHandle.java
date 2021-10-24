package Engine.Networking.Client;

import Editor.Console;
import Engine.Networking.Packet;
import Engine.Util.NonInstantiatable;

public class ClientHandle {

    public Client client;

    public ClientHandle(Client client) {
        this.client = client;
    }

    public void ClaimID(Packet packet) {
        client.clientID = packet.ReadInt();
    }

    public void Disconnect() {
        client.Disconnect();
    }

}
