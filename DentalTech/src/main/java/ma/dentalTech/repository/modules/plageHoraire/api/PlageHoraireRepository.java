package ma.dentalTech.repository.modules.plageHoraire.api;

import ma.dentalTech.entities.plageHoraire.PlageHoraire;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

public interface PlageHoraireRepository extends CrudRepository<PlageHoraire, Long> {

    List<PlageHoraire> findByDetailJourneeId(Long detailJourneeId);

    List<PlageHoraire> findDisponiblesByDetailJournee(Long detailJourneeId);
}
