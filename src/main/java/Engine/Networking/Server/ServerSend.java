package Engine.Networking.Server;

import Editor.Console;
import Engine.Networking.Packet;
import Engine.Networking.PacketType.ServerPackets;

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

}
