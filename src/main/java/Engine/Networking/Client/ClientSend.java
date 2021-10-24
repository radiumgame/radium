package Engine.Networking.Client;

import Engine.Networking.Packet;
import Engine.Networking.PacketType.ClientPackets;
import Engine.Util.NonInstantiatable;

public class ClientSend {

    private Client client;

    public ClientSend(Client client) {
        this.client = client;
    }

    public void SendReceivedID() {
        Packet packet = new Packet(ClientPackets.IDReceived.ordinal());
        client.SendData(packet);
    }

}
