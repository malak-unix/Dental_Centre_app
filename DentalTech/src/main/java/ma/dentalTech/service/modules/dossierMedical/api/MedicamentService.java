package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.enums.FormeMedicament;

import java.util.List;
import java.util.Optional;

public interface MedicamentService {

    // CRUD
    List<Medicament> getAll();
    Medicament getById(Long id);
    void create(Medicament m);
    void update(Medicament m);
    void delete(Medicament m);
    void deleteById(Long id);

    // Spécifiques (repo)
    List<Medicament> searchByNom(String keyword);
    Optional<Medicament> getByNomExact(String nom);
    List<Medicament> getByRemboursable(boolean remboursable);
    List<Medicament> getByLaboratoire(String laboratoire);
    List<Medicament> getByType(String typeMedicament);
    List<Medicament> getByForme(FormeMedicament forme);
    List<Medicament> getByPrixBetween(Double min, Double max);

    boolean existsById(Long id);
    long count();
    List<Medicament> findPage(int limit, int offset);
}
