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
                .typeRdv(e.getTypeRdv())
                .dateRdv(e.getDateRdv())
                .heure(e.getHeure())
                .motif(e.getMotif())
                .statut(parseEtat(e.getStatut()))
                .noteMedecin(e.getNoteMedecin())
                .build();
    }

    public static RDV toEntity(RdvDto d) {
        if (d == null) return null;

        return RDV.builder()
                .id(d.getId())
                .patientId(d.getPatientId())
                .detailJourneeId(d.getDetailJourneeId())
                .listeAttenteId(d.getListeAttenteId())
                .typeRdv(d.getTypeRdv())
                .dateRdv(d.getDateRdv())
                .heure(d.getHeure())
                .motif(d.getMotif())
                .statut(d.getStatut() != null ? d.getStatut().name() : null)
                .noteMedecin(d.getNoteMedecin())
                .build();
    }

    private static EtatRendezVous parseEtat(String s) {
        if (s == null || s.isBlank()) return null;
        try { return EtatRendezVous.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }
}
