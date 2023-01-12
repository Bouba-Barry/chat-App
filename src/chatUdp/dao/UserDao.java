package chatUdp.dao;

import chatUdp.dbConnection.ConnectDB;
import chatUdp.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private static Connection connection = ConnectDB.getConnection();

    public static void addUser(User user) {
        String sql = "INSERT INTO User (nom, password, adresse, port) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getAdresse());
            statement.setInt(4, user.getPort());
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
            ConnectDB.closeConnection();
        }
    }


    public static User read(int id) throws SQLException {
        String sql = "SELECT * FROM User WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String username = resultSet.getString("nom");
                String password = resultSet.getString("password");
                String adresse = resultSet.getString("adresse");
                int port = resultSet.getInt("port");
                return new User(id,username,password,adresse, port);
            } else {
                return null;
            }
        }
    }
    public static int readId(String username) {
        int id = -1;
        String sql = "SELECT id FROM User WHERE nom = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                id = resultSet.getInt("id");
            }
        }catch (SQLException e ){e.printStackTrace();}
        return id;
    }
    public static User getByName(String name){
        String sql = "SELECT * FROM User WHERE nom = ?";
        User user = null;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,name);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                String username = resultSet.getString("nom");
                String password = resultSet.getString("password");
                String adresse = resultSet.getString("adresse");
                int port = resultSet.getInt("port");
                user = new User(username,password, adresse, port);
            }
        }catch (SQLException e){e.printStackTrace();}
        return user;
    }
    public static String getNameByID(int id){
        String sql = "SELECT nom FROM User WHERE id = ? ";
        String name = "";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                name = resultSet.getString("nom");
            }else{
                System.out.println("User not found with id: " + id);
            }
        }catch (SQLException e){e.printStackTrace();}
        return name;
    }
    public static List<User> getAllFriends(int id) {
        List<User> friends = new ArrayList<>();
        String sql = "SELECT u.* FROM User u, FriendShip f WHERE f.friend_id = u.id AND f.user_id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                String username = resultSet.getString("nom");
                String password = resultSet.getString("password");
                String adresse = resultSet.getString("adresse");
                int port = resultSet.getInt("port");
                friends.add(new User(username,password,adresse, port));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return friends;
    }
    public static List<User> getAllUsers(){
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()) {
                String username = resultSet.getString("nom");
                String password = resultSet.getString("password");
                String adresse = resultSet.getString("adresse");
                int port = resultSet.getInt("port");
                users.add(new User(username,password, adresse, port));
            }
        }catch (SQLException e) {
            e.printStackTrace();
            // ConnectDB.closeConnection();
        }
        return users;
    }


    public static void addFriendShip(int idUser, int idFriend)  {
        String sql1 = "INSERT INTO FriendShip (user_id, friend_id) VALUES (?, ?)";
        String sql2 = "INSERT INTO FriendShip (user_id, friend_id) VALUES (?, ?)";
        PreparedStatement statement1;
        PreparedStatement statement2;
        try {
            statement1 = connection.prepareStatement(sql1);
            statement2 = connection.prepareStatement(sql2);

            statement1.setInt(1, idUser);
            statement1.setInt(2, idFriend);

            statement2.setInt(1, idFriend);
            statement2.setInt(2, idUser);

            statement1.executeUpdate();
            statement2.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


}

