package ma.dentalTech.mvc.controllers.modules.agenda.api;

import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.mvc.dto.agenda.RdvDto;

import java.time.LocalDate;
import java.util.List;

public interface RdvController {

    // LISTING
    List<RdvDto> getAll();
    List<RdvDto> getByDate(LocalDate date);
    List<RdvDto> getUpcomingFromToday();
    List<RdvDto> getByStatus(EtatRendezVous statut);

    // CRUD
    RdvDto getById(Long id);
    RdvDto create(RdvDto dto);
    RdvDto update(RdvDto dto);
    void deleteById(Long id);
}
