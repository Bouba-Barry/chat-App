package chatUdp.entity;

import chatUdp.dao.UserDao;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String username;
    private String password;
    private String numero;
    private String adresse;
    private List<User> friends = new ArrayList<>();
    private int port;
    private boolean isOnline;

    public User(){}
    public User(String username, String password, String num, String adresse, int port) {
        this.username = username;
        this.password = password;
        this.numero = num;
        this.adresse = adresse;
        this.port = port;
        this.isOnline = false;
    }
    public User(int id,String username, String password, String num, String adresse, int port) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.numero = num;
        this.adresse = adresse;
        this.port = port;
        this.isOnline = false;
    }


    public int getId() {
        return UserDao.readId(this.username);
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    public boolean getIsOnline(){
        return this.isOnline;
    }
    public void setIsOnline(boolean b){
        this.isOnline = b;
    }

    public List<User> getFriends() {
        return UserDao.getAllFriends(getId());
    }

    private void setFriends(List<User> friends) {
        this.friends = friends;
    }
}
