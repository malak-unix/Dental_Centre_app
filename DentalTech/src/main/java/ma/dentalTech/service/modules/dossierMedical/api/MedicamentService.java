package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.service.modules.dossierMedical.dto.common.*;
import ma.dentalTech.service.modules.dossierMedical.dto.medicament.*;

public interface MedicamentService {
    MedicamentDTO getById(IdRequestDTO in);
    ListResponseDTO<MedicamentDTO> findAll(EmptyRequestDTO in);
    ListResponseDTO<MedicamentDTO> search(SearchMedicamentsRequestDTO in);

    LongResponseDTO create(SaveMedicamentRequestDTO in);
    BooleanResponseDTO update(SaveMedicamentRequestDTO in);
    BooleanResponseDTO delete(IdRequestDTO in);

    CountResponseDTO count(EmptyRequestDTO in);
}
