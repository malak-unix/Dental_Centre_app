package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.service.modules.dossierMedical.dto.certificat.*;
import ma.dentalTech.service.modules.dossierMedical.dto.common.*;

public interface CertificatService {
    CertificatDTO getById(IdRequestDTO in);
    ListResponseDTO<CertificatDTO> findByDossierId(DossierIdRequestDTO in);
    ListResponseDTO<CertificatDTO> find(FindCertificatsRequestDTO in);

    LongResponseDTO create(SaveCertificatRequestDTO in);
    BooleanResponseDTO update(SaveCertificatRequestDTO in);
    BooleanResponseDTO delete(IdRequestDTO in);

    CountResponseDTO count(EmptyRequestDTO in);
}
