package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.service.modules.dossierMedical.dto.common.*;
import ma.dentalTech.service.modules.dossierMedical.dto.consultation.*;

public interface ConsultationService {
    ConsultationDTO getById(IdRequestDTO in);
    ListResponseDTO<ConsultationDTO> findByDossierId(IdRequestDTO in);
    LongResponseDTO create(SaveConsultationRequestDTO in);
    BooleanResponseDTO update(SaveConsultationRequestDTO in);
    BooleanResponseDTO delete(IdRequestDTO in);
}
