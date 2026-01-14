package ma.dentalTech.mvc.controllers.modules.referentiel.api;

import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.medicament.MedicamentDTO;
import java.util.List;

public interface ReferentielController {
    // Actes
    ActeDTO createActe(ActeDTO dto);

    ActeDTO updateActe(ActeDTO dto);

    void deleteActe(Long id);

    List<ActeDTO> getAllActes();

    // Medicaments
    MedicamentDTO createMedicament(MedicamentDTO dto);

    MedicamentDTO updateMedicament(MedicamentDTO dto);

    void deleteMedicament(Long id);

    List<MedicamentDTO> getAllMedicaments();

    // --- RETOUR REF ---
    List<ma.dentalTech.entities.enums.Assurance> getAllAssurances();

    // --- CATALOGUE ANTECEDENTS ---
    ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO createRefAntecedent(
            ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO dto);

    void deleteRefAntecedent(Long id);

    List<ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO> getAllRefAntecedents();
}
