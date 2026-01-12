package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.common.exceptions.NotFoundException;
import ma.dentalTech.entities.cabinet.Facture;
import ma.dentalTech.entities.cabinet.SituationFinanciere;
import ma.dentalTech.entities.dossierMedical.*;
import ma.dentalTech.mvc.dto.dossierMedicale.common.BooleanResponseDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.IdRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.LongResponseDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.PageResponseDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.dossier.*;
import ma.dentalTech.mvc.dto.dossierMedicale.readonly.FactureDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.readonly.SituationFinanciereDTO;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;
import ma.dentalTech.repository.modules.caisse.impl.FactureRepositoryImpl;
import ma.dentalTech.repository.modules.caisse.impl.SituationFinanciereRepositoryImpl;

import ma.dentalTech.repository.modules.dossierMedical.api.*;
import ma.dentalTech.repository.modules.dossierMedical.impl.*;

import ma.dentalTech.service.modules.dossierMedical.api.DossierMedicalService;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.document.DocumentMedicalDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceDTO;
import ma.dentalTech.service.modules.dossierMedical.exception.*;

import java.util.List;

public class DossierMedicalServiceImpl implements DossierMedicalService {

    private final DossierMedicalRepository dossierRepo;
    private final ConsultationRepository consultationRepo;
    private final DocumentMedicalRepository docRepo;
    private final OrdonnanceRepository ordonnanceRepo;
    private final CertificatRepository certificatRepo;
    private final FactureRepository factureRepo;
    private final SituationFinanciereRepository sfRepo;

    public DossierMedicalServiceImpl() {
        this(new DossierMedicalRepositoryImpl(),
                new ConsultationRepositoryImpl(),
                new DocumentMedicalRepositoryImpl(),
                new OrdonnanceRepositoryImpl(),
                new CertificatRepositoryImpl(),
                new FactureRepositoryImpl(),
                new SituationFinanciereRepositoryImpl());
    }

    public DossierMedicalServiceImpl(
            DossierMedicalRepository dossierRepo,
            ConsultationRepository consultationRepo,
            DocumentMedicalRepository docRepo,
            OrdonnanceRepository ordonnanceRepo,
            CertificatRepository certificatRepo,
            FactureRepository factureRepo,
            SituationFinanciereRepository sfRepo
    ) {
        this.dossierRepo = dossierRepo;
        this.consultationRepo = consultationRepo;
        this.docRepo = docRepo;
        this.ordonnanceRepo = ordonnanceRepo;
        this.certificatRepo = certificatRepo;
        this.factureRepo = factureRepo;
        this.sfRepo = sfRepo;
    }

    @Override
    public PageResponseDTO<DossierListItemDTO> list(DossierListRequestDTO in) {
        if (in == null) throw new ValidationException("DossierListRequestDTO null");

        int limit = (in.page() == null || in.page().limit() == null || in.page().limit() <= 0) ? 50 : in.page().limit();
        int offset = (in.page() == null || in.page().offset() == null || in.page().offset() < 0) ? 0 : in.page().offset();

        List<DossierMedical> data;
        if (in.keyword() != null && !in.keyword().isBlank()) data = dossierRepo.searchByNotes(in.keyword());
        else if (in.medecinId() != null) data = dossierRepo.findByMedecinId(in.medecinId());
        else data = dossierRepo.findPage(limit, offset);

        List<DossierListItemDTO> items = data.stream()
                .map(d -> new DossierListItemDTO(d.getId(), d.getPatientId(), d.getMedecinId(), preview(d.getNotes(), 60)))
                .toList();

        return new PageResponseDTO<>(items, dossierRepo.count());
    }

    @Override
    public DossierDetailsDTO details(IdRequestDTO in) {
        if (in == null || in.id() == null) throw new ValidationException("dossierId obligatoire");

        DossierMedical d = dossierRepo.findById(in.id());
        if (d == null) throw new NotFoundException("Dossier introuvable id=" + in.id());

        DossierDTO dossierDTO = toDTO(d);

        List<ConsultationDTO> consultations = consultationRepo.findByDossierId(d.getId()).stream().map(this::toDTO).toList();
        List<DocumentMedicalDTO> documents = docRepo.findByDossierId(d.getId()).stream().map(this::toDTO).toList();
        List<OrdonnanceDTO> ordonnances = ordonnanceRepo.findByDossierId(d.getId()).stream().map(this::toDTO).toList();
        List<CertificatDTO> certificats = certificatRepo.findByDossierId(d.getId()).stream().map(this::toDTO).toList();

        List<FactureDTO> factures = factureRepo.findByDossierId(d.getId()).stream().map(this::toDTO).toList();

        SituationFinanciere sf = sfRepo.findByDossierId(d.getId());
        SituationFinanciereDTO sfDTO = (sf == null) ? null : new SituationFinanciereDTO(
                sf.getId(), sf.getDossierId(), sf.getTotalDesActes(), sf.getTotalPaye(), sf.getCredit(),
                sf.getStatut() == null ? null : sf.getStatut().name()
        );

        return new DossierDetailsDTO(dossierDTO, consultations, documents, ordonnances, certificats, factures, sfDTO);
    }

