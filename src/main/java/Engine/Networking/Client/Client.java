package Engine.Networking.Client;

import Editor.Console;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Client {

    private String connectedIP;
    private int connectedPort;

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public Client() {

    }

    public void Connect(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public void Disconnect() {
        try {
            socket.close();
        } catch (Exception e) {
            Console.Error(e);
        }
    }

}
