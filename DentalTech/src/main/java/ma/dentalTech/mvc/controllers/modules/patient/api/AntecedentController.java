package ma.dentalTech.mvc.controllers.modules.patient.api;

import ma.dentalTech.mvc.dto.patient.AntecedentFormDto;
import ma.dentalTech.mvc.dto.patient.AntecedentListDto;

import java.util.List;

public interface AntecedentController {

    List<AntecedentListDto> listByPatient(Long patientId);

    AntecedentFormDto create(Long patientId, AntecedentFormDto dto);

    AntecedentFormDto update(Long id, AntecedentFormDto dto);

    void delete(Long id);
}
