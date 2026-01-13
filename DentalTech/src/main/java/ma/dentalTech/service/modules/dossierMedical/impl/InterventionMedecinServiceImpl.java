package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.common.exceptions.NotFoundException;
import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.mvc.dto.dossierMedicale.common.*;
import ma.dentalTech.mvc.dto.dossierMedicale.intervention.InterventionMedecinDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.intervention.SaveInterventionRequestDTO;
import ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepository;
import ma.dentalTech.repository.modules.dossierMedical.impl.InterventionMedecinRepositoryImpl;
import ma.dentalTech.service.modules.dossierMedical.api.InterventionMedecinService;
import ma.dentalTech.service.modules.dossierMedical.exception.*;

import java.util.List;

public class InterventionMedecinServiceImpl implements InterventionMedecinService {

    private final InterventionMedecinRepository repo;

    public InterventionMedecinServiceImpl() { this(new InterventionMedecinRepositoryImpl()); }
    public InterventionMedecinServiceImpl(InterventionMedecinRepository repo) { this.repo = repo; }

    @Override
    public InterventionMedecinDTO getById(IdRequestDTO in) {
        if (in == null || in.id() == null) throw new ValidationException("id obligatoire");
        InterventionMedecin im = repo.findById(in.id());
        if (im == null) throw new NotFoundException("Intervention introuvable id=" + in.id());
        return toDTO(im);
    }

    @Override
    public ListResponseDTO<InterventionMedecinDTO> findByConsultationId(ConsultationIdRequestDTO in) {
        if (in == null || in.consultationId() == null) throw new ValidationException("consultationId obligatoire");
        List<InterventionMedecinDTO> out = repo.findByConsultationId(in.consultationId()).stream().map(this::toDTO).toList();
        return new ListResponseDTO<>(out);
    }

    @Override
    public LongResponseDTO create(SaveInterventionRequestDTO in) {
        validateSave(in, false);

        InterventionMedecin im = InterventionMedecin.builder()
                .consultationId(in.intervention().consultationId())
                .acteId(in.intervention().acteId())
                .prixDePatient(in.intervention().prixPatient() == null ? 0.0 : in.intervention().prixPatient())
                .numDent(in.intervention().numDent())
                .creePar(in.actor().username())
                .modifiePar(in.actor().username())
                .build();

        repo.create(im);
        return new LongResponseDTO(im.getId());
    }

    @Override
    public BooleanResponseDTO update(SaveInterventionRequestDTO in) {
        validateSave(in, true);

        InterventionMedecin im = repo.findById(in.intervention().id());
        if (im == null) throw new NotFoundException("Intervention introuvable id=" + in.intervention().id());

        im.setConsultationId(in.intervention().consultationId());
        im.setActeId(in.intervention().acteId());
        im.setPrixDePatient(in.intervention().prixPatient() == null ? 0.0 : in.intervention().prixPatient());
        im.setNumDent(in.intervention().numDent());
        im.setModifiePar(in.actor().username());

        repo.update(im);
        return new BooleanResponseDTO(true);
    }

    @Override
    public BooleanResponseDTO delete(IdRequestDTO in) {
        if (in == null || in.id() == null) throw new ValidationException("id obligatoire");
        repo.deleteById(in.id());
        return new BooleanResponseDTO(true);
    }

    @Override
    public BooleanResponseDTO deleteByConsultationId(ConsultationIdRequestDTO in) {
        if (in == null || in.consultationId() == null) throw new ValidationException("consultationId obligatoire");
        repo.deleteByConsultationId(in.consultationId());
        return new BooleanResponseDTO(true);
    }

    private void validateSave(SaveInterventionRequestDTO in, boolean mustHaveId) {
        if (in == null) throw new ValidationException("SaveInterventionRequestDTO null");
        if (in.intervention() == null) throw new ValidationException("intervention null");
        if (mustHaveId && in.intervention().id() == null) throw new ValidationException("id obligatoire pour update");
        if (in.intervention().consultationId() == null) throw new ValidationException("consultationId obligatoire");

        if (in.intervention().prixPatient() != null && in.intervention().prixPatient() < 0)
            throw new ValidationException("prixPatient ne peut pas être négatif");

        if (in.intervention().numDent() != null && (in.intervention().numDent() < 1 || in.intervention().numDent() > 32))
            throw new ValidationException("numDent invalide (1..32)");

        if (in.actor() == null || in.actor().username() == null || in.actor().username().isBlank())
            throw new ValidationException("actor.username obligatoire");
    }

    private InterventionMedecinDTO toDTO(InterventionMedecin im) {
        return new InterventionMedecinDTO(
                im.getId(),
                im.getConsultationId(),
                im.getActeId(),
                im.getPrixDePatient(),
                im.getNumDent()
        );
    }
}
