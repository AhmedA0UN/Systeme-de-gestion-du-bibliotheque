package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton gérant la connexion MySQL.
 * Couche DAO - accès base de données.
 */
public class ConnexionDB {

    private static final String URL      = "jdbc:mysql://localhost:3306/bibliotheque?useSSL=false&serverTimezone=UTC";
    private static final String USER     = "root";
    private static final String PASSWORD = "";

    private static Connection instance = null;

    private ConnexionDB() {}

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                instance = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL introuvable : " + e.getMessage());
            }
        }
        return instance;
    }

    public static void closeConnection() {
        if (instance != null) {
            try {
                instance.close();
                instance = null;
            } catch (SQLException e) {
                System.err.println("Erreur fermeture connexion : " + e.getMessage());
            }
        }
    }
}
