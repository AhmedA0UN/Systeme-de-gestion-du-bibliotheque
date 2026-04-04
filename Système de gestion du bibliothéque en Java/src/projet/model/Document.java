package model;

import java.time.LocalDateTime;

/**
 * Modèle représentant un document de la bibliothèque.
 * Couche Modèle (MVC).
 */
public class Document {

    public enum TypeDocument { LIVRE, REVUE, THESE, MEMOIRE, AUTRE }

    private int           id;
    private String        titre;
    private String        auteur;
    private String        editeur;
    private int           anneeEdition;
    private String        isbn;
    private String        categorie;
    private TypeDocument  typeDocument;
    private int           nombreExemplaires;
    private int           disponibles;
    private String        description;
    private LocalDateTime dateAjout;

    public Document() {}

    public Document(int id, String titre, String auteur, String editeur,
                    int anneeEdition, String isbn, String categorie,
                    TypeDocument typeDocument, int nombreExemplaires, int disponibles) {
        this.id                = id;
        this.titre             = titre;
        this.auteur            = auteur;
        this.editeur           = editeur;
        this.anneeEdition      = anneeEdition;
        this.isbn              = isbn;
        this.categorie         = categorie;
        this.typeDocument      = typeDocument;
        this.nombreExemplaires = nombreExemplaires;
        this.disponibles       = disponibles;
    }

    // Getters / Setters
    public int           getId()                              { return id; }
    public void          setId(int id)                        { this.id = id; }

    public String        getTitre()                           { return titre; }
    public void          setTitre(String titre)               { this.titre = titre; }

    public String        getAuteur()                          { return auteur; }
    public void          setAuteur(String auteur)             { this.auteur = auteur; }

    public String        getEditeur()                         { return editeur; }
    public void          setEditeur(String editeur)           { this.editeur = editeur; }

    public int           getAnneeEdition()                    { return anneeEdition; }
    public void          setAnneeEdition(int anneeEdition)    { this.anneeEdition = anneeEdition; }

    public String        getIsbn()                            { return isbn; }
    public void          setIsbn(String isbn)                 { this.isbn = isbn; }

    public String        getCategorie()                       { return categorie; }
    public void          setCategorie(String categorie)       { this.categorie = categorie; }

    public TypeDocument  getTypeDocument()                          { return typeDocument; }
    public void          setTypeDocument(TypeDocument typeDocument) { this.typeDocument = typeDocument; }

    public int           getNombreExemplaires()                           { return nombreExemplaires; }
    public void          setNombreExemplaires(int nombreExemplaires)      { this.nombreExemplaires = nombreExemplaires; }

    public int           getDisponibles()                     { return disponibles; }
    public void          setDisponibles(int disponibles)      { this.disponibles = disponibles; }

    public String        getDescription()                     { return description; }
    public void          setDescription(String description)   { this.description = description; }

    public LocalDateTime getDateAjout()                               { return dateAjout; }
    public void          setDateAjout(LocalDateTime dateAjout)        { this.dateAjout = dateAjout; }

    public boolean       isDisponible()                       { return disponibles > 0; }

    @Override
    public String toString() {
        return titre + " — " + auteur + " (" + anneeEdition + ")";
    }
}
