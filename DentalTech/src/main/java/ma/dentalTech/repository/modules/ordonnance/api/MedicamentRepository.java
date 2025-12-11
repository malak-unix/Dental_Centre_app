package ma.dentalTech.repository.modules.ordonnance.api;

import ma.dentalTech.entities.medicament.Medicament;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

public interface MedicamentRepository extends CrudRepository<Medicament, Long> {

    /**
     * Recherche par mot-clé sur le nom du médicament (LIKE %keyword%).
     */
    List<Medicament> searchByNom(String keyword);

    /**
     * Médicaments remboursables ou non.
     */
    List<Medicament> findByRemboursable(boolean remboursable);

    /**
     * Nombre total de médicaments.
     */
    long count();

    /**
     * Pagination simple.
     */
    List<Medicament> findPage(int limit, int offset);
}
