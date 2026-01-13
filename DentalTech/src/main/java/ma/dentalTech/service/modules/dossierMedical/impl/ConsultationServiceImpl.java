package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.common.exceptions.NotFoundException;
import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.mvc.dto.dossierMedicale.common.BooleanResponseDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.IdRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.ListResponseDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.LongResponseDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationListRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.SaveConsultationRequestDTO;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.dentalTech.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl;
import ma.dentalTech.service.modules.dossierMedical.api.ConsultationService;
import ma.dentalTech.service.modules.dossierMedical.exception.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service Consultation (module dossierMedical).
 *
 * Règle simple:
 * - CRUD: on travaille avec l'entity Consultation dans la couche repository.
 * - Écran "Mes consultations": on utilise une projection DTO (JOIN consultation+dossier+patient+facture)
 *   via ConsultationRepository.searchForList().
 */
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository repo;

    public ConsultationServiceImpl() {
        this(new ConsultationRepositoryImpl());
    }

    public ConsultationServiceImpl(ConsultationRepository repo) {
        this.repo = repo;
    }

    // =====================================================
    // CRUD
    // =====================================================
    @Override
    public ConsultationDTO getById(IdRequestDTO in) {
        if (in == null || in.id() == null) throw new ValidationException("id obligatoire");

        Consultation c = repo.findById(in.id());
        if (c == null) throw new NotFoundException("Consultation introuvable id=" + in.id());

        return toDTO(c);
    }

    @Override
    public ListResponseDTO<ConsultationDTO> findByDossierId(IdRequestDTO in) {
        if (in == null || in.id() == null) throw new ValidationException("dossierId obligatoire");

        List<ConsultationDTO> out = repo.findByDossierId(in.id())
                .stream()
                .map(this::toDTO)
                .toList();

        return new ListResponseDTO<>(out);
    }

    @Override
    public LongResponseDTO create(SaveConsultationRequestDTO in) {
        validateSave(in, false);

        ConsultationDTO dto = in.consultation();
        Consultation c = Consultation.builder()
                .dossierId(dto.dossierId())
                .date(toDateTime(dto.date()))
                .status(dto.statut())
                .observationMedecin(dto.observationMedecin())
                .creePar(in.actor().username())
                .modifiePar(in.actor().username())
                .build();

        repo.create(c);
        return new LongResponseDTO(c.getId());
    }

    @Override
    public BooleanResponseDTO update(SaveConsultationRequestDTO in) {
        validateSave(in, true);

        ConsultationDTO dto = in.consultation();
        Consultation c = repo.findById(dto.id());
        if (c == null) throw new NotFoundException("Consultation introuvable id=" + dto.id());

        c.setDossierId(dto.dossierId());
        c.setDate(toDateTime(dto.date()));
        c.setStatus(dto.statut());
        c.setObservationMedecin(dto.observationMedecin());
        c.setModifiePar(in.actor().username());

        repo.update(c);
        return new BooleanResponseDTO(true);
    }

    @Override
    public BooleanResponseDTO delete(IdRequestDTO in) {
        if (in == null || in.id() == null) throw new ValidationException("id obligatoire");
        repo.deleteById(in.id());
        return new BooleanResponseDTO(true);
    }

    // =====================================================
    // Écran liste (DTO projection)
    // =====================================================
    @Override
    public ListResponseDTO<ConsultationListItemDTO> searchForList(ConsultationListRequestDTO in) {
        if (in == null) throw new ValidationException("ConsultationListRequestDTO null");
        if (in.getMedecinId() == null) throw new ValidationException("medecinId obligatoire");

        List<ConsultationListItemDTO> items = repo.searchForList(in);
        return new ListResponseDTO<>(items);
    }

    // =====================================================
    // Helpers
    // =====================================================
    private void validateSave(SaveConsultationRequestDTO in, boolean mustHaveId) {
        if (in == null) throw new ValidationException("SaveConsultationRequestDTO null");
        if (in.consultation() == null) throw new ValidationException("consultation null");
        if (in.actor() == null || in.actor().username() == null || in.actor().username().isBlank())
            throw new ValidationException("actor.username obligatoire");

        ConsultationDTO dto = in.consultation();
        if (mustHaveId && dto.id() == null) throw new ValidationException("id obligatoire pour update");
        if (dto.dossierId() == null) throw new ValidationException("dossierId obligatoire");
        if (dto.date() == null) throw new ValidationException("date obligatoire");
        if (dto.statut() == null) throw new ValidationException("statut obligatoire");
    }

    private ConsultationDTO toDTO(Consultation c) {
        return new ConsultationDTO(
                c.getId(),
                c.getDossierId(),
                c.getDate() == null ? null : c.getDate().toLocalDate(),
                c.getStatus(),
                c.getObservationMedecin()
        );
    }

    private static LocalDateTime toDateTime(LocalDate d) {
        // Choix : midi (12:00) pour éviter un "00:00" qui peut surprendre côté UI.
        return d == null ? null : d.atTime(12, 0);
    }
}
