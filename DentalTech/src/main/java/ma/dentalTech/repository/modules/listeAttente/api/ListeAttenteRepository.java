package ma.dentalTech.repository.modules.listeAttente.api;

import ma.dentalTech.entities.listeDattente.ListeAttente;
import ma.dentalTech.repository.common.CrudRepository;

/**
 * Repository spécifique pour la gestion des listes d'attente.
 *
 * On hérite des méthodes CRUD de CrudRepository :
 *  - create(T entity)
 *  - findById(Long id)
 *  - findAll()
 *  - update(T entity)
 *  - deleteById(Long id)
 *  - delete(T entity)
 */
public interface ListeAttenteRepository extends CrudRepository<ListeAttente, Long> {

    /**
     * Recherche une liste d'attente par son nom.
     */
    ListeAttente findByNomListe(String nomListe);
}
