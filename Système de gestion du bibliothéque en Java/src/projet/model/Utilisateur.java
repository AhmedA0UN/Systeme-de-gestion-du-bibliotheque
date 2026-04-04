package model;

/**
 * Modèle représentant un utilisateur du système.
 * Couche Modèle (MVC).
 */
public class Utilisateur {

    public enum Role { ADHERENT, BIBLIOTHECAIRE }

    private int    id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private String login;
    private String motDePasse;
    private Role   role;

    public Utilisateur() {}

    public Utilisateur(int id, String nom, String prenom, String email,
                       String telephone, String adresse,
                       String login, String motDePasse, Role role) {
        this.id         = id;
        this.nom        = nom;
        this.prenom     = prenom;
        this.email      = email;
        this.telephone  = telephone;
        this.adresse    = adresse;
        this.login      = login;
        this.motDePasse = motDePasse;
        this.role       = role;
    }

    // Getters / Setters
    public int    getId()          { return id; }
    public void   setId(int id)    { this.id = id; }

    public String getNom()               { return nom; }
    public void   setNom(String nom)     { this.nom = nom; }

    public String getPrenom()                { return prenom; }
    public void   setPrenom(String prenom)   { this.prenom = prenom; }

    public String getEmail()                 { return email; }
    public void   setEmail(String email)     { this.email = email; }

    public String getTelephone()                     { return telephone; }
    public void   setTelephone(String telephone)     { this.telephone = telephone; }

    public String getAdresse()                   { return adresse; }
    public void   setAdresse(String adresse)     { this.adresse = adresse; }

    public String getLogin()                 { return login; }
    public void   setLogin(String login)     { this.login = login; }

    public String getMotDePasse()                        { return motDePasse; }
    public void   setMotDePasse(String motDePasse)       { this.motDePasse = motDePasse; }

    public Role   getRole()                { return role; }
    public void   setRole(Role role)       { this.role = role; }

    public String getNomComplet() { return prenom + " " + nom; }

    @Override
    public String toString() {
        return "[" + role + "] " + getNomComplet() + " (" + login + ")";
    }
}
