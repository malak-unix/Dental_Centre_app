package ma.dentalTech.repository.modules.ordonnance.api;

import ma.dentalTech.entities.ordonnance.Ordonnance;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrdonnanceRepository extends CrudRepository<Ordonnance, Long> {

    /**
     * Toutes les ordonnances d'une date donnée.
     */
    List<Ordonnance> findByDate(LocalDate date);

    /**
     * Ordonnances entre deux dates (incluses).
     */
    List<Ordonnance> findByDateBetween(LocalDate start, LocalDate end);

    /**
     * Nombre total d’ordonnances.
     */
    long count();

    /**
     * Pagination simple.
     */
    List<Ordonnance> findPage(int limit, int offset);
}
