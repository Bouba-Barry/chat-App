package chatUdp.app;

import chatUdp.dao.UserDao;
import chatUdp.entity.User;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.SQLException;
import java.util.List;

public class ChatServer {
    // une liste de tous les comptes
    protected static List<User> ACCOUNTS;
    protected static final int PORT = 12345;
    public static void main(String[] args) throws IOException, SQLException {
        // avoir tous les clients;
        ACCOUNTS = UserDao.getAllUsers();

        // buffer pour le server
        byte[] buffer = new byte[1024];
        DatagramSocket serverSocket = new DatagramSocket(PORT);

        while (true) {
        // Recevoir un message depuis un client
        //  Arrays.fill(buffer, (byte) 0);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(packet);

        // Créer un nouveau ClientThread to gerez la connexion de ce client
        // cette classe ClientThread permet de gérez les réquêtes, fonctionnalités de chaque client.
        ClientThread thread = new ClientThread(serverSocket, packet);
        // demarrer le thread.
        thread.start();
        }
    }

}


