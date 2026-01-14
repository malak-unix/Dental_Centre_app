package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.FindActesRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.SaveActeRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.*;

public interface ActeService {
    PageResponseDTO<ActeDTO> find(FindActesRequestDTO in);
    ListResponseDTO<ActeDTO> findAll(IdRequestDTO in); // (DTO entrée obligatoire) -> ignore id
    ActeDTO getById(IdRequestDTO in);
    LongResponseDTO create(SaveActeRequestDTO in);
    BooleanResponseDTO update(SaveActeRequestDTO in);
    BooleanResponseDTO delete(IdRequestDTO in);
}
