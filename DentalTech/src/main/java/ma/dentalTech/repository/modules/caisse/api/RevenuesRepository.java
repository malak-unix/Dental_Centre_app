package ma.dentalTech.repository.modules.caisse.api;

import ma.dentalTech.entities.revenues.Revenues;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RevenuesRepository extends CrudRepository<Revenues, Long> {

    List<Revenues> findByDateBetween(LocalDateTime start, LocalDateTime end);
    Double calculateTotalOtherRevenue(LocalDateTime start, LocalDateTime end);
}