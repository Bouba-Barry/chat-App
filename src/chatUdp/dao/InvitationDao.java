package chatUdp.dao;

import chatUdp.dbConnection.ConnectDB;
import chatUdp.entity.Invitation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class InvitationDao {

    protected static Connection connection = ConnectDB.getConnection();

    public static void addInvite(Invitation inv) {
        String sql = "INSERT INTO Invitation (sender_id, receiver_id ,etat) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, inv.getSender_id());
            statement.setInt(2, inv.getReceiver_id());
            statement.setString(3, inv.getEtat());
            statement.executeUpdate();
            // Get the generated primary key
        }
         catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
        public static Invitation getByID(int id){
        String sql = "SELECT * FROM Invitation WHERE id = ?";
        Invitation invitation =  new Invitation();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                invitation.setId(resultSet.getInt("id"));
                invitation.setSender_id(resultSet.getInt("sender_id"));
                invitation.setReceiver_id(resultSet.getInt("receiver_id"));
                invitation.setEtat(resultSet.getString("etat"));
            }
        }catch (SQLException e){
            e.printStackTrace();
            // ConnectDB.closeConnection();
        }
        return invitation;
    }
    public static List<Invitation> getAllInvitesNotSended(int id){
        List<Invitation> demand = new ArrayList<>();
        String sql = "SELECT * FROM Invitation WHERE receiver_id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
          //  statement.setString(1,"ACCEPTÉ");
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                int idd = resultSet.getInt("id");
                int sender_id = resultSet.getInt("sender_id");
                int receiver_id = resultSet.getInt("receiver_id");
                String etat = resultSet.getString("etat");
                demand.add(new Invitation(idd,sender_id,receiver_id,etat));
            }
        }catch (SQLException e){e.printStackTrace();}
        return demand;
    }

    public static List<Invitation> getAllSenderInvite(int id){
        List<Invitation> demand = new ArrayList<>();
        String sql = "SELECT * FROM Invitation WHERE etat <> ? AND sender_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(2, id);
            statement.setString(1,"ACCEPTÉ");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                int sender_id = resultSet.getInt("sender_id");
                int receiver_id = resultSet.getInt("receiver_id");
                String etat = resultSet.getString("etat");
                demand.add(new Invitation(sender_id,receiver_id,etat));
            }
        }catch (SQLException e){
            e.printStackTrace();
            // ConnectDB.closeConnection();
        }
        return demand;
    }

    public static void delete(int id){
        String sql = "DELETE FROM Invitation WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        }catch (SQLException e){e.printStackTrace();}
    }

}
