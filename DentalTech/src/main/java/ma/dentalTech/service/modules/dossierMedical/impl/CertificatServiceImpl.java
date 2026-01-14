package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.common.exceptions.NotFoundException;
import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.FindCertificatsRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.SaveCertificatRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.*;
import ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepository;
import ma.dentalTech.repository.modules.dossierMedical.impl.CertificatRepositoryImpl;
import ma.dentalTech.service.modules.dossierMedical.api.CertificatService;
import ma.dentalTech.service.modules.dossierMedical.exception.*;

import java.time.LocalDate;
import java.util.List;

public class CertificatServiceImpl implements CertificatService {

    private final CertificatRepository repo;

    public CertificatServiceImpl() { this(new CertificatRepositoryImpl()); }
    public CertificatServiceImpl(CertificatRepository repo) { this.repo = repo; }

    @Override
    public CertificatDTO getById(IdRequestDTO in) {
        if (in == null || in.id() == null) throw new ValidationException("id obligatoire");
        Certificat c = repo.findById(in.id());
        if (c == null) throw new NotFoundException("Certificat introuvable id=" + in.id());
        return toDTO(c);
    }

    @Override
    public ListResponseDTO<CertificatDTO> findByDossierId(DossierIdRequestDTO in) {
        if (in == null || in.dossierId() == null) throw new ValidationException("dossierId obligatoire");
        List<CertificatDTO> out = repo.findByDossierId(in.dossierId()).stream().map(this::toDTO).toList();
        return new ListResponseDTO<>(out);
    }

    @Override
    public ListResponseDTO<CertificatDTO> find(FindCertificatsRequestDTO in) {
        if (in == null) throw new ValidationException("FindCertificatsRequestDTO null");

        List<Certificat> data;
        if (in.dossierId() != null) data = repo.findByDossierId(in.dossierId());
        else if (in.start() != null && in.end() != null) data = repo.findByDateBetween(in.start(), in.end());
        else if (in.noteKeyword() != null && !in.noteKeyword().isBlank()) data = repo.searchByNote(in.noteKeyword());
        else data = repo.findAll();

        return new ListResponseDTO<>(data.stream().map(this::toDTO).toList());
    }

    @Override
    public LongResponseDTO create(SaveCertificatRequestDTO in) {
        validateSave(in, false);

        Certificat c = Certificat.builder()
                .dossierId(in.certificat().dossierId())
                .dateDebut(in.certificat().dateDebut())
                .dateFin(in.certificat().dateFin())
                .duree(in.certificat().duree() == null ? 0 : in.certificat().duree())
                .noteMedecin(in.certificat().noteMedecin())
                .creePar(in.actor().username())
                .modifiePar(in.actor().username())
                .build();

        // règle simple: si dateFin null et duree > 0 => dateFin = dateDebut + duree
        if (c.getDateDebut() != null && c.getDateFin() == null && c.getDuree() > 0) {
            c.setDateFin(c.getDateDebut().plusDays(c.getDuree()));
        }

        repo.create(c);
        return new LongResponseDTO(c.getId());
    }

    @Override
    public BooleanResponseDTO update(SaveCertificatRequestDTO in) {
        validateSave(in, true);

        Certificat c = repo.findById(in.certificat().id());
        if (c == null) throw new NotFoundException("Certificat introuvable id=" + in.certificat().id());

        c.setDossierId(in.certificat().dossierId());
        c.setDateDebut(in.certificat().dateDebut());
        c.setDateFin(in.certificat().dateFin());
        c.setDuree(in.certificat().duree() == null ? 0 : in.certificat().duree());
        c.setNoteMedecin(in.certificat().noteMedecin());
        c.setModifiePar(in.actor().username());

        if (c.getDateDebut() != null && c.getDateFin() == null && c.getDuree() > 0) {
            c.setDateFin(c.getDateDebut().plusDays(c.getDuree()));
        }

        repo.update(c);
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

    private void validateSave(SaveCertificatRequestDTO in, boolean mustHaveId) {
        if (in == null) throw new ValidationException("SaveCertificatRequestDTO null");
        if (in.certificat() == null) throw new ValidationException("certificat null");
        if (mustHaveId && in.certificat().id() == null) throw new ValidationException("id obligatoire pour update");
        if (in.certificat().dossierId() == null) throw new ValidationException("dossierId obligatoire");

        LocalDate d1 = in.certificat().dateDebut();
        LocalDate d2 = in.certificat().dateFin();
        if (d1 != null && d2 != null && d2.isBefore(d1)) throw new ValidationException("dateFin < dateDebut");

        if (in.actor() == null || in.actor().username() == null || in.actor().username().isBlank())
            throw new ValidationException("actor.username obligatoire");
    }

    private CertificatDTO toDTO(Certificat c) {
        return new CertificatDTO(
                c.getId(), c.getDossierId(), c.getDateDebut(), c.getDateFin(), c.getDuree(), c.getNoteMedecin()
        );
    }
}
