package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.enums.FormeMedicament;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MedicamentRepository extends CrudRepository<Medicament, Long> {

    List<Medicament> searchByNom(String keyword);

    Optional<Medicament> findByNomExact(String nom);

    List<Medicament> findByRemboursable(boolean remboursable);

    List<Medicament> findByLaboratoire(String laboratoire);

    List<Medicament> findByType(String typeMedicament);

    List<Medicament> findByForme(FormeMedicament forme);

    List<Medicament> findByPrixBetween(Double min, Double max);

    boolean existsById(Long id);

    long count();

    List<Medicament> findPage(int limit, int offset);
}
