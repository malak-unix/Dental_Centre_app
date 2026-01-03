package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.service.modules.dossierMedical.dto.common.*;
import ma.dentalTech.service.modules.dossierMedical.dto.intervention.*;

public interface InterventionMedecinService {
    InterventionMedecinDTO getById(IdRequestDTO in);
    ListResponseDTO<InterventionMedecinDTO> findByConsultationId(ConsultationIdRequestDTO in);

    LongResponseDTO create(SaveInterventionRequestDTO in);
    BooleanResponseDTO update(SaveInterventionRequestDTO in);
    BooleanResponseDTO delete(IdRequestDTO in);

    BooleanResponseDTO deleteByConsultationId(ConsultationIdRequestDTO in);
}
