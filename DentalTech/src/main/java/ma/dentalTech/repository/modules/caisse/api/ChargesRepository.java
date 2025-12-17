package ma.dentalTech.repository.modules.caisse.api;

import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChargesRepository extends CrudRepository<Charges, Long> {

    List<Charges> findByDateBetween(LocalDateTime start, LocalDateTime end);

    Double calculateTotalCharges(LocalDateTime start, LocalDateTime end);
}
