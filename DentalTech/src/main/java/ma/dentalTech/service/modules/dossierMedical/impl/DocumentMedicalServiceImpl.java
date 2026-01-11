package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.DocumentMedical;
import ma.dentalTech.entities.enums.TypeDocument;
import ma.dentalTech.repository.modules.dossierMedical.api.DocumentMedicalRepository;
import ma.dentalTech.repository.modules.dossierMedical.impl.DocumentMedicalRepositoryImpl;
import ma.dentalTech.service.modules.dossierMedical.api.DocumentMedicalService;
import ma.dentalTech.service.modules.dossierMedical.dto.common.*;
import ma.dentalTech.service.modules.dossierMedical.dto.document.*;
import ma.dentalTech.service.modules.dossierMedical.exception.*;

import java.time.LocalDateTime;
import java.util.List;

public class DocumentMedicalServiceImpl implements DocumentMedicalService {

    private final DocumentMedicalRepository repo;

    public DocumentMedicalServiceImpl() { this(new DocumentMedicalRepositoryImpl()); }
    public DocumentMedicalServiceImpl(DocumentMedicalRepository repo) { this.repo = repo; }

    @Override
    public DocumentMedicalDTO getById(IdRequestDTO in) {
        if (in == null || in.id() == null) throw new ValidationException("id obligatoire");
        DocumentMedical d = repo.findById(in.id());
        if (d == null) throw new NotFoundException("DocumentMedical introuvable id=" + in.id());
        return toDTO(d);
    }

    @Override
    public ListResponseDTO<DocumentMedicalDTO> findByDossierId(DossierIdRequestDTO in) {
        if (in == null || in.dossierId() == null) throw new ValidationException("dossierId obligatoire");
        List<DocumentMedicalDTO> out = repo.findByDossierId(in.dossierId()).stream().map(this::toDTO).toList();
        return new ListResponseDTO<>(out);
    }

    @Override
    public ListResponseDTO<DocumentMedicalDTO> findByConsultationId(ConsultationIdRequestDTO in) {
        if (in == null || in.consultationId() == null) throw new ValidationException("consultationId obligatoire");
        List<DocumentMedicalDTO> out = repo.findByConsultationId(in.consultationId()).stream().map(this::toDTO).toList();
        return new ListResponseDTO<>(out);
    }

    @Override
    public ListResponseDTO<DocumentMedicalDTO> search(SearchDocumentsRequestDTO in) {
        if (in == null) throw new ValidationException("SearchDocumentsRequestDTO null");

        List<DocumentMedical> data;
        if (in.dossierId() != null) data = repo.findByDossierId(in.dossierId());
        else if (in.consultationId() != null) data = repo.findByConsultationId(in.consultationId());
        else if (in.keyword() != null && !in.keyword().isBlank()) data = repo.searchByTitreOrNom(in.keyword());
        else data = repo.findAll();

        return new ListResponseDTO<>(data.stream().map(this::toDTO).toList());
    }

    @Override
    public LongResponseDTO create(SaveDocumentMedicalRequestDTO in) {
        validateSave(in, false);

        DocumentMedical d = DocumentMedical.builder()
                .dossierId(in.document().dossierId())
                .consultationId(in.document().consultationId())
                .typeDocument(in.document().typeDocument() == null ? TypeDocument.AUTRE : in.document().typeDocument())
                .titre(in.document().titre())
                .nomFichier(in.document().nomFichier())
                .cheminFichier(in.document().cheminFichier())
                .tailleOctets(in.document().tailleOctets() == null ? 0L : in.document().tailleOctets())
                .dateDocument(in.document().dateDocument() == null ? LocalDateTime.now() : in.document().dateDocument())
                .creePar(in.actor().username())
                .modifiePar(in.actor().username())
                .build();

        repo.create(d);
        return new LongResponseDTO(d.getId());
    }

    @Override
    public BooleanResponseDTO update(SaveDocumentMedicalRequestDTO in) {
        validateSave(in, true);

        DocumentMedical d = repo.findById(in.document().id());
        if (d == null) throw new NotFoundException("DocumentMedical introuvable id=" + in.document().id());

        d.setDossierId(in.document().dossierId());
        d.setConsultationId(in.document().consultationId());
        d.setTypeDocument(in.document().typeDocument() == null ? TypeDocument.AUTRE : in.document().typeDocument());
        d.setTitre(in.document().titre());
        d.setNomFichier(in.document().nomFichier());
        d.setCheminFichier(in.document().cheminFichier());
        d.setTailleOctets(in.document().tailleOctets() == null ? 0L : in.document().tailleOctets());
        d.setDateDocument(in.document().dateDocument() == null ? LocalDateTime.now() : in.document().dateDocument());
        d.setModifiePar(in.actor().username());

        repo.update(d);
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

    private void validateSave(SaveDocumentMedicalRequestDTO in, boolean mustHaveId) {
        if (in == null) throw new ValidationException("SaveDocumentMedicalRequestDTO null");
        if (in.document() == null) throw new ValidationException("document null");
        if (mustHaveId && in.document().id() == null) throw new ValidationException("id obligatoire pour update");

        if (in.document().dossierId() == null) throw new ValidationException("dossierId obligatoire");
        if (in.document().cheminFichier() == null || in.document().cheminFichier().isBlank())
            throw new ValidationException("cheminFichier obligatoire");

        if (in.actor() == null || in.actor().username() == null || in.actor().username().isBlank())
            throw new ValidationException("actor.username obligatoire");
    }

    private DocumentMedicalDTO toDTO(DocumentMedical d) {
        return new DocumentMedicalDTO(
                d.getId(), d.getDossierId(), d.getConsultationId(), d.getTypeDocument(),
                d.getTitre(), d.getNomFichier(), d.getCheminFichier(), d.getTailleOctets(), d.getDateDocument()
        );
    }
}
