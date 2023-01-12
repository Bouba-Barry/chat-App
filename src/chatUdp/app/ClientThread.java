package chatUdp.app;

import chatUdp.dao.InvitationDao;
import chatUdp.dao.UserDao;
import chatUdp.entity.Invitation;
import chatUdp.entity.User;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientThread extends  Thread {

    private DatagramPacket packet;
    private DatagramSocket serverSocket;
    private boolean isFirstQueryOk;
    private String nom;
    protected static List<User> loggedClient= new ArrayList<User>();
    public ClientThread(DatagramSocket serverSocket, DatagramPacket packet) {
        this.packet = packet;
        this.serverSocket = serverSocket;
        this.isFirstQueryOk = false;
    }

    @Override
    public void run() {
        // la fonction init gére le login ou l'enregistrement du user
        init();

        // gerez le chat entre les amis du user
        handleChat();
        try {
            // gerez si réçoit une réquete d'invitation du user
            handleInvitation();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        // si le user connecté demande les invitations qu'il a manqué
        handleInvitationNotSended();
    }

    public void init(){
        String message = new String(packet.getData(), 0, packet.getLength());
        System.out.println("message: "+message);
        String response = "";
        if (message.startsWith("LOGIN:")) {
            // lors du login, ses infos son envoyé, il faut le split pour recupérer le nom et password
            String[] parts = message.split(":");
            String username = parts[1];
            String password = parts[2];

            // vérifier si le username et password existe
            boolean loginSuccess = false;

            for (int i = 0; i < ChatServer.ACCOUNTS.size(); i++) {
                User user = ChatServer.ACCOUNTS.get(i);
                if (user.getUsername().equalsIgnoreCase(username) && user.getPassword().equals(password)) {
                    loginSuccess = true;
                    user.setAdresse(packet.getAddress().toString());
                    user.setPort(packet.getPort());
                    user.setIsOnline(true);
                    // enregistrez le user sur la liste de ceux connecté
                   loggedClient.add(user);
                    broadcastAllFriend(serverSocket," a join le chat ", username);
                    break;
                }
            }

            // envoyé un retour au client
            if(loginSuccess)
                response = "LOGIN_SUCESS";
            else
                response = "LOGIN_FAILLED";

            isFirstQueryOk = loginSuccess;

            sendMessage(serverSocket,response,username,packet.getPort());
        }
        else if (message.startsWith("REGISTER:")) {

            String[] parts = message.split(":");
            String username = parts[1];
            String password = parts[2];

            // vérifier si le username n'est pas déjà pris
            boolean usernameTaken = false;
            for (int i = 0; i < ChatServer.ACCOUNTS.size(); i++) {
                User user = ChatServer.ACCOUNTS.get(i);
                if (user.getUsername().equals(username)) {
                    usernameTaken = true;
                    break;
                }
            }

            // envoyé une réponse au client
            if (usernameTaken) {
                response = "REGISTER_FAILED";
            } else {
                // Ajouter le compte en db;
                User client = new User();
                client.setUsername(username);
                client.setPassword(password);

                response = "REGISTER_SUCCESS";
                // connecter le client direct en l'ajoutant sur la liste loggedClient
                client.setPort(packet.getPort());
                client.setAdresse(packet.getAddress().toString());
                client.setIsOnline(true);
                String tmpName = username;
                //currentClient = client;
                UserDao.addUser(client);
                loggedClient.add(client);
                System.out.println("blbal  " +tmpName);
            }
            isFirstQueryOk = usernameTaken;
            sendMessage(serverSocket,response,username,packet.getPort());
        }
    }

    private static void sendMessage(DatagramSocket socket,String message,String receiver, int port) {
        byte[] buffer = message.getBytes();
        for(User cl : loggedClient){
            System.out.println(cl.getAdresse());
            try{
                if(cl.getUsername().equalsIgnoreCase(receiver)){
                    System.out.println("you see = "+cl.getAdresse());
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length,InetAddress.getByName(cl.getAdresse().replace("/", "")),cl.getPort());
                    socket.send(packet);
                    System.out.println(message + " enoyé à  "+cl.getUsername());
                }
            }
            catch(Exception e){e.printStackTrace();}
        }
    }
    private  void broadcastAllFriend(DatagramSocket socket,String message, String sender){
      //  System.out.println("dans le brodcast on a le sender: "+sender);
        User user = UserDao.getByName(sender);
        List<User> friends = user.getFriends();
        message = "brodcast,"+sender+":"+message;
        byte[] buffer = message.getBytes();
        for(User cl : loggedClient){
            for(User u: friends){
                if(cl.getUsername().equals(u.getUsername()))
                    try{
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length,InetAddress.getByName(cl.getAdresse().replace("/", "")) ,cl.getPort());
                        socket.send(packet);
                        System.out.println(message + " enoyé à  "+cl.getUsername());
                    }
                    catch(Exception e){e.printStackTrace();}
            }

        }
    }
    public void handleChat(){
        String message = new String(packet.getData(), 0, packet.getLength());
        System.out.println("message réçu du chat = "+message);

        if(message.startsWith("CHAT:")){
            String[] parts = message.split(":");
            String content = parts[2];
            String username = parts[1];
                broadcastAllFriend(serverSocket,content, username);
        }
        if(message.startsWith("EXIT:")){
            String[] parts = message.split(":");
            String username = parts[1];
            broadcastAllFriend(serverSocket," a quitté le chat ! ", username);
        }

    }

    /**
     * gerez si un user envoi une réquete d'invitation ...
     * @throws SQLException
     * @throws UnknownHostException
     */
    public void handleInvitation() throws SQLException, UnknownHostException {
        String message = new String(packet.getData(), 0, packet.getLength());

        // vérifier si le méssage réçu par le server commence par INVITE, alors il s'agit d'une réquête d'invitation
        if(message.startsWith("INVITE:")){
        System.out.println("new demand de "+message);

            String [] parts = message.split(",");
            String tmp [] = parts[0].split(":");
            String sender = tmp[1];
            User user_sender = UserDao.getByName(sender);
            for(int i = 1; i<parts.length; i++){
                    System.out.println(parts[i]);
                    User user_receiver = UserDao.getByName(parts[i]);
                //System.out.println("son id: "+user_receiver.getId());
                    InvitationDao.addInvite(new Invitation(user_sender.getId(), user_receiver.getId(), "attente"));
                    String inv = "INVITATION:"+user_sender.getUsername();
                    if(user_receiver.getIsOnline()){
                        sendMessage(serverSocket,inv,user_receiver.getUsername(), user_receiver.getPort());
                    }
                }
            }
        }

    /**
     * gérez la réquête lié au invitation non réçu par le user
     */
    public void handleInvitationNotSended()  {
        String message = new String(packet.getData(), 0, packet.getLength());
        if(message.startsWith("REQUEST_INVITATION:")){
            //System.out.println("Invitation = "+message);
        List<Invitation> invitationList;
            String parts [] = message.split(":");
                User user = UserDao.getByName(parts[1]);

            invitationList = InvitationDao.getAllInvitesNotSended(user.getId());

           String resultat =  convertInvitationsToString(invitationList);
            //System.out.println("tous mes invite en string " +resultat);

            sendMessage(serverSocket,"DEMAND,"+resultat,user.getUsername(), user.getPort());
        }
    }

    /**
     * convertir toute les invitations manqué en string pour les envoyés au users ...
     * @param invitations
     * @return String, tous les invitations du user
     */
    public String convertInvitationsToString(List<Invitation> invitations) {

        StringBuilder invitationsString = new StringBuilder();
        for (Invitation invitation : invitations) {
            //System.out.println("invitation id " +invitation.getId());
            invitationsString.append(UserDao.getNameByID(invitation.getSender_id()) + ":"+invitation.getId()+",");
        }
        if(invitationsString.length() > 0) {
            invitationsString.setLength(invitationsString.length() - 1);
        }

        return invitationsString.toString();
    }
}