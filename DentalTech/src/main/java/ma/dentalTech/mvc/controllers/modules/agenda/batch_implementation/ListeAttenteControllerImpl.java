package ma.dentalTech.mvc.controllers.modules.agenda.batch_implementation;

import ma.dentalTech.entities.agenda.ListeAttente;
import ma.dentalTech.mvc.controllers.modules.agenda.api.ListeAttenteController;
import ma.dentalTech.mvc.dto.agenda.ListeAttenteDto;
import ma.dentalTech.service.modules.agenda.api.ListeAttenteService;

import java.util.List;

public class ListeAttenteControllerImpl implements ListeAttenteController {

    private final ListeAttenteService service;

    public ListeAttenteControllerImpl(ListeAttenteService service) {
        this.service = service;
    }

    @Override
    public List<ListeAttenteDto> getAll() {
        return service.getAll().stream().map(this::toDto).toList();
    }

    @Override
    public List<ListeAttenteDto> searchByNom(String nom) {
        if (nom == null || nom.isBlank()) return getAll();
        return service.searchByNomListe(nom).stream().map(this::toDto).toList();
    }

    private ListeAttenteDto toDto(ListeAttente l) {
        return (l == null) ? null : ListeAttenteDto.builder()
                .id(l.getId())
                .nom(l.getNom())
                .build();
    }
}
