package ma.dentalTech.mvc.controllers.modules.patient.api;

import ma.dentalTech.mvc.dto.patient.PatientFormDto;
import ma.dentalTech.mvc.dto.patient.PatientListDto;

import java.util.List;

public interface PatientController {

    List<PatientListDto> lister();

    PatientFormDto consulter(Long id);

    PatientFormDto creer(PatientFormDto dto);

    PatientFormDto modifier(Long id, PatientFormDto dto);

    void supprimer(Long id);

    List<PatientListDto> rechercherParNom(String nom);

    PatientFormDto rechercherParTelephone(String tel);

    // ✅ utilisé dans ton UI
    List<PatientListDto> showRecentPatients();
}
