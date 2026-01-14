package ma.dentalTech.mvc.controllers.modules.referentiel.impl;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.mvc.controllers.modules.referentiel.api.ReferentielController;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.medicament.MedicamentDTO;
import ma.dentalTech.service.modules.referentiel.api.ReferentielService;
import java.util.List;

@RequiredArgsConstructor
public class ReferentielControllerImpl implements ReferentielController {

    private final ReferentielService referentielService;

    @Override
    public ActeDTO createActe(ActeDTO dto) {
        if (dto == null)
            throw new IllegalArgumentException("DTO cannot be null");
        return referentielService.createActe(dto);
    }

    @Override
    public ActeDTO updateActe(ActeDTO dto) {
        if (dto == null)
            throw new IllegalArgumentException("DTO cannot be null");
        return referentielService.updateActe(dto);
    }

    @Override
    public void deleteActe(Long id) {
        if (id == null)
            throw new IllegalArgumentException("ID cannot be null");
        referentielService.deleteActe(id);
    }

    @Override
    public List<ActeDTO> getAllActes() {
        return referentielService.getAllActes();
    }

    @Override
    public MedicamentDTO createMedicament(MedicamentDTO dto) {
        if (dto == null)
            throw new IllegalArgumentException("DTO cannot be null");
        return referentielService.createMedicament(dto);
    }

    @Override
    public MedicamentDTO updateMedicament(MedicamentDTO dto) {
        if (dto == null)
            throw new IllegalArgumentException("DTO cannot be null");
        return referentielService.updateMedicament(dto);
    }

    @Override
    public void deleteMedicament(Long id) {
        if (id == null)
            throw new IllegalArgumentException("ID cannot be null");
        referentielService.deleteMedicament(id);
    }

    @Override
    public List<MedicamentDTO> getAllMedicaments() {
        return referentielService.getAllMedicaments();
    }

    @Override
    public List<ma.dentalTech.entities.enums.Assurance> getAllAssurances() {
        return referentielService.getAllAssurances();
    }

    @Override
    public ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO createRefAntecedent(
            ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO dto) {
        if (dto == null)
            throw new IllegalArgumentException("DTO cannot be null");
        return referentielService.createRefAntecedent(dto);
    }

    @Override
    public void deleteRefAntecedent(Long id) {
        if (id == null)
            throw new IllegalArgumentException("ID cannot be null");
        referentielService.deleteRefAntecedent(id);
    }

    @Override
    public List<ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO> getAllRefAntecedents() {
        return referentielService.getAllRefAntecedents();
    }
}
