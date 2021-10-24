package Engine.Networking.Server;

import Editor.Console;
import Engine.Networking.Packet;
import Engine.Networking.PacketType.ServerPackets;
import Engine.Util.NonInstantiatable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public final class Server extends NonInstantiatable {

    private static ServerSocket socket;

    private static List<ServerClient> clients = new ArrayList<>();
    private static int assignableID = 0;

    private static boolean Open = false;
    private static Thread acceptThread;

    private static boolean Log = true;

    public static void Start() {
        try {
            socket = new ServerSocket(444);

            Open = true;

            acceptThread = new Thread(() -> {
                while (Open) {
                    try {
                        AcceptClients();
                    } catch (Exception e) {
                        Console.Error(e);
                    }
                }
            });
            acceptThread.start();
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public static void Close() {
        try {
            socket.close();

            Open = false;
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    private static void AcceptClients() throws IOException {
        Socket newClient = socket.accept();
        ServerClient client = new ServerClient(newClient);
        client.id = assignableID;

        clients.add(client);
        assignableID++;

        client.send.SendID();
    }

}
