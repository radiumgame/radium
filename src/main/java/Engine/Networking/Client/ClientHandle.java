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
        Console.Log("received id");

        client.clientID = packet.ReadInt();
        client.send.SendReceivedID();
    }

}
