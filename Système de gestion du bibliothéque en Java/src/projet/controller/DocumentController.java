package controller;

import dao.DocumentDAO;
import model.Document;

import java.sql.SQLException;
import java.util.List;

/**
 * Contrôleur pour la gestion des documents.
 * Fait le lien entre la Vue et le DAO (couche Modèle).
 */
public class DocumentController {

    private final DocumentDAO dao;

    public DocumentController() throws SQLException {
        this.dao = new DocumentDAO();
    }

    public List<Document> getTousDocuments() throws SQLException {
        return dao.trouverTous();
    }

    public List<Document> getDocumentsDisponibles() throws SQLException {
        return dao.trouverDisponibles();
    }

    public List<Document> rechercherParTitre(String titre) throws SQLException {
        return dao.rechercherParTitre(titre);
    }

    public List<Document> rechercherParAuteur(String auteur) throws SQLException {
        return dao.rechercherParAuteur(auteur);
    }

    public Document getDocumentById(int id) throws SQLException {
        return dao.trouverParId(id);
    }

    public Document ajouterDocument(Document doc) throws SQLException {
        valider(doc);
        return dao.ajouter(doc);
    }

    public boolean modifierDocument(Document doc) throws SQLException {
        valider(doc);
        return dao.modifier(doc);
    }

    public boolean supprimerDocument(int id) throws SQLException {
        return dao.supprimer(id);
    }

    private void valider(Document doc) {
        if (doc.getTitre() == null || doc.getTitre().trim().isEmpty())
            throw new IllegalArgumentException("Le titre est obligatoire.");
        if (doc.getNombreExemplaires() < 1)
            throw new IllegalArgumentException("Le nombre d'exemplaires doit être ≥ 1.");
        if (doc.getDisponibles() > doc.getNombreExemplaires())
            throw new IllegalArgumentException("Les exemplaires disponibles ne peuvent pas dépasser le total.");
    }
}
