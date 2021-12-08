package Radium.Networking.Server;

import RadiumEditor.Console;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static ServerSocket socket;

    public static List<ServerClient> clients = new ArrayList<>();
    private static int assignableID = 0;

    public static boolean Open = false;
    private static Thread acceptThread;

    protected Server() {}

    public static void Start(int port) {
        try {
            socket = new ServerSocket(port);

            Open = true;

            acceptThread = new Thread(() -> {
                while (Open) {
                    AcceptClients();
                }
            });
            acceptThread.start();
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public static void Close() {
        try {
            Open = false;
            socket.close();
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    private static void AcceptClients() {
        Socket newClient = null;
        try {
            newClient = socket.accept();
        } catch (Exception e) {
            if (!Open) {
                return;
            } else {
                Console.Error(e);
            }
        }
        ServerClient client = new ServerClient(newClient);
        client.id = assignableID;

        clients.add(client);
        assignableID++;

        client.send.SendID();
    }

}
