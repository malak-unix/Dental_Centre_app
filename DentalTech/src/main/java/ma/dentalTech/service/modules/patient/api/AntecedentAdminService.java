package ma.dentalTech.service.modules.patient.api;

import ma.dentalTech.mvc.dto.patient.AntecedentAdminRowDTO;

import java.util.List;

public interface AntecedentAdminService {
    List<AntecedentAdminRowDTO> getAll();
}
