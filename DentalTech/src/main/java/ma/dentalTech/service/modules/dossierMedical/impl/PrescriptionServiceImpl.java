package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.common.exceptions.NotFoundException;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.mvc.dto.dossierMedicale.common.*;
import ma.dentalTech.mvc.dto.dossierMedicale.prescription.PrescriptionDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.prescription.SavePrescriptionRequestDTO;
import ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepository;
import ma.dentalTech.repository.modules.dossierMedical.impl.PrescriptionRepositoryImpl;
import ma.dentalTech.service.modules.dossierMedical.api.PrescriptionService;
import ma.dentalTech.service.modules.dossierMedical.exception.*;

import java.util.List;

public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository repo;

    public PrescriptionServiceImpl() { this(new PrescriptionRepositoryImpl()); }
    public PrescriptionServiceImpl(PrescriptionRepository repo) { this.repo = repo; }

    @Override
    public PrescriptionDTO getById(IdRequestDTO in) {
        if (in == null || in.id() == null) throw new ValidationException("id obligatoire");
        Prescription p = repo.findById(in.id());
        if (p == null) throw new NotFoundException("Prescription introuvable id=" + in.id());
        return toDTO(p);
    }

    @Override
    public ListResponseDTO<PrescriptionDTO> findByOrdonnanceId(OrdonnanceIdRequestDTO in) {
        if (in == null || in.ordonnanceId() == null) throw new ValidationException("ordonnanceId obligatoire");
        List<PrescriptionDTO> out = repo.findByOrdonnanceId(in.ordonnanceId()).stream().map(this::toDTO).toList();
        return new ListResponseDTO<>(out);
    }

    @Override
    public LongResponseDTO create(SavePrescriptionRequestDTO in) {
        validateSave(in, false);

        Prescription p = Prescription.builder()
                .ordonnanceId(in.prescription().ordonnanceId())
                .medicamentId(in.prescription().medicamentId())
                .quantite(Math.max(1, in.prescription().quantite()))
                .frequence(in.prescription().frequence())
                .dureeEnJours(Math.max(0, in.prescription().dureeEnJours()))
                .creePar(in.actor().username())
                .modifiePar(in.actor().username())
                .build();

        repo.create(p);
        return new LongResponseDTO(p.getId());
    }

    @Override
    public BooleanResponseDTO update(SavePrescriptionRequestDTO in) {
        validateSave(in, true);

        Prescription p = repo.findById(in.prescription().id());
        if (p == null) throw new NotFoundException("Prescription introuvable id=" + in.prescription().id());

        p.setOrdonnanceId(in.prescription().ordonnanceId());
        p.setMedicamentId(in.prescription().medicamentId());
        p.setQuantite(Math.max(1, in.prescription().quantite()));
        p.setFrequence(in.prescription().frequence());
        p.setDureeEnJours(Math.max(0, in.prescription().dureeEnJours()));
        p.setModifiePar(in.actor().username());

        repo.update(p);
        return new BooleanResponseDTO(true);
    }

    @Override
    public BooleanResponseDTO delete(IdRequestDTO in) {
        if (in == null || in.id() == null) throw new ValidationException("id obligatoire");
        repo.deleteById(in.id());
        return new BooleanResponseDTO(true);
    }

    @Override
    public BooleanResponseDTO deleteByOrdonnanceId(OrdonnanceIdRequestDTO in) {
        if (in == null || in.ordonnanceId() == null) throw new ValidationException("ordonnanceId obligatoire");
        repo.deleteByOrdonnanceId(in.ordonnanceId());
        return new BooleanResponseDTO(true);
    }

    @Override
    public CountResponseDTO countByOrdonnanceId(OrdonnanceIdRequestDTO in) {
        if (in == null || in.ordonnanceId() == null) throw new ValidationException("ordonnanceId obligatoire");
        return new CountResponseDTO(repo.countByOrdonnanceId(in.ordonnanceId()));
    }

    private void validateSave(SavePrescriptionRequestDTO in, boolean mustHaveId) {
        if (in == null) throw new ValidationException("SavePrescriptionRequestDTO null");
        if (in.prescription() == null) throw new ValidationException("prescription null");
        if (mustHaveId && in.prescription().id() == null) throw new ValidationException("id obligatoire pour update");
        if (in.prescription().ordonnanceId() == null) throw new ValidationException("ordonnanceId obligatoire");

        if (in.actor() == null || in.actor().username() == null || in.actor().username().isBlank())
            throw new ValidationException("actor.username obligatoire");
    }

    private PrescriptionDTO toDTO(Prescription p) {
        return new PrescriptionDTO(
                p.getId(), p.getOrdonnanceId(), p.getMedicamentId(),
                p.getQuantite(), p.getFrequence(), p.getDureeEnJours()
        );
    }
}
