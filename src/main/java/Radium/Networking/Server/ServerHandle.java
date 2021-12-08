package Radium.Networking.Server;

import Radium.Networking.Packet;

public class ServerHandle {

    public ServerClient client;

    public ServerHandle(ServerClient client) {
        this.client = client;
    }

    public void ClientDisconnect(Packet packet) {
        client.Disconnect();
    }

}
