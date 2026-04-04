package controller;

import dao.AdherentDAO;
import dao.UtilisateurDAO;
import model.Adherent;
import model.Utilisateur;

import java.sql.SQLException;
import java.util.List;

/**
 * Contrôleur pour la gestion des adhérents et l'authentification.
 */
public class AdherentController {

    private final AdherentDAO     adherentDAO;
    private final UtilisateurDAO  utilisateurDAO;

    public AdherentController() throws SQLException {
        this.adherentDAO    = new AdherentDAO();
        this.utilisateurDAO = new UtilisateurDAO();
    }

    public List<Adherent> getTousAdherents() throws SQLException {
        return adherentDAO.trouverTous();
    }

    public Adherent getAdherentById(int id) throws SQLException {
        return adherentDAO.trouverParId(id);
    }

    public Adherent ajouterAdherent(Adherent a) throws SQLException {
        valider(a);
        return adherentDAO.ajouter(a);
    }

    public boolean modifierAdherent(Adherent a) throws SQLException {
        valider(a);
        return adherentDAO.modifier(a);
    }

    public boolean supprimerAdherent(int id) throws SQLException {
        return adherentDAO.supprimer(id);
    }

    /**
     * Authentifie un utilisateur (adhérent ou bibliothécaire).
     * @return l'utilisateur authentifié ou null
     */
    public Utilisateur authentifier(String login, String motDePasse) throws SQLException {
        return utilisateurDAO.authentifier(login, motDePasse);
    }

    /**
     * Retourne l'ID spécifique (id_adherent ou id_bibliothecaire) à partir de l'id utilisateur.
     */
    public int getIdSpecifique(int idUtilisateur, Utilisateur.Role role) throws SQLException {
        return utilisateurDAO.getIdSpecifique(idUtilisateur, role);
    }

    private void valider(Adherent a) {
        if (a.getNom() == null || a.getNom().trim().isEmpty())
            throw new IllegalArgumentException("Le nom est obligatoire.");
        if (a.getLogin() == null || a.getLogin().trim().isEmpty())
            throw new IllegalArgumentException("Le login est obligatoire.");
        if (a.getNumeroCarte() == null || a.getNumeroCarte().trim().isEmpty())
            throw new IllegalArgumentException("Le numéro de carte est obligatoire.");
    }
}
