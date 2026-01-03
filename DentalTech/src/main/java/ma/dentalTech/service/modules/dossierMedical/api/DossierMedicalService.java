package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.service.modules.dossierMedical.dto.common.*;
import ma.dentalTech.service.modules.dossierMedical.dto.dossier.*;

public interface DossierMedicalService {
    PageResponseDTO<DossierListItemDTO> list(DossierListRequestDTO in);
    DossierDetailsDTO details(IdRequestDTO in);

    DossierDTO getById(IdRequestDTO in);
    LongResponseDTO create(SaveDossierRequestDTO in);
    BooleanResponseDTO update(SaveDossierRequestDTO in);
    BooleanResponseDTO delete(IdRequestDTO in);
}
