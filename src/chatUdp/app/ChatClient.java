package chatUdp.app;

import chatUdp.dao.InvitationDao;
import chatUdp.dao.UserDao;
import chatUdp.entity.User;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedHashSet;
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
        int choice = 0;
        String message ;
      quit:do{
        System.out.println("entrez 1 Ou 2 pour faire Votre choix");
        System.out.println("1. Login");
        System.out.println("2. Registration");
        System.out.print("\nvotre choix SVP: ");
        choice = input.nextInt();
        input.nextLine();

        if(choice == 1 || choice == 2)
            break quit;
      }while(choice != 1 || choice != 2);
        if (choice == 1) {

            System.out.print("Entrer votre username: ");
            username = input.nextLine();
            System.out.print("Entrer votre password: ");
            String password = input.nextLine();
            message = "LOGIN:" + username + ":" + password;
            //  buffer = message.getBytes();
        } else {
            // la requête de l'enregistrement
            System.out.print("Enter votre Nom pour l'enregistrement: ");
            username = input.nextLine();
            System.out.print("Enter votre Password : ");
            String password = input.nextLine();
            message = "REGISTER:" + username + ":" + password;
        }

        sendMessage(socket,message,serverAddress,PORT);

        // receiver un message du server pour la reponse de vos informations
        packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String retour = new String(packet.getData(), 0, packet.getLength());

        // verifiez la réponse que le server a envoyer
        if (retour.equals("LOGIN_SUCESS")) {
            // Le login a été un success
            System.out.println("Login successful!");
            menu = true;

        } else if (retour.equals("REGISTER_SUCCESS")) {
            // l'enregistrement a été un succes
            menu = true;
            System.out.println("Registration successful!");
        } else {
            // Le login ou registration a echouer
            System.out.println("Error: " + message);
        }
        if(menu){
            menuApp();
        }
    }

    private static void menuApp(){
        // demander au user ce qu'il veut , ceci est le menu de l'app
        int choice = 0;
        boucle:do {
            System.out.println("\n\t 1. ALLEZ AU CHAT");
            System.out.println("\t 2. GEREZ LES INVITATIONS");
            System.out.println("\t 3. Voir Liste Amis");
            System.out.println("\t 4. DECONNEXION \n");
            System.out.print(" Entrez Votre Choix ( 1 ou 2 ou 3 ou 4 ): ");
             choice = input.nextInt();
            input.nextLine();

            byte[] buffer;
            if (choice == 1) {
                System.out.println("------------------------ Chat Room ---------------------------------------------");
                System.out.println("\t Pour quitter le chat entrez 'EXIT' ! ");
                try {
                    chat();
                    //receptMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (choice == 2) {
                System.out.println("\n---------------gestion Invitations ----------------");
                System.out.println("1. VOIR LES DEMANDES MANQUÉES ");
                System.out.println("2. ENVOYEZ DES INVITATIONS ");
                System.out.println("3. SORTIR \n");
                System.out.print("Entrez Votre Choix (1 ou 2 ou 3 ): ");
                int entrer = input.nextInt();
                input.nextLine();

                if (entrer == 1) {
                    // cette fonction va permettre d'envoyez une requete au server, pour avoir toutes les invitations ratés
                    requestInvitMissed();

                } else if (entrer == 2) {
                    // cette fonction va permettre d'envoyez des demandes
                    seeAllUserAndInvite();
                } else {
                    System.out.println("choix non valide !");
                }
            }
            else if(choice == 3) {
                if(SeeFriends().isEmpty()){
                    System.out.println("Vous n'avez Aucun amis !");
                }else{
                    for(User u: SeeFriends())
                        System.out.println(u.getUsername()+ " adresse "+u.getAdresse());
                }
            }
            else if(choice == 4){
                System.out.println("deconnexion en cours");
                socket.disconnect();
                socket.close();
                break boucle;
            }
            else{
                System.out.println("choix invalide veuillez Réessayer !!");
            }
        }while (choice != 1 || choice != 2 || choice != 3 || choice != 4);
    }

    /**
     * cette fonction ne prend rien en paramètre ,
     * elle permet de créer un thread en background pour afficher les messages du chat. en plus de l'envoi aussi
     * @throws IOException
     */
    private static void chat() throws IOException {
        // créer un thread pour la reception de message
        Thread thread = new Thread(() -> {
            byte tmp [] = new byte[1024];

            while (true) {
                //Arrays.fill(tmp, (byte) 0);
                DatagramPacket packet = new DatagramPacket(tmp, tmp.length);
                try {
                    socket.receive(packet);
                    //System.out.println("le client a lancé socke.receive ... ");
                    String message = new String(packet.getData(), 0, packet.getLength());
                    //System.out.println(message);
                    if(message.startsWith("brodcast,")) {
                        String[] content = message.split(",");
                        if (content.length > 1) {
                            System.out.println(content[1]);
                        }
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
                String m = "EXIT:"+username;
                sendMessage(socket,m,serverAddress, PORT);
            }
        }while (! messageToSend.equalsIgnoreCase("EXIT"));
        // réafficher le menu au user pour qu'il puisse faire d'autre choses comme l'invitation
        menuApp();
    }

    /**
     * cette fonction envoyé un message au server
     * @param socket
     * @param message
     * @param address
     * @param port
     */
    private static void sendMessage(DatagramSocket socket, String message, InetAddress address, int port)  {
        buffer = message.getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, port);
        try{
            socket.send(packet);
        }catch (IOException e){e.printStackTrace();}
    }

    /**
     * permet d'afficher la liste des users et lui envoyé demande s'il veut
     */
    private static void seeAllUserAndInvite(){
        String choice;
        List<String> invitations = new ArrayList<String>();
        System.out.println("Entrez Son Nom pour envoyé la demande, OU EXIT pour sortir !!");
        System.out.println("Liste de Tous les Users ");
        LinkedHashSet<String> friendUsernames = new LinkedHashSet<>();
        List<User> friends = SeeFriends();
        for (User f : friends) {
            friendUsernames.add(f.getUsername());
        }
        for (User cl : UserDao.getAllUsers()) {
            if (!friendUsernames.contains(cl.getUsername())) {
                if(!cl.getUsername().equals(username))
                    System.out.println(cl.getUsername());
            }
        }
        do {
            System.out.println("Entrez Le nom de celui à qui vous voulez être amis !");
            choice =  input.nextLine();
            //input.nextLine();
            String message = choice;
            if(! choice.equalsIgnoreCase("EXIT")){
                invitations.add(message);
            }
        }while ( ! choice.equalsIgnoreCase("EXIT"));
        // recupperez tous les invitaions du users et envoyés au server.
        sendMessage(socket,"INVITE:"+username+","+convertListToString(invitations),serverAddress,PORT);
        //System.out.println("message envoyer au server ");
        // afficher le menu au user quand il fini
        menuApp();
    }
    // envoyé une requête au server pour demander tous mes invitations manqués
    public static void requestInvitMissed() {
        // envoyer un message au server pour recevoir les invitations manqué lors qu'il etait hors ligne
        sendMessage(socket, "REQUEST_INVITATION:"+username, serverAddress, PORT);

        // attente d'une réponse
        byte[] data = new byte[1024];
        packet = new DatagramPacket(data, data.length);
        try{
            socket.receive(packet);
        }catch (IOException e){e.printStackTrace();}

        String message = new String(packet.getData(),0,packet.getData().length);

        // checker la réponse envoyé par le server...
        so:if(message.startsWith("DEMAND,")){
            String parts[] = message.split(",");
            if(parts.length > 1){
                for(int i = 1; i < parts.length; i++){
                    String tmp [] = parts[i].split(":");
                    // System.out.println(parts[i]);
                    int idInvite = 0;
                    try {
                        if(tmp.length > 1){
                           // System.out.println("idInvite => " +tmp[1]);
                            idInvite = Integer.parseInt(tmp[1].trim());
                        }
                        else {
                            System.out.println("Aucune Invitation Manqué ");
                            break so;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("valeur Invalide: " + tmp[1] + " pas un nombre.");
                    }
                    String nomSender = tmp[0];

                        System.out.println("Demande Amitié de: " + nomSender);
                        System.out.println("TAPER 1 pour ACCEPTÉ OU 2 pour REFUSÉ");
                        // System.out.println("entrez your choice ?");

                         int choice = input.nextInt();
                        input.nextLine();

                        if (choice == 1) {
                            // System.out.println("a");
                            // ajout comme amis:
                            //UserDao.addFriend(username,UserDao.readId(nomSender),nomSender,UserDao.readId(username));
                            System.out.println("vous avez accepter une amitié de "+nomSender);
                            UserDao.addFriendShip(UserDao.readId(username), UserDao.readId(nomSender));
                            InvitationDao.delete(idInvite);
                        } else if (choice == 2) {
                            System.out.println("vous avez refuser une amitié de "+nomSender);
                            InvitationDao.delete(idInvite);
                        } else {
                            System.out.println("choix invalide: Tapez 1 pour accepter ou 2 pour refuser !");
                        }

                }
            }
            else{
                System.out.println("Aucune invitation manqué !! ");
            }

        }
        menuApp();
    }

    /**
     * cette fonction permet de prendre une list et la convertir en string,
     * elle est utilisé lors du regroupement des invitations multiples
     * @param myList
     * @return
     */
    public static String convertListToString(List<String> myList) {
        StringBuilder resInString = new StringBuilder();
        for (String list : myList) {
            resInString.append(list + ",");
        }
        if(resInString.length() > 0) {
            resInString.setLength(resInString.length() - 1);
        }
        return resInString.toString();
    }
    public static  List<User> SeeFriends(){
            return UserDao.getAllFriends(UserDao.readId(username));
    }

}