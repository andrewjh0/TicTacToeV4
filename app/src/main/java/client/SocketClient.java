package client; // Replace with your actual package name

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import socket.Request;
import socket.Response;

public class SocketClient {
    private static final String SERVER_ADDRESS = "128.153.171.0"; // Replace with the server's IP address
    private static final int SERVER_PORT = 5000; // Replace with the server's port number

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // Singleton instance
    private static SocketClient instance = null;

    private SocketClient() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized SocketClient getInstance() {
        if (instance == null) {
            instance = new SocketClient();
        }
        return instance;
    }

    public void send(Object data) {
        try {
            out.writeObject(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object receive() {
        try {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


