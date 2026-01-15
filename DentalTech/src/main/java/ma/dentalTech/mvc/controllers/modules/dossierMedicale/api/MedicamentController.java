package ma.dentalTech.mvc.controllers.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Medicament;

import java.util.List;

public interface MedicamentController {

    List<Medicament> getAll();

    List<Medicament> searchByNom(String keyword);

    Medicament getById(Long id);

    Medicament create(Medicament m);

    Medicament update(Medicament m);

    void deleteById(Long id);
}
