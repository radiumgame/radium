package Radium.Networking.Server;

import RadiumEditor.Console;
import Radium.Networking.Packet;
import Radium.Networking.PacketType.ClientPackets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.function.Consumer;

/**
 * Client data stored on the server
 */
public class ServerClient {

    /**
     * The clients socket
     */
    public Socket socket;
    /**
     * The client's socket input stream
     */
    public DataInputStream input;
    /**
     * The client's socket output stream
     */
    public DataOutputStream output;

    /**
     * Receive callbacks
     */
    public ServerHandle handle;
    /**
     * Data sender
     */
    public ServerSend send;

    private Packet receivedData;
    private byte[] receiveBuffer;

    private boolean Connected = false;

    private Hashtable<Integer, Consumer<Packet>> packetHandlers = new Hashtable<>();

    /**
     * Client's server ID
     */
    public int id;

    private Thread updateThread;

    /**
     * Create a client from a socket
     * @param socket Client socket
     */
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

    /**
     * Try to read data from input stream
     */
    public void Update() {
        try {
            input.read(receiveBuffer, 0, receiveBuffer.length);
            int byteLength = receiveBuffer.length;

            if (byteLength <= 0) {
                ForceDisconnect();
                return;
            }
            receivedData.Reset(HandlePacket(receiveBuffer));
        } catch (Exception e) {
            if (socket.isClosed()) return;
            else Console.Error(e);
        }
    }

    /**
     * Removes client from server data and closes socket
     */
    public void Disconnect() {
        try {
            Connected = false;

            socket.close();
            Server.clients.remove(id);
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    /**
     * Disconnect client side
     */
    public void ForceDisconnect() {
        send.Disconnect();
    }

    /**
     * Send a packet to this client
     * @param packet Data
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

    /**
     * @return IP of socket
     */
    public String GetIP() {
        return FormatIP(socket.getRemoteSocketAddress().toString());
    }

    /**
     * Formats {@link #FormatIP(String) IP} of socket
     * @param ip
     * @return
     */
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
