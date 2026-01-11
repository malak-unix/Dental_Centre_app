package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepository;
import ma.dentalTech.repository.modules.dossierMedical.impl.MedicamentRepositoryImpl;
import ma.dentalTech.service.modules.dossierMedical.api.MedicamentService;
import ma.dentalTech.service.modules.dossierMedical.dto.common.*;
import ma.dentalTech.service.modules.dossierMedical.dto.medicament.*;
import ma.dentalTech.service.modules.dossierMedical.exception.*;

import java.util.List;

public class MedicamentServiceImpl implements MedicamentService {

    private final MedicamentRepository repo;

    public MedicamentServiceImpl() { this(new MedicamentRepositoryImpl()); }
    public MedicamentServiceImpl(MedicamentRepository repo) { this.repo = repo; }

    @Override
    public MedicamentDTO getById(IdRequestDTO in) {
        if (in == null || in.id() == null) throw new ValidationException("id obligatoire");
        Medicament m = repo.findById(in.id());
        if (m == null) throw new NotFoundException("Medicament introuvable id=" + in.id());
        return toDTO(m);
    }

    @Override
    public ListResponseDTO<MedicamentDTO> findAll(EmptyRequestDTO in) {
        List<MedicamentDTO> out = repo.findAll().stream().map(this::toDTO).toList();
        return new ListResponseDTO<>(out);
    }

    @Override
    public ListResponseDTO<MedicamentDTO> search(SearchMedicamentsRequestDTO in) {
        if (in == null) throw new ValidationException("SearchMedicamentsRequestDTO null");

        List<Medicament> data;
        if (in.remboursable() != null) data = repo.findByRemboursable(in.remboursable());
        else if (in.keyword() != null && !in.keyword().isBlank()) data = repo.searchByNom(in.keyword());
        else data = repo.findAll();

        return new ListResponseDTO<>(data.stream().map(this::toDTO).toList());
    }

    @Override
    public LongResponseDTO create(SaveMedicamentRequestDTO in) {
        validateSave(in, false);

        Medicament m = Medicament.builder()
                .nom(in.medicament().nom())
                .laboratoire(in.medicament().laboratoire())
                .type(in.medicament().type())
                .forme(in.medicament().forme())
                .remboursable(in.medicament().remboursable())
                .prixUnitaire(in.medicament().prixUnitaire())
                .description(in.medicament().description())
                .creePar(in.actor().username())
                .modifiePar(in.actor().username())
                .build();

        repo.create(m);
        return new LongResponseDTO(m.getId());
    }

    @Override
    public BooleanResponseDTO update(SaveMedicamentRequestDTO in) {
        validateSave(in, true);

        Medicament m = repo.findById(in.medicament().id());
        if (m == null) throw new NotFoundException("Medicament introuvable id=" + in.medicament().id());

        m.setNom(in.medicament().nom());
        m.setLaboratoire(in.medicament().laboratoire());
        m.setType(in.medicament().type());
        m.setForme(in.medicament().forme());
        m.setRemboursable(in.medicament().remboursable());
        m.setPrixUnitaire(in.medicament().prixUnitaire());
        m.setDescription(in.medicament().description());
        m.setModifiePar(in.actor().username());

        repo.update(m);
        return new BooleanResponseDTO(true);
    }

    @Override
    public BooleanResponseDTO delete(IdRequestDTO in) {
        if (in == null || in.id() == null) throw new ValidationException("id obligatoire");
        repo.deleteById(in.id());
        return new BooleanResponseDTO(true);
    }

    @Override
    public CountResponseDTO count(EmptyRequestDTO in) {
        return new CountResponseDTO(repo.count());
    }

    private void validateSave(SaveMedicamentRequestDTO in, boolean mustHaveId) {
        if (in == null) throw new ValidationException("SaveMedicamentRequestDTO null");
        if (in.medicament() == null) throw new ValidationException("medicament null");
        if (mustHaveId && in.medicament().id() == null) throw new ValidationException("id obligatoire pour update");
        if (in.medicament().nom() == null || in.medicament().nom().isBlank()) throw new ValidationException("nom obligatoire");

        if (in.medicament().prixUnitaire() != null && in.medicament().prixUnitaire() < 0)
            throw new ValidationException("prixUnitaire ne peut pas être négatif");

        if (in.actor() == null || in.actor().username() == null || in.actor().username().isBlank())
            throw new ValidationException("actor.username obligatoire");
    }

    private MedicamentDTO toDTO(Medicament m) {
        return new MedicamentDTO(
                m.getId(), m.getNom(), m.getLaboratoire(), m.getType(), m.getForme(),
                m.isRemboursable(), m.getPrixUnitaire(), m.getDescription()
        );
    }
}
