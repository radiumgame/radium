package Engine.Networking.Server;

import Editor.Console;
import Engine.Util.NonInstantiatable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.Hashtable;

public final class Server extends NonInstantiatable {

    private static ServerSocket socket;

    private static Hashtable<Integer, ServerClient> clients = new Hashtable<>();
    private static int assignableID = 0;

    private static boolean Open = false;
    private static Thread acceptThread;

    private static boolean Log = false;

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

            Log("Server opened");
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public static void Close() {
        try {
            socket.close();

            Open = false;

            Log("Server closed");
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    private static void AcceptClients() throws IOException {
        Socket newClient = socket.accept();
        ServerClient client = new ServerClient(newClient);

        client.id = assignableID;
        Log("Client has connected from " + client.GetIP() + " and has assumed an id of " + client.id);

        assignableID++;
    }

    private static void Log(String message) {
        if (!Log) return;

        Console.Log(message);
    }

}
