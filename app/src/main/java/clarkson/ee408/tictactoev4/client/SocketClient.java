package clarkson.ee408.tictactoev4.client; // Replace with your actual package name

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import clarkson.ee408.tictactoev4.socket.Request;

public class SocketClient {
    private static final String SERVER_ADDRESS = "10.0.0.11"; // Replace with the server's IP address
    private static final int SERVER_PORT = 5000; // Replace with the server's port number

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Gson gson;

    // Singleton instance
    private static SocketClient instance = null;

    private SocketClient() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            gson = new GsonBuilder().serializeNulls().create();
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
    public <T> T sendRequest (Request request, Class<T> responseClass) {
        try {
            // Read the client's serialized request
            String serializedRequest = gson.toJson(request);
            out.writeUTF(serializedRequest);
            out.flush();

            String serializedResponse = in.readUTF();
            T response = gson.fromJson(serializedResponse, responseClass);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            close();
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


