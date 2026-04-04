package controller;

import dao.AdherentDAO;
import dao.PretDAO;
import model.Adherent;
import model.Pret;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Contrôleur pour la gestion des prêts et retours.
 */
public class PretController {

    private static final int DUREE_PRET_JOURS = 14;

    private final PretDAO      pretDAO;
    private final AdherentDAO  adherentDAO;

    public PretController() throws SQLException {
        this.pretDAO     = new PretDAO();
        this.adherentDAO = new AdherentDAO();
    }

    public List<Pret> getTousPrets() throws SQLException {
        return pretDAO.trouverTous();
    }

    public List<Pret> getPretsEnCours() throws SQLException {
        return pretDAO.trouverEnCours();
    }

    public List<Pret> getPretsAdherent(int idAdherent) throws SQLException {
        return pretDAO.trouverParAdherent(idAdherent);
    }

    /**
     * Crée un nouveau prêt pour un adhérent.
     * Vérifie que l'adhérent est actif et que le document est disponible.
     */
    public Pret emprunterDocument(int idAdherent, int idDocument, int idBibliothecaire) throws SQLException {
        // Vérifier statut adhérent
        Adherent adherent = adherentDAO.trouverParId(idAdherent);
        if (adherent == null)
            throw new IllegalArgumentException("Adhérent introuvable.");
        if (!adherent.isActif())
            throw new IllegalStateException("L'adhérent n'est pas actif ou sa carte est expirée.");

        LocalDate debut  = LocalDate.now();
        LocalDate fin    = debut.plusDays(DUREE_PRET_JOURS);
        Pret pret = new Pret(idAdherent, idDocument, debut, fin, idBibliothecaire);
        return pretDAO.ajouter(pret);
    }

    /**
     * Enregistre le retour d'un document emprunté.
     */
    public boolean enregistrerRetour(int idPret, String remarque) throws SQLException {
        return pretDAO.enregistrerRetour(idPret, remarque);
    }

    public boolean modifierPret(Pret pret) throws SQLException {
        return pretDAO.modifier(pret);
    }

    public boolean supprimerPret(int id) throws SQLException {
        return pretDAO.supprimer(id);
    }
}
