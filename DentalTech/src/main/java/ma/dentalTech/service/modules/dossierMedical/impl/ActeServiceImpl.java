package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository;
import ma.dentalTech.repository.modules.dossierMedical.impl.ActeRepositoryImpl;
import ma.dentalTech.service.modules.dossierMedical.api.ActeService;
import ma.dentalTech.service.modules.dossierMedical.dto.acte.*;
import ma.dentalTech.service.modules.dossierMedical.dto.common.*;
import ma.dentalTech.service.modules.dossierMedical.exception.*;

import java.util.List;

public class ActeServiceImpl implements ActeService {

    private final ActeRepository repo;

    public ActeServiceImpl() { this(new ActeRepositoryImpl()); }
    public ActeServiceImpl(ActeRepository repo) { this.repo = repo; }

    @Override
    public PageResponseDTO<ActeDTO> find(FindActesRequestDTO in) {
        if (in == null) throw new ValidationException("FindActesRequestDTO null");

        int limit = (in.page() == null || in.page().limit() == null || in.page().limit() <= 0) ? 50 : in.page().limit();
        int offset = (in.page() == null || in.page().offset() == null || in.page().offset() < 0) ? 0 : in.page().offset();

        List<Acte> data;
        if (in.categorie() != null && !in.categorie().isBlank()) data = repo.findByCategorie(in.categorie());
        else if (in.keyword() != null && !in.keyword().isBlank()) data = repo.searchByLibelle(in.keyword());
        else data = repo.findPage(limit, offset);

        List<ActeDTO> out = data.stream().map(this::toDTO).toList();
        return new PageResponseDTO<>(out, repo.count());
    }

    @Override
    public ListResponseDTO<ActeDTO> findAll(IdRequestDTO in) {
        List<ActeDTO> out = repo.findAll().stream().map(this::toDTO).toList();
        return new ListResponseDTO<>(out);
    }

    @Override
    public ActeDTO getById(IdRequestDTO in) {
        if (in == null || in.id() == null) throw new ValidationException("id obligatoire");
        Acte a = repo.findById(in.id());
        if (a == null) throw new NotFoundException("Acte introuvable id=" + in.id());
        return toDTO(a);
    }

    @Override
    public LongResponseDTO create(SaveActeRequestDTO in) {
        validateSave(in, false);

        Acte a = Acte.builder()
                .libelle(in.acte().libelle())
                .categorie(in.acte().categorie())
                .prixBase(in.acte().prixBase())
                .description(in.acte().description())
                .creePar(in.actor().username())
                .modifiePar(in.actor().username())
                .build();

        repo.create(a);
        return new LongResponseDTO(a.getId());
    }

    @Override
    public BooleanResponseDTO update(SaveActeRequestDTO in) {
        validateSave(in, true);

        Acte a = repo.findById(in.acte().id());
        if (a == null) throw new NotFoundException("Acte introuvable id=" + in.acte().id());

        a.setLibelle(in.acte().libelle());
        a.setCategorie(in.acte().categorie());
        a.setPrixBase(in.acte().prixBase());
        a.setDescription(in.acte().description());
        a.setModifiePar(in.actor().username());

        repo.update(a);
        return new BooleanResponseDTO(true);
    }

    @Override
    public BooleanResponseDTO delete(IdRequestDTO in) {
        if (in == null || in.id() == null) throw new ValidationException("id obligatoire");
        repo.deleteById(in.id());
        return new BooleanResponseDTO(true);
    }

    private void validateSave(SaveActeRequestDTO in, boolean mustHaveId) {
        if (in == null) throw new ValidationException("SaveActeRequestDTO null");
        if (in.acte() == null) throw new ValidationException("acte null");
        if (mustHaveId && in.acte().id() == null) throw new ValidationException("id obligatoire pour update");
        if (in.acte().libelle() == null || in.acte().libelle().isBlank()) throw new ValidationException("libelle obligatoire");
        if (in.actor() == null || in.actor().username() == null || in.actor().username().isBlank())
            throw new ValidationException("actor.username obligatoire");
    }

    private ActeDTO toDTO(Acte a) {
        return new ActeDTO(a.getId(), a.getLibelle(), a.getCategorie(), a.getPrixBase(), a.getDescription());
    }
}
