package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.mvc.dto.dossierMedicale.common.BooleanResponseDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.IdRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.LongResponseDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.PageResponseDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.dossier.*;

public interface DossierMedicalService {
    PageResponseDTO<DossierListItemDTO> list(DossierListRequestDTO in);
    DossierDetailsDTO details(IdRequestDTO in);

    DossierDTO getById(IdRequestDTO in);
    LongResponseDTO create(SaveDossierRequestDTO in);
    BooleanResponseDTO update(SaveDossierRequestDTO in);
    BooleanResponseDTO delete(IdRequestDTO in);
}
