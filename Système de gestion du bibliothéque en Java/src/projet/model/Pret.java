package model;

import java.time.LocalDate;

/**
 * Modèle représentant un prêt.
 */
public class Pret {

    public enum Statut { EN_COURS, RENDU, EN_RETARD }

    private int       id;
    private int       idAdherent;
    private int       idDocument;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;
    private Statut    statut;
    private int       idBibliothecaire;
    private String    remarque;

    // Champs de jointure (affichage)
    private String    titreDocument;
    private String    nomAdherent;

    public Pret() {}

    public Pret(int idAdherent, int idDocument, LocalDate dateEmprunt,
                LocalDate dateRetourPrevue, int idBibliothecaire) {
        this.idAdherent       = idAdherent;
        this.idDocument       = idDocument;
        this.dateEmprunt      = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.idBibliothecaire = idBibliothecaire;
        this.statut           = Statut.EN_COURS;
    }

    // Getters / Setters
    public int       getId()                       { return id; }
    public void      setId(int id)                 { this.id = id; }

    public int       getIdAdherent()               { return idAdherent; }
    public void      setIdAdherent(int v)          { this.idAdherent = v; }

    public int       getIdDocument()               { return idDocument; }
    public void      setIdDocument(int v)          { this.idDocument = v; }

    public LocalDate getDateEmprunt()              { return dateEmprunt; }
    public void      setDateEmprunt(LocalDate v)   { this.dateEmprunt = v; }

    public LocalDate getDateRetourPrevue()             { return dateRetourPrevue; }
    public void      setDateRetourPrevue(LocalDate v)  { this.dateRetourPrevue = v; }

    public LocalDate getDateRetourEffective()                  { return dateRetourEffective; }
    public void      setDateRetourEffective(LocalDate v)       { this.dateRetourEffective = v; }

    public Statut    getStatut()                   { return statut; }
    public void      setStatut(Statut statut)      { this.statut = statut; }

    public int       getIdBibliothecaire()         { return idBibliothecaire; }
    public void      setIdBibliothecaire(int v)    { this.idBibliothecaire = v; }

    public String    getRemarque()                 { return remarque; }
    public void      setRemarque(String remarque)  { this.remarque = remarque; }

    public String    getTitreDocument()                    { return titreDocument; }
    public void      setTitreDocument(String titreDocument){ this.titreDocument = titreDocument; }

    public String    getNomAdherent()                   { return nomAdherent; }
    public void      setNomAdherent(String nomAdherent) { this.nomAdherent = nomAdherent; }

    public boolean   isEnRetard() {
        return statut == Statut.EN_COURS && LocalDate.now().isAfter(dateRetourPrevue);
    }
}
