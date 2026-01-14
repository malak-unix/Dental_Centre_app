package ma.dentalTech.service.modules.referentiel.api;

import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.medicament.MedicamentDTO;
import java.util.List;

public interface ReferentielService {
    // --- ACTES ---
    ActeDTO createActe(ActeDTO dto);

    ActeDTO updateActe(ActeDTO dto);

    void deleteActe(Long id);

    List<ActeDTO> getAllActes();

    // --- MEDICAMENTS ---
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
