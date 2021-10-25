package Engine.Networking.Client;

import Editor.Console;
import Engine.Networking.Packet;
import Engine.Networking.PacketType.ServerPackets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.function.Consumer;

public class Client {

    private String connectedIP;
    private int connectedPort;
    public int clientID;

    public ClientHandle handle;
    public ClientSend send;

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    private boolean Connected = true;

    private Packet receivedData;
    private byte[] receiveBuffer;
    private Hashtable<Integer, Consumer<Packet>> packetHandlers = new Hashtable<>();

    private Thread updateThread;

    public Client() {
        InitializePacketHandlers();
    }

    public void Connect(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            handle = new ClientHandle(this);
            send = new ClientSend(this);

            connectedIP = ip;
            connectedPort = port;

            receivedData = new Packet();
            receiveBuffer = new byte[4096];

            Connected = true;

            updateThread = new Thread(() -> {
                while (Connected) Update();
            });
            updateThread.start();
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    private void Update() {
        try {
            input.read(receiveBuffer, 0, receiveBuffer.length);
            int byteLength = receiveBuffer.length;

            if (byteLength <= 0) {
                Disconnect();
                return;
            }
            receivedData.Reset(HandlePacket(receiveBuffer));
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public void Disconnect() {
        try {
            Connected = false;

            if (socket.isConnected()) {
                send.Disconnect();
            }

            socket.close();
            connectedIP = null;
            connectedPort = -1;
            send = null;
            handle = null;
        } catch (Exception e) {
            Console.Error(e);
        }
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
                Console.Warning("Received a packet with an unidentifiable packet ID");
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

    private void InitializePacketHandlers() {
        packetHandlers.put(ServerPackets.ID.ordinal(), (Packet packet) -> { handle.ClaimID(packet); });
        packetHandlers.put(ServerPackets.ForceDisconnect.ordinal(), (Packet packet) -> { handle.Disconnect(); });
    }

}
