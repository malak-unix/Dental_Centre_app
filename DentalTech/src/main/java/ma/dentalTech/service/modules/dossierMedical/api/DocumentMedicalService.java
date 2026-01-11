package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.service.modules.dossierMedical.dto.common.*;
import ma.dentalTech.service.modules.dossierMedical.dto.document.*;

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
