package Engine.Networking.Server;

import Editor.Console;
import Engine.Networking.Packet;
import Engine.Networking.PacketType.ClientPackets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.function.Consumer;

public class ServerClient {

    public Socket socket;
    public DataInputStream input;
    public DataOutputStream output;

    public ServerHandle handle;
    public ServerSend send;

    private Packet receivedData;
    private byte[] receiveBuffer;

    private boolean Connected = false;

    private Hashtable<Integer, Consumer<Packet>> packetHandlers = new Hashtable<>();

    public int id;

    private Thread updateThread;

    public ServerClient(Socket socket) {
        this.socket = socket;

        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            handle = new ServerHandle(this);
            send = new ServerSend(this);

            receivedData = new Packet();
            receiveBuffer = new byte[4096];

            InitializePacketHandlers();

            Connected = true;

            updateThread = new Thread(() -> {
                while (Connected) {
                    Update();
                }
            });
            updateThread.start();
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public void Update() {
        try {
            input.read(receiveBuffer, 0, receiveBuffer.length);
            int byteLength = receiveBuffer.length;

            if (byteLength <= 0) {
                // Disconnect client
                return;
            }
            receivedData.Reset(HandlePacket(receiveBuffer));
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public void Disconnect() {
        try {
            socket.close();
            Server.clients.remove(id);
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public void ForceDisconnect() {
        send.Disconnect();
    }

    public void SendData(Packet packet) {
        try {
            packet.WriteLength();
            output.write(packet.ToArray(), 0, packet.Length());
            output.flush();
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public String GetIP() {
        return FormatIP(socket.getRemoteSocketAddress().toString());
    }

    private String FormatIP(String ip) {
        return ip.split("/")[1];
    }

    private void InitializePacketHandlers() {
        packetHandlers.put(ClientPackets.Disconnect.ordinal(), (Packet packet) -> {
            handle.ClientDisconnect(packet);
        });
    }

    private boolean HandlePacket(byte[] data) {
        int packetLength = 0;

        receivedData.SetBytes(data);
        if (receivedData.UnreadLength() >= 4) {
            packetLength = receivedData.ReadInt();
            if (packetLength <= 0) {
                return true;
            }
        }

        while (packetLength > 0 && packetLength <= receivedData.UnreadLength()) {
            byte[] packetBytes = receivedData.ReadBytes(packetLength);

            Packet newPacket = new Packet(packetBytes);
            int packetID = newPacket.ReadInt();
            packetHandlers.getOrDefault(packetID, (Packet packet) -> {
                Console.Warning("Received a packet with an unidentifiable id");
            }).accept(newPacket);

            packetLength = 0;
            if (receivedData.UnreadLength() >= 4) {
                packetLength = receivedData.ReadInt();
                if (packetLength <= 0) return true;
            }
        }

        if (packetLength <= 1) return true;

        return false;
    }

}
