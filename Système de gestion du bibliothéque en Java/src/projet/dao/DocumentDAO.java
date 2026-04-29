package dao;

import model.Document;
import model.Document.TypeDocument;
import util.SingletonConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation DAO pour la table document.
 * Gère toutes les opérations CRUD sur les documents.
 */
public class DocumentDAO implements IDao<Document, Integer> {

    private Connection conn;

    public DocumentDAO() throws SQLException {
        this.conn = SingletonConnection.getInstance();
    }

    // ------------------------------------------------------------------
    // Mapper ResultSet -> Document
    // ------------------------------------------------------------------
    private Document mapper(ResultSet rs) throws SQLException {
        Document d = new Document();
        d.setId(rs.getInt("id_document"));
        d.setTitre(rs.getString("titre"));
        d.setAuteur(rs.getString("auteur"));
        d.setEditeur(rs.getString("editeur"));
        d.setAnneeEdition(rs.getInt("annee_edition"));
        d.setIsbn(rs.getString("isbn"));
        d.setCategorie(rs.getString("categorie"));
        d.setTypeDocument(TypeDocument.valueOf(rs.getString("type_document")));
        d.setNombreExemplaires(rs.getInt("nombre_exemplaires"));
        d.setDisponibles(rs.getInt("disponibles"));
        d.setDescription(rs.getString("description"));
        return d;
    }

    // ------------------------------------------------------------------
    // CRUD
    // ------------------------------------------------------------------
    @Override
    public Document ajouter(Document doc) throws SQLException {
        String sql = "INSERT INTO document (titre, auteur, editeur, annee_edition, isbn, " +
                     "categorie, type_document, nombre_exemplaires, disponibles, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, doc.getTitre());
            ps.setString(2, doc.getAuteur());
            ps.setString(3, doc.getEditeur());
            ps.setInt   (4, doc.getAnneeEdition());
            ps.setString(5, doc.getIsbn());
            ps.setString(6, doc.getCategorie());
            ps.setString(7, doc.getTypeDocument().name());
            ps.setInt   (8, doc.getNombreExemplaires());
            ps.setInt   (9, doc.getDisponibles());
            ps.setString(10, doc.getDescription());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) doc.setId(keys.getInt(1));
        }
        return doc;
    }

    @Override
    public boolean modifier(Document doc) throws SQLException {
        String sql = "UPDATE document SET titre=?, auteur=?, editeur=?, annee_edition=?, isbn=?, " +
                     "categorie=?, type_document=?, nombre_exemplaires=?, disponibles=?, description=? " +
                     "WHERE id_document=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,  doc.getTitre());
            ps.setString(2,  doc.getAuteur());
            ps.setString(3,  doc.getEditeur());
            ps.setInt   (4,  doc.getAnneeEdition());
            ps.setString(5,  doc.getIsbn());
            ps.setString(6,  doc.getCategorie());
            ps.setString(7,  doc.getTypeDocument().name());
            ps.setInt   (8,  doc.getNombreExemplaires());
            ps.setInt   (9,  doc.getDisponibles());
            ps.setString(10, doc.getDescription());
            ps.setInt   (11, doc.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean supprimer(Integer id) throws SQLException {
        String sql = "DELETE FROM document WHERE id_document = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Document trouverParId(Integer id) throws SQLException {
        String sql = "SELECT * FROM document WHERE id_document = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapper(rs);
        }
        return null;
    }

    @Override
    public List<Document> trouverTous() throws SQLException {
        List<Document> liste = new ArrayList<>();
        String sql = "SELECT * FROM document ORDER BY titre";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) liste.add(mapper(rs));
        }
        return liste;
    }

    // ------------------------------------------------------------------
    // Recherches spécifiques
    // ------------------------------------------------------------------
    public List<Document> rechercherParTitre(String titre) throws SQLException {
        List<Document> liste = new ArrayList<>();
        String sql = "SELECT * FROM document WHERE titre LIKE ? ORDER BY titre";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + titre + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapper(rs));
        }
        return liste;
    }

    public List<Document> rechercherParAuteur(String auteur) throws SQLException {
        List<Document> liste = new ArrayList<>();
        String sql = "SELECT * FROM document WHERE auteur LIKE ? ORDER BY auteur";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + auteur + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapper(rs));
        }
        return liste;
    }

    public List<Document> trouverDisponibles() throws SQLException {
        List<Document> liste = new ArrayList<>();
        String sql = "SELECT * FROM document WHERE disponibles > 0 ORDER BY titre";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) liste.add(mapper(rs));
        }
        return liste;
    }
}