    @Override
    public DossierDTO getById(IdRequestDTO in) {
        if (in == null || in.id() == null) throw new ValidationException("id obligatoire");
        DossierMedical d = dossierRepo.findById(in.id());
        if (d == null) throw new NotFoundException("Dossier introuvable id=" + in.id());
        return toDTO(d);
    }

    @Override
    public LongResponseDTO create(SaveDossierRequestDTO in) {
        validateSave(in, false);

        DossierMedical d = DossierMedical.builder()
                .patientId(in.dossier().patientId())
                .medecinId(in.dossier().medecinId())
                .notes(in.dossier().notes())
                .creePar(in.actor().username())
                .modifiePar(in.actor().username())
                .build();

        dossierRepo.create(d);
        return new LongResponseDTO(d.getId());
    }

    @Override
    public BooleanResponseDTO update(SaveDossierRequestDTO in) {
        validateSave(in, true);

        DossierMedical d = dossierRepo.findById(in.dossier().id());
        if (d == null) throw new NotFoundException("Dossier introuvable id=" + in.dossier().id());

        d.setPatientId(in.dossier().patientId());
        d.setMedecinId(in.dossier().medecinId());
        d.setNotes(in.dossier().notes());
        d.setModifiePar(in.actor().username());

        dossierRepo.update(d);
        return new BooleanResponseDTO(true);
    }

    @Override
    public BooleanResponseDTO delete(IdRequestDTO in) {
        if (in == null || in.id() == null) throw new ValidationException("id obligatoire");
        dossierRepo.deleteById(in.id());
        return new BooleanResponseDTO(true);
    }

    private void validateSave(SaveDossierRequestDTO in, boolean mustHaveId) {
        if (in == null) throw new ValidationException("SaveDossierRequestDTO null");
        if (in.dossier() == null) throw new ValidationException("dossier null");
        if (mustHaveId && in.dossier().id() == null) throw new ValidationException("id obligatoire pour update");
        if (in.dossier().patientId() == null) throw new ValidationException("patientId obligatoire");
        if (in.actor() == null || in.actor().username() == null || in.actor().username().isBlank())
            throw new ValidationException("actor.username obligatoire");
    }

    private DossierDTO toDTO(DossierMedical d) {
        return new DossierDTO(d.getId(), d.getPatientId(), d.getMedecinId(), d.getNotes());
    }

    private ConsultationDTO toDTO(Consultation c) {
        return new ConsultationDTO(c.getId(), c.getDossierId(), c.getDate(), c.getStatus(), c.getObservationMedecin());
    }

    private DocumentMedicalDTO toDTO(DocumentMedical d) {
        return new DocumentMedicalDTO(d.getId(), d.getDossierId(), d.getConsultationId(), d.getTypeDocument(),
                d.getTitre(), d.getNomFichier(), d.getCheminFichier(), d.getTailleOctets(), d.getDateDocument());
    }

    private OrdonnanceDTO toDTO(Ordonnance o) {
        return new OrdonnanceDTO(o.getId(), o.getDossierId(), o.getConsultationId(), o.getDate());
    }

    private CertificatDTO toDTO(Certificat c) {
        return new CertificatDTO(c.getId(), c.getDossierId(), c.getDateDebut(), c.getDateFin(), c.getDuree(), c.getNoteMedecin());
    }

    private FactureDTO toDTO(Facture f) {
        return new FactureDTO(f.getId(), f.getConsultationId(), f.getDateFacture(), f.getTotalFacture(), f.getTotalPaye(), f.getReste(), f.getStatut());
    }

    private static String preview(String s, int max) {
        if (s == null) return null;
        String t = s.trim();
        return (t.length() <= max) ? t : t.substring(0, max) + "...";
    }
}
