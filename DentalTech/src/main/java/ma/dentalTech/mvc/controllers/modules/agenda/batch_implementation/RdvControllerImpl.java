package ma.dentalTech.mvc.controllers.modules.agenda.batch_implementation;

import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.mvc.controllers.modules.agenda.api.RdvController;
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.service.modules.agenda.api.RdvService;

import java.time.LocalDate;
import java.util.List;

public class RdvControllerImpl implements RdvController {

    private final RdvService service;

    public RdvControllerImpl(RdvService service) {
        this.service = service;
    }

    @Override
    public List<RdvDto> getAll() {
        return service.getAll().stream().map(this::toDto).toList();
    }

    @Override
    public List<RdvDto> getByDate(LocalDate date) {
        return service.getByDate(date).stream().map(this::toDto).toList();
    }

    @Override
    public List<RdvDto> getUpcomingFromToday() {
        return service.getUpcomingFromToday().stream().map(this::toDto).toList();
    }

    @Override
    public List<RdvDto> getByStatus(EtatRendezVous statut) {
        return service.getByStatus(statut).stream().map(this::toDto).toList();
    }

    private RdvDto toDto(RDV r) {
        if (r == null) return null;

        return RdvDto.builder()
                .id(r.getId())
                .detailJourneeId(r.getDetailJourneeId())
                .listeAttenteId(r.getListeAttenteId())
                .dateRdv(r.getDateRdv())
                .heure(r.getHeure())
                .motif(r.getMotif())
                .statut(
                        r.getStatut() != null
                                ? EtatRendezVous.valueOf(r.getStatut())
                                : null
                )
                .build();
    }

}
