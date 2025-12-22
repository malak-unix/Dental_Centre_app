package ma.dentalTech.service.modules.agenda.mappers;

import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.mvc.dto.agenda.RdvDto;

public final class RdvMapper {

    private RdvMapper() {}

    public static RDV toEntity(RdvDto dto) {
        if (dto == null) return null;

        return RDV.builder()
                .id(dto.getId())
                .patientId(dto.getPatientId())
                .detailJourneeId(dto.getDetailJourneeId())
                .listeAttenteId(dto.getListeAttenteId())
                .typeRdv(dto.getTypeRdv())
                .dateRdv(dto.getDateRdv())
                .heure(dto.getHeure())
                .motif(dto.getMotif())
                .statut(dto.getStatut() == null ? null : dto.getStatut().name()) // enum -> String
                .noteMedecin(dto.getNoteMedecin())
                .build();
    }

    public static RdvDto toDto(RDV entity) {
        if (entity == null) return null;

        return RdvDto.builder()
                .id(entity.getId())
                .patientId(entity.getPatientId())
                .detailJourneeId(entity.getDetailJourneeId())
                .listeAttenteId(entity.getListeAttenteId())
                .typeRdv(entity.getTypeRdv())
                .dateRdv(entity.getDateRdv())
                .heure(entity.getHeure())
                .motif(entity.getMotif())
                .statut(parseEtat(entity.getStatut())) // String -> enum
                .noteMedecin(entity.getNoteMedecin())
                .build();
    }

    private static EtatRendezVous parseEtat(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return EtatRendezVous.valueOf(s.trim());
        } catch (IllegalArgumentException ex) {
            return null; // si la BD contient une valeur inconnue
        }
    }
}
