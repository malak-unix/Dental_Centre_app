package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.mvc.dto.dossierMedicale.common.*;
import ma.dentalTech.mvc.dto.dossierMedicale.document.DocumentMedicalDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.document.SaveDocumentMedicalRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.document.SearchDocumentsRequestDTO;

public interface DocumentMedicalService {
    DocumentMedicalDTO getById(IdRequestDTO in);

    ListResponseDTO<DocumentMedicalDTO> findByDossierId(DossierIdRequestDTO in);
    ListResponseDTO<DocumentMedicalDTO> findByConsultationId(ConsultationIdRequestDTO in);
    ListResponseDTO<DocumentMedicalDTO> search(SearchDocumentsRequestDTO in);

    LongResponseDTO create(SaveDocumentMedicalRequestDTO in);
    BooleanResponseDTO update(SaveDocumentMedicalRequestDTO in);
    BooleanResponseDTO delete(IdRequestDTO in);

    CountResponseDTO count(EmptyRequestDTO in);
}
