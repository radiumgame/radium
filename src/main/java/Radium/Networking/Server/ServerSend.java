package Radium.Networking.Server;

import Radium.Networking.Packet;
import Radium.Networking.PacketType.ServerPackets;

/**
 * Sending data to client
 */
public class ServerSend {

    /**
     * Send data to this
     */
    public ServerClient client;

    /**
     * Create server sending functionality for client
     * @param client Receiver
     */
    public ServerSend(ServerClient client) {
        this.client = client;
    }

    /**
     * Send the clients ID for client to claim
     */
    public void SendID() {
        Packet packet = new Packet(ServerPackets.ID.ordinal());
        packet.Write(client.id);
        client.SendData(packet);
    }

    /**
     * Disconnect the client client-side
     */
    public void Disconnect() {
        Packet packet = new Packet(ServerPackets.ForceDisconnect.ordinal());
        client.SendData(packet);
    }

}
