package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.mvc.dto.dossierMedicale.common.*;
import ma.dentalTech.mvc.dto.dossierMedicale.prescription.PrescriptionDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.prescription.SavePrescriptionRequestDTO;

public interface PrescriptionService {
    PrescriptionDTO getById(IdRequestDTO in);
    ListResponseDTO<PrescriptionDTO> findByOrdonnanceId(OrdonnanceIdRequestDTO in);

    LongResponseDTO create(SavePrescriptionRequestDTO in);
    BooleanResponseDTO update(SavePrescriptionRequestDTO in);
    BooleanResponseDTO delete(IdRequestDTO in);

    BooleanResponseDTO deleteByOrdonnanceId(OrdonnanceIdRequestDTO in);
    CountResponseDTO countByOrdonnanceId(OrdonnanceIdRequestDTO in);
}
