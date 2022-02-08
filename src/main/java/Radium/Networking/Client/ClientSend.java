package Radium.Networking.Client;

import Radium.Networking.Packet;
import Radium.Networking.PacketType.ClientPackets;

/**
 * Send data to server
 */
public class ClientSend {

    private Client client;

    /**
     * Create client send for client
     * @param client Sender
     */
    public ClientSend(Client client) {
        this.client = client;
    }

    /**
     * Send disconnect packet
     */
    public void Disconnect() {
        Packet disconnectPacket = new Packet(ClientPackets.Disconnect.ordinal());
        client.SendData(disconnectPacket);
    }

}
