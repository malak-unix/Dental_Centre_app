package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.mvc.dto.dossierMedicale.common.*;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceBetweenDatesRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.SaveOrdonnanceRequestDTO;

public interface OrdonnanceService {
    OrdonnanceDTO getById(IdRequestDTO in);

    ListResponseDTO<OrdonnanceDTO> findByDossierId(DossierIdRequestDTO in);
    ListResponseDTO<OrdonnanceDTO> findByConsultationId(ConsultationIdRequestDTO in);
    ListResponseDTO<OrdonnanceDTO> findByDateBetween(OrdonnanceBetweenDatesRequestDTO in);

    LongResponseDTO create(SaveOrdonnanceRequestDTO in);
    BooleanResponseDTO update(SaveOrdonnanceRequestDTO in);
    BooleanResponseDTO delete(IdRequestDTO in);

    CountResponseDTO count(EmptyRequestDTO in);
}
