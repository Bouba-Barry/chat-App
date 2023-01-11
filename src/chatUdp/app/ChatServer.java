package chatUdp.app;

import chatUdp.dao.UserDao;
import chatUdp.entity.User;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.SQLException;
import java.util.List;

// ...

public class ChatServer {
    // Set up the database of accounts
    protected static List<User> ACCOUNTS;
    protected static final int PORT = 12345;
    public static void main(String[] args) throws IOException, SQLException {
        // avoir tous les clients;
        ACCOUNTS = UserDao.getAllUsers();
        // Set up the socket and
        // buffer for the server
        byte[] buffer = new byte[1024];
        DatagramSocket serverSocket = new DatagramSocket(PORT);

        while (true) {
        // Receive a message from a client
        //  Arrays.fill(buffer, (byte) 0);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(packet);

        // Create a new ClientThread to handle the client's connection
        ClientThread thread = new ClientThread(serverSocket, packet);
        // Start the thread
        thread.start();
        }
    }

}


