package model;

import java.time.LocalDate;

/**
 * Modèle représentant un adhérent.
 * Spécialisation de Utilisateur.
 */
public class Adherent extends Utilisateur {

    public enum Statut { ACTIF, SUSPENDU, EXPIRE }

    private int        idAdherent;
    private String     numeroCarte;
    private LocalDate  dateInscription;
    private LocalDate  dateExpiration;
    private Statut     statut;

    public Adherent() {
        super();
        setRole(Role.ADHERENT);
    }

    public Adherent(int id, String nom, String prenom, String email,
                    String telephone, String adresse,
                    String login, String motDePasse,
                    int idAdherent, String numeroCarte,
                    LocalDate dateInscription, LocalDate dateExpiration,
                    Statut statut) {
        super(id, nom, prenom, email, telephone, adresse, login, motDePasse, Role.ADHERENT);
        this.idAdherent      = idAdherent;
        this.numeroCarte     = numeroCarte;
        this.dateInscription = dateInscription;
        this.dateExpiration  = dateExpiration;
        this.statut          = statut;
    }

    public int        getIdAdherent()                         { return idAdherent; }
    public void       setIdAdherent(int idAdherent)           { this.idAdherent = idAdherent; }

    public String     getNumeroCarte()                        { return numeroCarte; }
    public void       setNumeroCarte(String numeroCarte)      { this.numeroCarte = numeroCarte; }

    public LocalDate  getDateInscription()                              { return dateInscription; }
    public void       setDateInscription(LocalDate dateInscription)     { this.dateInscription = dateInscription; }

    public LocalDate  getDateExpiration()                               { return dateExpiration; }
    public void       setDateExpiration(LocalDate dateExpiration)       { this.dateExpiration = dateExpiration; }

    public Statut     getStatut()                             { return statut; }
    public void       setStatut(Statut statut)                { this.statut = statut; }

    public boolean    isActif() { return statut == Statut.ACTIF && !dateExpiration.isBefore(LocalDate.now()); }
}
