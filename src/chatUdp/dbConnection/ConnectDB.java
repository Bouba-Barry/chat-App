package chatUdp.dbConnection;
import java.sql.*;
public class ConnectDB {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/CHAT";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName(JDBC_DRIVER);
                connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            } catch (ClassNotFoundException e) {e.printStackTrace();}
            catch (SQLException e){e.printStackTrace();}
        }
        return connection;
    }

    public static void closeConnection() {
        try{
            if (connection != null) {
                connection.close();
                connection = null;
            }
        }catch (SQLException e){e.printStackTrace();}

    }
}

