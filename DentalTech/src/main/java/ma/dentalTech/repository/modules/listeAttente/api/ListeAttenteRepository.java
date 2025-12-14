package ma.dentalTech.repository.modules.listeAttente.api;

import ma.dentalTech.entities.listeDattente.ListeAttente;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

public interface ListeAttenteRepository extends CrudRepository<ListeAttente, Long> {

    List<ListeAttente> findByNomListe(String nomListe);
}
