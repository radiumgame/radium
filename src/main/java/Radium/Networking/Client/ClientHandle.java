package Radium.Networking.Client;

import Radium.Networking.Packet;

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
