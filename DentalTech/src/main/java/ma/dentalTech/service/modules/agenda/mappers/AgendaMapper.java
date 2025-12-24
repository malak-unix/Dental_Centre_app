package ma.dentalTech.service.modules.agenda.mappers;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.DetailJournee;
import ma.dentalTech.entities.enums.StatutJournee;
import ma.dentalTech.mvc.dto.agenda.AgendaMensuelDto;
import ma.dentalTech.mvc.dto.agenda.DetailJourneeDto;

public final class AgendaMapper {
    private AgendaMapper() {}

    public static AgendaMensuelDto toDto(AgendaMensuel a) {
        if (a == null) return null;
        return AgendaMensuelDto.builder()
                .id(a.getId())
                .medecinId(a.getMedecinId())
                .mois(a.getMois())
                .annee(a.getAnnee())
                .build();
    }

    public static AgendaMensuel toEntity(AgendaMensuelDto d) {
        if (d == null) return null;
        return AgendaMensuel.builder()
                .id(d.getId())
                .medecinId(d.getMedecinId())
                .mois(d.getMois())
                .annee(d.getAnnee())
                .build();
    }

    public static DetailJourneeDto toDto(DetailJournee dj) {
        if (dj == null) return null;
        return DetailJourneeDto.builder()
                .id(dj.getId())
                .agendaId(dj.getAgendaId())
                .dateJour(dj.getDateJour())
                .heureDebutTravail(dj.getHeureDebutTravail())
                .heureFinTravail(dj.getHeureFinTravail())
                .etatJour(StatutJournee.valueOf(String.valueOf(dj.getEtatJour())))
                .commentaire(dj.getCommentaire())
                .build();
    }

    public static DetailJournee toEntity(DetailJourneeDto d) {
        if (d == null) return null;
        return DetailJournee.builder()
                .id(d.getId())
                .agendaId(d.getAgendaId())
                .dateJour(d.getDateJour())
                .heureDebutTravail(d.getHeureDebutTravail())
                .heureFinTravail(d.getHeureFinTravail())
                .etatJour(StatutJournee.valueOf(String.valueOf(d.getEtatJour())))
                .commentaire(d.getCommentaire())
                .build();
    }
}
