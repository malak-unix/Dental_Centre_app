package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.service.modules.dossierMedical.dto.common.*;
import ma.dentalTech.service.modules.dossierMedical.dto.prescription.*;

public interface PrescriptionService {
    PrescriptionDTO getById(IdRequestDTO in);
    ListResponseDTO<PrescriptionDTO> findByOrdonnanceId(OrdonnanceIdRequestDTO in);

    LongResponseDTO create(SavePrescriptionRequestDTO in);
    BooleanResponseDTO update(SavePrescriptionRequestDTO in);
    BooleanResponseDTO delete(IdRequestDTO in);

    BooleanResponseDTO deleteByOrdonnanceId(OrdonnanceIdRequestDTO in);
    CountResponseDTO countByOrdonnanceId(OrdonnanceIdRequestDTO in);
}
