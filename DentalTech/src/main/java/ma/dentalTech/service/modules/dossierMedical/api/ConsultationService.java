package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.mvc.dto.dossierMedicale.common.BooleanResponseDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.IdRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.ListResponseDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.LongResponseDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationListRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.SaveConsultationRequestDTO;

public interface ConsultationService {
    ConsultationDTO getById(IdRequestDTO in);
    ListResponseDTO<ConsultationDTO> findByDossierId(IdRequestDTO in);
    LongResponseDTO create(SaveConsultationRequestDTO in);
    BooleanResponseDTO update(SaveConsultationRequestDTO in);
    BooleanResponseDTO delete(IdRequestDTO in);

    ListResponseDTO<ConsultationListItemDTO> searchForList(ConsultationListRequestDTO in);

}
