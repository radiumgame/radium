package Radium.Networking.Server;

import Radium.Networking.Packet;

/**
 * Receive callbacks for server clients
 */
public class ServerHandle {

    public ServerClient client;

    /**
     * Create callbacks for client
     * @param client
     */
    public ServerHandle(ServerClient client) {
        this.client = client;
    }

    /**
     * Receive a disconnect packet from client
     * @param packet
     */
    public void ClientDisconnect(Packet packet) {
        client.Disconnect();
    }

}
