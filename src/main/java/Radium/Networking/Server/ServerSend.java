package Radium.Networking.Server;

import Radium.Networking.Packet;
import Radium.Networking.PacketType.ServerPackets;

public class ServerSend {

    public ServerClient client;

    public ServerSend(ServerClient client) {
        this.client = client;
    }

    public void SendID() {
        Packet packet = new Packet(ServerPackets.ID.ordinal());
        packet.Write(client.id);
        client.SendData(packet);
    }

    public void Disconnect() {
        Packet packet = new Packet(ServerPackets.ForceDisconnect.ordinal());
        client.SendData(packet);
    }

}
