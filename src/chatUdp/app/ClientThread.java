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
    protected static int num = 3224;
    private boolean isFirstQueryOk;
    private String nom;
    protected static List<User> loggedClient= new ArrayList<User>();
    public ClientThread(DatagramSocket serverSocket, DatagramPacket packet) {
        this.packet = packet;
        this.serverSocket = serverSocket;
        this.isFirstQueryOk = false;
        num ++;
    }


    @Override
    public void run() {
        init();
        System.out.println("les personnes connecté :");

            for (User u : loggedClient)
                System.out.println("user => "+u.getUsername());

            handleChat();
        try {
            handleInvitation();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        handleInvitationNotSended();

    }

    public void init(){
        String message = new String(packet.getData(), 0, packet.getLength());
        System.out.println("message: "+message);
        String response = "";
        if (message.startsWith("LOGIN:")) {
            // This is a login request
            String[] parts = message.split(":");
            String username = parts[1];
            String password = parts[2];

            // Check if the provided username and password match with existing account
            boolean loginSuccess = false;
            String tmpNum = null;
            for (int i = 0; i < ChatServer.ACCOUNTS.size(); i++) {
                User user = ChatServer.ACCOUNTS.get(i);
                if (user.getUsername().equalsIgnoreCase(username) && user.getPassword().equals(password)) {
                    loginSuccess = true;
                    tmpNum = user.getNumero();
                    user.setAdresse(packet.getAddress().toString());
                    user.setPort(packet.getPort());
                    user.setIsOnline(true);
                   loggedClient.add(user);
                    break;
                }
            }

            // Send a response to the client
            String tmpName;
            if(loginSuccess){
                response = "LOGIN_SUCESS";
               // Client client = new Client(packet.getAddress(), packet.getPort(), this.name, tmpNum);
               //loggedClient.add(currentClient);
               // System.out.println(username);
                tmpName = username;
                //System.out.println("blabla "+tmpName);
                this.nom = tmpName;
               // System.out.println("blablabal "+this.nom);
                //loggedClient.add(currentClient);
            }
            else{
                response = "LOGIN_FAILLED";
            }
            isFirstQueryOk = loginSuccess;
            sendMessage(serverSocket,response,username,packet.getPort());
        }
        else if (message.startsWith("REGISTER:")) {
            // This is a registration request
            String[] parts = message.split(":");
            String username = parts[1];
            String password = parts[2];

            // Check if the desired username is already taken
            boolean usernameTaken = false;
            for (int i = 0; i < ChatServer.ACCOUNTS.size(); i++) {
                User user = ChatServer.ACCOUNTS.get(i);
                if (user.getUsername().equals(username)) {
                    usernameTaken = true;
                    break;
                }
            }

            // Send a response to the client
            if (usernameTaken) {
                response = "REGISTER_FAILED";
            } else {
                // Add the new account to the database
                User client = new User();
                client.setUsername(username);
                client.setPassword(password);
                client.setNumero(String.valueOf(num));
                //ChatServer.ACCOUNTS.add(client);

                response = "REGISTER_SUCCESS";
                // connecter le client direct en l'ajoutant sur la liste
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
        //broadcastAllFriend(serverSocket,username+" has now been logged ");
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
        System.out.println("dans le brodcast on a le sender: "+sender);
        User user = UserDao.getByName(sender);
        List<User> friends = user.getFriends();
        for(User u: friends)
            System.out.println("name_friend = "+u.getUsername());
        message = "brodcast,"+sender+":"+message;
        byte[] buffer = message.getBytes();
        for(User cl : loggedClient){

                //System.out.println(cl.getIsOnline());
                //System.out.println(cl.getAdresse());
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
        System.out.println("message réçu = "+message);
        //while(message != null && !message.equals("EXIT")){

        if(message.startsWith("CHAT:")){
            String[] parts = message.split(":");
            String content = parts[2];
            String username = parts[1];
            // si y'a aucune personne connecté ...
                broadcastAllFriend(serverSocket,content, username);
        }
      //  }
    }

    public void handleInvitation() throws SQLException, UnknownHostException {
        String message = new String(packet.getData(), 0, packet.getLength());
        if(message.startsWith("INVITE:")){
        System.out.println("new demand de "+message);

            String [] parts = message.split(",");
            String tmp [] = parts[0].split(":");
            String sender = tmp[1];
            User user_sender = UserDao.getByName(sender);
            for(int i = 1; i<parts.length; i++){
                    System.out.println(parts[i]);
                    User user_receiver = UserDao.getByName(parts[i]);
                System.out.println("nameInvitere = "+user_receiver.getUsername());
                System.out.println("son id: "+user_receiver.getId());
                    InvitationDao.addInvite(new Invitation(user_sender.getId(), user_receiver.getId(), "attente"));
                    System.out.println("invitation enregistré !! ");
                    String inv = "INVITATION:"+user_sender.getUsername();
                    if(user_receiver.getIsOnline()){
                        sendMessage(serverSocket,inv,user_receiver.getUsername(), user_receiver.getPort());
                    }
                }
            }
        }
    public void handleInvitationNotSended()  {
        String message = new String(packet.getData(), 0, packet.getLength());
        if(message.startsWith("REQUEST_INVITATION:")){
        System.out.println("Invitation = "+message);
        List<Invitation> invitationList;
            String parts [] = message.split(":");
                User user = UserDao.getByName(parts[1]);
            System.out.println("userRequestInvite: " +user.getId());
            invitationList = InvitationDao.getAllInvitesNotSended(user.getId());

           String resultat =  convertInvitationsToString(invitationList);
            System.out.println("tous mes invite en string " +resultat);
            //convertListToString(invitationList);
            System.out.println("le User "+user.getUsername());
                sendMessage(serverSocket,"DEMAND,"+resultat,user.getUsername(), user.getPort());

            System.out.println("invitation manqué envoyé au user ");
        }
    }
    public String convertInvitationsToString(List<Invitation> invitations) {
        // Iterate through the list of pending invitations and create a string with the sender's usernames
        StringBuilder invitationsString = new StringBuilder();
        for (Invitation invitation : invitations) {
            //System.out.println("invitation id " +invitation.getId());
            invitationsString.append(UserDao.getNameByID(invitation.getSender_id()) + ":"+invitation.getId()+",");
        }
        // Remove the trailing comma
        if(invitationsString.length() > 0) {
            invitationsString.setLength(invitationsString.length() - 1);
        }

        return invitationsString.toString();
    }
}