package dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface générique DAO.
 * Définit les opérations CRUD communes à toutes les entités.
 *
 * @param <T>  type de l'entité
 * @param <ID> type de la clé primaire
 */
public interface IDao<T, ID> {

    /**
     * Insère une nouvelle entité en base.
     * @return l'entité avec son ID généré
     */
    T ajouter(T entite) throws SQLException;

    /**
     * Met à jour une entité existante.
     * @return true si la mise à jour a réussi
     */
    boolean modifier(T entite) throws SQLException;

    /**
     * Supprime une entité par son identifiant.
     * @return true si la suppression a réussi
     */
    boolean supprimer(ID id) throws SQLException;

    /**
     * Recherche une entité par son identifiant.
     * @return l'entité trouvée ou null
     */
    T trouverParId(ID id) throws SQLException;

    /**
     * Retourne la liste complète des entités.
     */
    List<T> trouverTous() throws SQLException;
}
