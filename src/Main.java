import chatUdp.app.ChatClient;
import chatUdp.dao.InvitationDao;
import chatUdp.dao.UserDao;
import chatUdp.entity.Invitation;
import chatUdp.entity.User;
import chatUdp.gui.ChatUI;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
/*
        for(Invitation i : InvitationDao.getAllInvitesNotSended(2)){
            System.out.println(i.getId()+" " + i.getReceiver_id());
        }

        System.out.println(UserDao.getNameByID(1));
        System.out.println("affichage des infos amis de barry");
        for (User u: UserDao.getAllFriends(1)){
            System.out.println("friend_name: "+ u.getUsername());
        }
 */


    }

}