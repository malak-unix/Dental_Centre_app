package ma.dentalTech.repository.modules.plageHoraire.api;

import ma.dentalTech.entities.plageHoraire.PlageHoraire;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

public interface PlageHoraireRepository extends CrudRepository<PlageHoraire, Long> {

    /**
     * Toutes les plages d'une journée.
     */
    List<PlageHoraire> findByDetailJourneeId(Long detailJourneeId);

    /**
     * Plages disponibles (disponible = true) pour une journée.
     */
    List<PlageHoraire> findDisponiblesByDetailJournee(Long detailJourneeId);
}
