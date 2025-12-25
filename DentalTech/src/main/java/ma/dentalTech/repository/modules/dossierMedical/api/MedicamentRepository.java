package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

public interface MedicamentRepository extends CrudRepository<Medicament, Long> {

    List<Medicament> searchByNom(String keyword);

    List<Medicament> findByRemboursable(boolean remboursable);

    long count();

    List<Medicament> findPage(int limit, int offset);
}
