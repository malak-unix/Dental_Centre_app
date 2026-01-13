package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.mvc.dto.dossierMedicale.common.*;
import ma.dentalTech.mvc.dto.dossierMedicale.medicament.MedicamentDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.medicament.SaveMedicamentRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.medicament.SearchMedicamentsRequestDTO;

public interface MedicamentService {
    MedicamentDTO getById(IdRequestDTO in);
    ListResponseDTO<MedicamentDTO> findAll(EmptyRequestDTO in);
    ListResponseDTO<MedicamentDTO> search(SearchMedicamentsRequestDTO in);

    LongResponseDTO create(SaveMedicamentRequestDTO in);
    BooleanResponseDTO update(SaveMedicamentRequestDTO in);
    BooleanResponseDTO delete(IdRequestDTO in);

    CountResponseDTO count(EmptyRequestDTO in);
}
