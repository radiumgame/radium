package Radium.Networking.Client;

import Radium.Networking.Packet;
import Radium.Networking.PacketType.ClientPackets;

public class ClientSend {

    private Client client;

    public ClientSend(Client client) {
        this.client = client;
    }

    public void Disconnect() {
        Packet disconnectPacket = new Packet(ClientPackets.Disconnect.ordinal());
        client.SendData(disconnectPacket);
    }

}
