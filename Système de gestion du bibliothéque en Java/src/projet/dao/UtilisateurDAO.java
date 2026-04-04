package dao;

import model.Utilisateur;
import model.Utilisateur.Role;

import java.sql.*;

/**
 * DAO dédié à l'authentification.
 */
public class UtilisateurDAO {

    private Connection conn;

    public UtilisateurDAO() throws SQLException {
        this.conn = ConnexionDB.getConnection();
    }

    /**
     * Authentifie un utilisateur par login/mot de passe.
     * Le mot de passe est comparé avec SHA2-256.
     * @return l'utilisateur si authentifié, null sinon
     */
    public Utilisateur authentifier(String login, String motDePasse) throws SQLException {
        String sql = "SELECT * FROM utilisateur " +
                     "WHERE login = ? AND mot_de_passe = SHA2(?, 256)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, motDePasse);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Utilisateur u = new Utilisateur();
                u.setId(rs.getInt("id_utilisateur"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                u.setEmail(rs.getString("email"));
                u.setTelephone(rs.getString("telephone"));
                u.setAdresse(rs.getString("adresse"));
                u.setLogin(rs.getString("login"));
                u.setRole(Role.valueOf(rs.getString("role")));
                return u;
            }
        }
        return null;
    }

    /**
     * Récupère l'ID de l'adhérent ou du bibliothécaire à partir de l'id utilisateur.
     */
    public int getIdSpecifique(int idUtilisateur, Role role) throws SQLException {
        String table  = (role == Role.ADHERENT) ? "adherent" : "bibliothecaire";
        String colPk  = (role == Role.ADHERENT) ? "id_adherent" : "id_bibliothecaire";
        String sql    = "SELECT " + colPk + " FROM " + table + " WHERE id_utilisateur = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }
}
