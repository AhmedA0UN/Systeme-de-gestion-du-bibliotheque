package dao;

import model.Adherent;
import model.Adherent.Statut;
import model.Utilisateur.Role;
import util.SingletonConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation DAO pour la table adherent (jointure avec utilisateur).
 */
public class AdherentDAO implements IDao<Adherent, Integer> {

    private Connection conn;

    public AdherentDAO() throws SQLException {
        this.conn = SingletonConnection.getInstance();
    }

    private Adherent mapper(ResultSet rs) throws SQLException {
        Adherent a = new Adherent();
        a.setId(rs.getInt("id_utilisateur"));
        a.setNom(rs.getString("nom"));
        a.setPrenom(rs.getString("prenom"));
        a.setEmail(rs.getString("email"));
        a.setTelephone(rs.getString("telephone"));
        a.setAdresse(rs.getString("adresse"));
        a.setLogin(rs.getString("login"));
        a.setMotDePasse(rs.getString("mot_de_passe"));
        a.setRole(Role.ADHERENT);
        a.setIdAdherent(rs.getInt("id_adherent"));
        a.setNumeroCarte(rs.getString("numero_carte"));
        a.setDateInscription(rs.getDate("date_inscription").toLocalDate());
        a.setDateExpiration(rs.getDate("date_expiration").toLocalDate());
        a.setStatut(Statut.valueOf(rs.getString("statut")));
        return a;
    }

    @Override
    public Adherent ajouter(Adherent a) throws SQLException {
        conn.setAutoCommit(false);
        try {
            // 1. Insérer dans utilisateur
            String sqlU = "INSERT INTO utilisateur (nom, prenom, email, telephone, adresse, login, mot_de_passe, role) " +
                          "VALUES (?, ?, ?, ?, ?, ?, SHA2(?,256), 'ADHERENT')";
            int idUtil;
            try (PreparedStatement ps = conn.prepareStatement(sqlU, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, a.getNom());
                ps.setString(2, a.getPrenom());
                ps.setString(3, a.getEmail());
                ps.setString(4, a.getTelephone());
                ps.setString(5, a.getAdresse());
                ps.setString(6, a.getLogin());
                ps.setString(7, a.getMotDePasse());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                idUtil = keys.getInt(1);
                a.setId(idUtil);
            }
            // 2. Insérer dans adherent
            String sqlA = "INSERT INTO adherent (id_utilisateur, numero_carte, date_inscription, date_expiration, statut) " +
                          "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlA, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt   (1, idUtil);
                ps.setString(2, a.getNumeroCarte());
                ps.setDate  (3, Date.valueOf(a.getDateInscription()));
                ps.setDate  (4, Date.valueOf(a.getDateExpiration()));
                ps.setString(5, a.getStatut().name());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                a.setIdAdherent(keys.getInt(1));
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
        return a;
    }

    @Override
    public boolean modifier(Adherent a) throws SQLException {
        conn.setAutoCommit(false);
        try {
            String sqlU = "UPDATE utilisateur SET nom=?, prenom=?, email=?, telephone=?, adresse=? " +
                          "WHERE id_utilisateur=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlU)) {
                ps.setString(1, a.getNom());
                ps.setString(2, a.getPrenom());
                ps.setString(3, a.getEmail());
                ps.setString(4, a.getTelephone());
                ps.setString(5, a.getAdresse());
                ps.setInt   (6, a.getId());
                ps.executeUpdate();
            }
            String sqlA = "UPDATE adherent SET numero_carte=?, date_inscription=?, date_expiration=?, statut=? " +
                          "WHERE id_adherent=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlA)) {
                ps.setString(1, a.getNumeroCarte());
                ps.setDate  (2, Date.valueOf(a.getDateInscription()));
                ps.setDate  (3, Date.valueOf(a.getDateExpiration()));
                ps.setString(4, a.getStatut().name());
                ps.setInt   (5, a.getIdAdherent());
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public boolean supprimer(Integer id) throws SQLException {
        // La suppression de utilisateur cascade sur adherent
        String sql = "DELETE FROM utilisateur WHERE id_utilisateur = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Adherent trouverParId(Integer id) throws SQLException {
        String sql = "SELECT u.*, a.* FROM utilisateur u " +
                     "JOIN adherent a ON u.id_utilisateur = a.id_utilisateur " +
                     "WHERE a.id_adherent = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapper(rs);
        }
        return null;
    }

    @Override
    public List<Adherent> trouverTous() throws SQLException {
        List<Adherent> liste = new ArrayList<>();
        String sql = "SELECT u.*, a.* FROM utilisateur u " +
                     "JOIN adherent a ON u.id_utilisateur = a.id_utilisateur " +
                     "ORDER BY u.nom, u.prenom";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) liste.add(mapper(rs));
        }
        return liste;
    }

    public Adherent trouverParLogin(String login) throws SQLException {
        String sql = "SELECT u.*, a.* FROM utilisateur u " +
                     "JOIN adherent a ON u.id_utilisateur = a.id_utilisateur " +
                     "WHERE u.login = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapper(rs);
        }
        return null;
    }
}
