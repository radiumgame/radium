package Radium.Networking.Client;

import RadiumEditor.Console;
import Radium.Networking.Packet;
import Radium.Networking.PacketType.ServerPackets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.function.Consumer;

/**
 * Class to connect to a dedicated server
 */
public class Client {

    private String connectedIP;
    private int connectedPort;
    /**
     * ID of client on server
     */
    public int clientID;

    /**
     * Used to handle incoming data
     */
    public ClientHandle handle;
    /**
     * Used to send data to server
     */
    public ClientSend send;

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    private boolean Connected = true;

    private Packet receivedData;
    private byte[] receiveBuffer;
    private Hashtable<Integer, Consumer<Packet>> packetHandlers = new Hashtable<>();

    private Thread updateThread;

    /**
     * Create an empty client
     */
    public Client() {
        InitializePacketHandlers();
    }

    /**
     * Conect the client to a dedicated server
     * @param ip IP of server
     * @param port Port server is running on
     */
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

    /**
     * Disconnect client from server
     */
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

    /**
     * Send a packet to the server
     * @param packet Data to send
     */
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
