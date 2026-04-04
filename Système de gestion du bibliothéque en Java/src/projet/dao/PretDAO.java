package dao;

import model.Pret;
import model.Pret.Statut;
import util.ConnexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation DAO pour la table pret.
 */
public class PretDAO implements IDao<Pret, Integer> {

    private Connection conn;

    public PretDAO() throws SQLException {
        this.conn = ConnexionDB.getConnection();
    }

    private Pret mapper(ResultSet rs) throws SQLException {
        Pret p = new Pret();
        p.setId(rs.getInt("id_pret"));
        p.setIdAdherent(rs.getInt("id_adherent"));
        p.setIdDocument(rs.getInt("id_document"));
        p.setDateEmprunt(rs.getDate("date_emprunt").toLocalDate());
        p.setDateRetourPrevue(rs.getDate("date_retour_prevue").toLocalDate());
        Date retourEff = rs.getDate("date_retour_effective");
        if (retourEff != null) p.setDateRetourEffective(retourEff.toLocalDate());
        p.setStatut(Statut.valueOf(rs.getString("statut")));
        p.setIdBibliothecaire(rs.getInt("id_bibliothecaire"));
        p.setRemarque(rs.getString("remarque"));
        // Champs de jointure (optionnels selon la requête)
        try { p.setTitreDocument(rs.getString("titre")); } catch (SQLException ignored) {}
        try { p.setNomAdherent(rs.getString("nom_adherent")); } catch (SQLException ignored) {}
        return p;
    }

    @Override
    public Pret ajouter(Pret pret) throws SQLException {
        String sql = "INSERT INTO pret (id_adherent, id_document, date_emprunt, date_retour_prevue, statut, id_bibliothecaire) " +
                     "VALUES (?, ?, ?, ?, 'EN_COURS', ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt (1, pret.getIdAdherent());
            ps.setInt (2, pret.getIdDocument());
            ps.setDate(3, Date.valueOf(pret.getDateEmprunt()));
            ps.setDate(4, Date.valueOf(pret.getDateRetourPrevue()));
            ps.setInt (5, pret.getIdBibliothecaire());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) pret.setId(keys.getInt(1));
        }
        return pret;
    }

    @Override
    public boolean modifier(Pret pret) throws SQLException {
        String sql = "UPDATE pret SET statut=?, date_retour_effective=?, remarque=? WHERE id_pret=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pret.getStatut().name());
            ps.setDate  (2, pret.getDateRetourEffective() != null ?
                            Date.valueOf(pret.getDateRetourEffective()) : null);
            ps.setString(3, pret.getRemarque());
            ps.setInt   (4, pret.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean supprimer(Integer id) throws SQLException {
        String sql = "DELETE FROM pret WHERE id_pret = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Pret trouverParId(Integer id) throws SQLException {
        String sql = "SELECT p.*, d.titre, CONCAT(u.prenom,' ',u.nom) AS nom_adherent " +
                     "FROM pret p " +
                     "JOIN document d ON p.id_document = d.id_document " +
                     "JOIN adherent a ON p.id_adherent = a.id_adherent " +
                     "JOIN utilisateur u ON a.id_utilisateur = u.id_utilisateur " +
                     "WHERE p.id_pret = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapper(rs);
        }
        return null;
    }

    @Override
    public List<Pret> trouverTous() throws SQLException {
        List<Pret> liste = new ArrayList<>();
        String sql = "SELECT p.*, d.titre, CONCAT(u.prenom,' ',u.nom) AS nom_adherent " +
                     "FROM pret p " +
                     "JOIN document d ON p.id_document = d.id_document " +
                     "JOIN adherent a ON p.id_adherent = a.id_adherent " +
                     "JOIN utilisateur u ON a.id_utilisateur = u.id_utilisateur " +
                     "ORDER BY p.date_emprunt DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) liste.add(mapper(rs));
        }
        return liste;
    }

    public List<Pret> trouverEnCours() throws SQLException {
        List<Pret> liste = new ArrayList<>();
        String sql = "SELECT p.*, d.titre, CONCAT(u.prenom,' ',u.nom) AS nom_adherent " +
                     "FROM pret p " +
                     "JOIN document d ON p.id_document = d.id_document " +
                     "JOIN adherent a ON p.id_adherent = a.id_adherent " +
                     "JOIN utilisateur u ON a.id_utilisateur = u.id_utilisateur " +
                     "WHERE p.statut = 'EN_COURS' OR p.statut = 'EN_RETARD' " +
                     "ORDER BY p.date_retour_prevue";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) liste.add(mapper(rs));
        }
        return liste;
    }

    public List<Pret> trouverParAdherent(int idAdherent) throws SQLException {
        List<Pret> liste = new ArrayList<>();
        String sql = "SELECT p.*, d.titre, CONCAT(u.prenom,' ',u.nom) AS nom_adherent " +
                     "FROM pret p " +
                     "JOIN document d ON p.id_document = d.id_document " +
                     "JOIN adherent a ON p.id_adherent = a.id_adherent " +
                     "JOIN utilisateur u ON a.id_utilisateur = u.id_utilisateur " +
                     "WHERE p.id_adherent = ? ORDER BY p.date_emprunt DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAdherent);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapper(rs));
        }
        return liste;
    }

    /**
     * Enregistre le retour d'un document.
     */
    public boolean enregistrerRetour(int idPret, String remarque) throws SQLException {
        String sql = "UPDATE pret SET statut='RENDU', date_retour_effective=CURDATE(), remarque=? " +
                     "WHERE id_pret=? AND statut != 'RENDU'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, remarque);
            ps.setInt   (2, idPret);
            return ps.executeUpdate() > 0;
        }
    }
}
