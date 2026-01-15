package ma.dentalTech.mvc.controllers.modules.patient.api;

import ma.dentalTech.mvc.dto.patient.AntecedentAdminRowDTO;

import java.util.List;

public interface AntecedentAdminController {
    List<AntecedentAdminRowDTO> getAll();
}
