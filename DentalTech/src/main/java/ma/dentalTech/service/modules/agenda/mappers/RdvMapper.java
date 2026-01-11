package ma.dentalTech.service.modules.agenda.mappers;

import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.mvc.dto.agenda.RdvDto;

public final class RdvMapper {

    private RdvMapper() {}

    public static RdvDto toDto(RDV e) {
        if (e == null) return null;

        return RdvDto.builder()
                .id(e.getId())
                .patientId(e.getPatientId())
                .detailJourneeId(e.getDetailJourneeId())
                .listeAttenteId(e.getListeAttenteId())
                .dateRdv(e.getDateRdv())
                .heure(e.getHeure())
                .motif(e.getMotif())
                .statut(e.getStatut())       // enum -> enum
                .noteMedecin(e.getNoteMedecin())
                // typeRdv: ton entity ne l’a pas => on laisse dto.typeRdv tel quel uniquement côté UI
                .build();
    }

    public static RDV toEntity(RdvDto d) {
        if (d == null) return null;

        EtatRendezVous st = (d.getStatut() != null) ? d.getStatut() : EtatRendezVous.PLANIFIE;

        return RDV.builder()
                .id(d.getId())
                .patientId(d.getPatientId())
                .detailJourneeId(d.getDetailJourneeId())
                .listeAttenteId(d.getListeAttenteId())
                .dateRdv(d.getDateRdv())
                .heure(d.getHeure())
                .motif(d.getMotif())
                .statut(st)                 // ✅ enum
                .noteMedecin(d.getNoteMedecin())
                .build();
    }
}
