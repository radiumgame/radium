package Radium.Networking.Client;

import Radium.Networking.Packet;

/**
 * Handle incoming data with callbacks
 */
public class ClientHandle {

    /**
     * Receiving client
     */
    public Client client;

    /**
     * Create a client handle for the client
     * @param client Receiver
     */
    public ClientHandle(Client client) {
        this.client = client;
    }

    /**
     * Claim ID that server assigns
     * @param packet Data
     */
    public void ClaimID(Packet packet) {
        client.clientID = packet.ReadInt();
    }

    /**
     * Receive a disconnect packet from server
     */
    public void Disconnect() {
        client.Disconnect();
    }

}
