package Engine.Networking.Server;

import Editor.Console;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ServerClient {

    public Socket socket;
    public DataInputStream input;
    public DataOutputStream output;

    public int id;

    public ServerClient(Socket socket) {
        this.socket = socket;

        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
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

}
