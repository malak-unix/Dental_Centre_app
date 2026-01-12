package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.mvc.dto.dossierMedicale.common.*;
import ma.dentalTech.mvc.dto.dossierMedicale.intervention.InterventionMedecinDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.intervention.SaveInterventionRequestDTO;

public interface InterventionMedecinService {
    InterventionMedecinDTO getById(IdRequestDTO in);
    ListResponseDTO<InterventionMedecinDTO> findByConsultationId(ConsultationIdRequestDTO in);

    LongResponseDTO create(SaveInterventionRequestDTO in);
    BooleanResponseDTO update(SaveInterventionRequestDTO in);
    BooleanResponseDTO delete(IdRequestDTO in);

    BooleanResponseDTO deleteByConsultationId(ConsultationIdRequestDTO in);
}
