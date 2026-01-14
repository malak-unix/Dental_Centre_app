package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.common.exceptions.NotFoundException;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.mvc.dto.dossierMedicale.common.*;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceBetweenDatesRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.SaveOrdonnanceRequestDTO;
import ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepository;
import ma.dentalTech.repository.modules.dossierMedical.impl.OrdonnanceRepositoryImpl;
import ma.dentalTech.service.modules.dossierMedical.api.OrdonnanceService;
import ma.dentalTech.service.modules.dossierMedical.exception.*;

import java.time.LocalDate;
import java.util.List;

public class OrdonnanceServiceImpl implements OrdonnanceService {

    private final OrdonnanceRepository repo;

    public OrdonnanceServiceImpl() { this(new OrdonnanceRepositoryImpl()); }
    public OrdonnanceServiceImpl(OrdonnanceRepository repo) { this.repo = repo; }

    @Override
    public OrdonnanceDTO getById(IdRequestDTO in) {
        if (in == null || in.id() == null) throw new ValidationException("id obligatoire");
        Ordonnance o = repo.findById(in.id());
        if (o == null) throw new NotFoundException("Ordonnance introuvable id=" + in.id());
        return toDTO(o);
    }

    @Override
    public ListResponseDTO<OrdonnanceDTO> findByDossierId(DossierIdRequestDTO in) {
        if (in == null || in.dossierId() == null) throw new ValidationException("dossierId obligatoire");
        List<OrdonnanceDTO> out = repo.findByDossierId(in.dossierId()).stream().map(this::toDTO).toList();
        return new ListResponseDTO<>(out);
    }

    @Override
    public ListResponseDTO<OrdonnanceDTO> findByConsultationId(ConsultationIdRequestDTO in) {
        if (in == null || in.consultationId() == null) throw new ValidationException("consultationId obligatoire");
        List<OrdonnanceDTO> out = repo.findByConsultationId(in.consultationId()).stream().map(this::toDTO).toList();
        return new ListResponseDTO<>(out);
    }

    @Override
    public ListResponseDTO<OrdonnanceDTO> findByDateBetween(OrdonnanceBetweenDatesRequestDTO in) {
        if (in == null || in.start() == null || in.end() == null) throw new ValidationException("start/end obligatoires");
        List<OrdonnanceDTO> out = repo.findByDateBetween(in.start(), in.end()).stream().map(this::toDTO).toList();
        return new ListResponseDTO<>(out);
    }

    @Override
    public LongResponseDTO create(SaveOrdonnanceRequestDTO in) {
        validateSave(in, false);

        Ordonnance o = Ordonnance.builder()
                .dossierId(in.ordonnance().dossierId())
                .consultationId(in.ordonnance().consultationId())
                .date(in.ordonnance().date() == null ? LocalDate.now() : in.ordonnance().date())
                .creePar(in.actor().username())
                .modifiePar(in.actor().username())
                .build();

        repo.create(o);
        return new LongResponseDTO(o.getId());
    }

    @Override
    public BooleanResponseDTO update(SaveOrdonnanceRequestDTO in) {
        validateSave(in, true);

        Ordonnance o = repo.findById(in.ordonnance().id());
        if (o == null) throw new NotFoundException("Ordonnance introuvable id=" + in.ordonnance().id());

        o.setDossierId(in.ordonnance().dossierId());
        o.setConsultationId(in.ordonnance().consultationId());
        o.setDate(in.ordonnance().date() == null ? LocalDate.now() : in.ordonnance().date());
        o.setModifiePar(in.actor().username());

        repo.update(o);
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

    private void validateSave(SaveOrdonnanceRequestDTO in, boolean mustHaveId) {
        if (in == null) throw new ValidationException("SaveOrdonnanceRequestDTO null");
        if (in.ordonnance() == null) throw new ValidationException("ordonnance null");
        if (mustHaveId && in.ordonnance().id() == null) throw new ValidationException("id obligatoire pour update");

        // dossierId et consultationId sont NULL autorisés côté DB (dans ton script)
        // mais tu peux imposer une règle métier: au moins un des deux
        if (in.ordonnance().dossierId() == null && in.ordonnance().consultationId() == null)
            throw new ValidationException("dossierId OU consultationId obligatoire");

        if (in.actor() == null || in.actor().username() == null || in.actor().username().isBlank())
            throw new ValidationException("actor.username obligatoire");
    }

    private OrdonnanceDTO toDTO(Ordonnance o) {
        return new OrdonnanceDTO(o.getId(), o.getDossierId(), o.getConsultationId(), o.getDate());
    }
}
