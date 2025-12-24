package ma.dentalTech.service.modules.agenda.api;

import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.mvc.dto.agenda.RdvDto;

import java.time.LocalDate;
import java.util.List;

public interface RdvAppService {

    // CRUD DTO
    List<RdvDto> getAll();
    RdvDto getById(Long id);
    RdvDto create(RdvDto dto);
    RdvDto update(RdvDto dto);
    void deleteById(Long id);

    // Use cases
    RdvDto confirmer(Long rdvId);
    RdvDto annuler(Long rdvId);

    List<RdvDto> getByPatient(Long patientId);
    List<RdvDto> getByDate(LocalDate date);
    List<RdvDto> getByStatus(EtatRendezVous status);
}
