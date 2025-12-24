package ma.dentalTech.service.modules.agenda.mappers;

import ma.dentalTech.entities.agenda.ListeAttente;
import ma.dentalTech.mvc.dto.agenda.ListeAttenteDto;

public final class ListeAttenteMapper {
    private ListeAttenteMapper() {}

    public static ListeAttenteDto toDto(ListeAttente e) {
        if (e == null) return null;
        return ListeAttenteDto.builder()
                .id(e.getId())
                .nom(e.getNom())
                .build();
    }

    public static ListeAttente toEntity(ListeAttenteDto d) {
        if (d == null) return null;
        return ListeAttente.builder()
                .id(d.getId())
                .nom(d.getNom())
                .build();
    }
}
