package Engine.Networking.Client;

import Engine.Networking.Packet;
import Engine.Networking.PacketType.ClientPackets;
import Engine.Util.NonInstantiatable;

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
