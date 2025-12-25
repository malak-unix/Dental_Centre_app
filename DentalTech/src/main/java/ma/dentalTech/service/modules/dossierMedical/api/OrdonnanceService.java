package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.service.modules.dossierMedical.dto.common.*;
import ma.dentalTech.service.modules.dossierMedical.dto.ordonnance.*;

import java.time.LocalDate;

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
