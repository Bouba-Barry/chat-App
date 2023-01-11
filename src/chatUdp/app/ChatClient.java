package chatUdp.app;

import chatUdp.dao.InvitationDao;
import chatUdp.dao.UserDao;
import chatUdp.entity.User;
import chatUdp.gui.ChatUI;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatClient {
    private static boolean menu = false;
    private static Scanner input = new Scanner(System.in);
    protected static DatagramSocket socket;
    protected static InetAddress serverAddress;
    protected static DatagramPacket packet;
    private static final  int PORT = 12345;
    protected static  byte[] buffer;
    protected static String username;

    public static void main(String[] args) throws IOException {

        serverAddress = InetAddress.getByName("localhost");
        socket = new DatagramSocket();
        // Prompt the user to choose between login and registration
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.print("Enter your choice: ");
        int choice = input.nextInt();
        input.nextLine(); // consume the newline character

        // Send the appropriate message to the server
        //byte[] buffer;
        String message ;
        if (choice == 1) {
            // This is a login request
            System.out.print("Enter your username: ");
            username = input.nextLine();
            System.out.print("Enter your password: ");
            String password = input.nextLine();
            message = "LOGIN:" + username + ":" + password;
            //  buffer = message.getBytes();
        } else {
            // This is a registration request
            System.out.print("Enter your desired username: ");
            username = input.nextLine();
            System.out.print("Enter your desired password: ");
            String password = input.nextLine();
            message = "REGISTER:" + username + ":" + password;
            //buffer = message.getBytes();
        }
      /*  packet = new DatagramPacket(buffer, buffer.length, serverAddress, PORT);
        socket.send(packet);*/
        sendMessage(socket,message,serverAddress,PORT);

        // Receive the response from the server
        packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String retour = new String(packet.getData(), 0, packet.getLength());

        // Handle the response from the server
        if (retour.equals("LOGIN_SUCESS")) {
            // The login was successful, print a confirmation message
            System.out.println("Login successful!");
            menu = true;
            // Now the user can perform other actions
        } else if (retour.equals("REGISTER_SUCCESS")) {
            // The registration was successful, print a confirmation message
            menu = true;
            System.out.println("Registration successful!");
            // Now the user can log in
        } else {
            // The login or registration failed, print an error message
            System.out.println("Error: " + message);
        }

        if(menu){
            menuApp();
        }
    }

    private static void menuApp(){
        // Prompt the user to choose between login and registration
        System.out.println("1. GO TO THE CHAT");
        System.out.println("2. SEE USER AND SEND INVITATION");
        System.out.println("3. DISCONNECT ");
        int choice = input.nextInt();
        input.nextLine(); // consume the newline character

        // Send the appropriate message to the server
        byte[] buffer;
        if (choice == 1) {
            System.out.println("------------------------ Chat Room ---------------------------------------------");
            try{
                chat();
                //receptMessage();
            }catch (Exception e){e.printStackTrace();}
        }
        else if(choice == 2){
            System.out.println("1. VIEW ALL DEMAND ");
            System.out.println("2. SEE USER AND SEND INVITATION");
            System.out.println("3. EXIT");
            int entrer = input.nextInt();
            input.nextLine();

            if(entrer == 1){
                requestInvitMissed();
                //menuApp();
            }
            else if(entrer == 2){
                seeAllUserAndInvite();
            }
            else {
                System.out.println("invalid choice !");
            }
        }
        else{
            socket.disconnect();
        }
    }

    private static void chat() throws IOException {
        // create a thread for the reception
        Thread thread = new Thread(() -> {
            byte tmp [] = new byte[1024];

            while (true) {
                //Arrays.fill(tmp, (byte) 0);
                DatagramPacket packet = new DatagramPacket(tmp, tmp.length);
                try {
                    socket.receive(packet);
                    System.out.println("le client a lancé socke.receive ... ");
                    String message = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(message);
                    String [] content = message.split(",");
                    if (content.length > 1) {
                        System.out.println(content[1]);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });thread.start();

        String messageToSend;
        do{
            messageToSend = input.nextLine();
            // Send the message
            if(! messageToSend.equalsIgnoreCase("EXIT")){
                sendMessage(socket,"CHAT:"+username+":"+messageToSend,serverAddress, PORT);
            }else{
                sendMessage(socket,messageToSend,serverAddress, PORT);
            }
        }while (! messageToSend.equalsIgnoreCase("EXIT"));
        menuApp();
    }
    private  static  void receptMessage(){
    }

    private static void sendMessage(DatagramSocket socket, String message, InetAddress address, int port)  {
        buffer = message.getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, port);
        try{
            socket.send(packet);
        }catch (IOException e){e.printStackTrace();}
    }

    private static void seeAllUserAndInvite(){
        String choice;
        List<String> invitations = new ArrayList<String>();
        System.out.println("Enter his UserName to send Invitation oR Exit to quit");
        System.out.println("List of All Client");
        for (User cl : UserDao.getAllUsers()){
            System.out.println(cl.getNumero() +": " +cl.getUsername());
        }
        do {
            System.out.println("Print the Username to Send Invitation !");
            choice =  input.nextLine();
            //input.nextLine();
            String message = choice;
            if(! choice.equalsIgnoreCase("EXIT")){
                invitations.add(message);
            }
        }while ( ! choice.equalsIgnoreCase("EXIT"));
        // send all the invitation to the server
        sendMessage(socket,"INVITE:"+username+","+convertListToString(invitations),serverAddress,PORT);
        System.out.println("message envoyer au server ");
        menuApp();
    }
    // Send a request to the server asking for any invitations that were sent to the user
    public static void requestInvitMissed() {
        // envoyer un message au server pour recevoir les invitations manqué lors qu'il etait hors ligne
        sendMessage(socket, "REQUEST_INVITATION:"+username, serverAddress, PORT);

        // wait for the server to send back all the invitation that i missed
        byte[] data = new byte[1024];
        packet = new DatagramPacket(data, data.length);
        try{
            socket.receive(packet);
        }catch (IOException e){e.printStackTrace();}
        String message = new String(packet.getData(),0,packet.getData().length);
        if(message.startsWith("DEMAND,")){
            String parts[] = message.split(",");
            System.out.println("1=ACCEPTÉ et 2=REFUSÉ");
            if(parts.length > 1){
                for(int i = 1; i < parts.length; i++){
                    String tmp [] = parts[i].split(":");
                    // System.out.println(parts[i]);
                    int idInvite = 0;
                    try {
                        idInvite = Integer.parseInt(tmp[1].trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid value: " + tmp[1] + " is not a number.");
                    }
                    String nomSender = tmp[0];
                    System.out.println("Demande Amitié de: "+nomSender);
                    // System.out.println("entrez your choice ?");
                    int choice = input.nextInt();
                    input.nextLine();

                    if(choice == 1){
                        // System.out.println("a");
                        // ajout comme amis:
                        //UserDao.addFriend(username,UserDao.readId(nomSender),nomSender,UserDao.readId(username));
                        UserDao.addFriendShip(UserDao.readId(username), UserDao.readId(nomSender));
                        InvitationDao.delete(idInvite);
                    } else if (choice == 2) {
                        System.out.println("vous avez refuser une invitation");
                        InvitationDao.delete(idInvite);
                    }else {
                        System.out.println("choix invalide: Tapez 1: accepter ou 2: refuser !");
                    }
                }
            }
            else{
                System.out.println("taille d'invitation reçu petit ");
            }

        }
        menuApp();
    }

    // Convert a list to a string
    public static String convertListToString(List<String> myList) {
        // Iterate through the list of pending invitations and create a string with the sender's usernames
        StringBuilder resInString = new StringBuilder();
        for (String list : myList) {
            resInString.append(list + ",");
        }
        // Remove the trailing comma
        if(resInString.length() > 0) {
            resInString.setLength(resInString.length() - 1);
        }
        return resInString.toString();
    }

    public static void logOut(){
        socket.close();
    }

}