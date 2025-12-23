package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.dentalTech.service.modules.dossierMedical.api.ConsultationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;

    public ConsultationServiceImpl(ConsultationRepository consultationRepository) {
        this.consultationRepository = consultationRepository;
    }

    @Override
    public List<Consultation> getAll() {
        return consultationRepository.findAll();
    }

    @Override
    public Consultation getById(Long id) {
        if (id == null) return null;
        return consultationRepository.findById(id);
    }

    @Override
    public void create(Consultation c) {
        if (c == null) throw new IllegalArgumentException("Consultation null");
        if (c.getDossierId() == null) throw new IllegalArgumentException("dossierId obligatoire");
        if (c.getDate() == null) c.setDate(LocalDate.now());


        consultationRepository.create(c);
    }

    @Override
    public void update(Consultation c) {
        if (c == null || c.getId() == null) throw new IllegalArgumentException("Consultation id obligatoire");
        if (c.getDossierId() == null) throw new IllegalArgumentException("dossierId obligatoire");

        if (c.getStatus() == StatutConsultation.PLANIFIE) {
            c.setStatus(StatutConsultation.PLANIFIE);
        }

        consultationRepository.update(c);
    }

    @Override
    public void delete(Consultation c) {
        consultationRepository.delete(c);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;
        consultationRepository.deleteById(id);
    }

    @Override
    public List<Consultation> getByDossierId(Long dossierId) {
        if (dossierId == null) return List.of();
        return consultationRepository.findByDossierId(dossierId);
    }

    @Override
    public List<Consultation> getByDate(LocalDate date) {
        if (date == null) return List.of();
        return consultationRepository.findByDate(date);
    }

    @Override
    public List<Consultation> getByDateBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) return List.of();
        return consultationRepository.findByDateBetween(start, end);
    }

    @Override
    public List<Consultation> getByStatut(StatutConsultation statut) {
        if (statut == null) return List.of();
        if (statut == StatutConsultation.PLANIFIE) statut = StatutConsultation.PLANIFIE;
        return consultationRepository.findByStatut(statut);
    }

    @Override
    public List<Consultation> searchByObservation(String keyword) {
        return consultationRepository.searchByObservation(keyword == null ? "" : keyword);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;
        return consultationRepository.existsById(id);
    }

    @Override
    public long count() {
        return consultationRepository.count();
    }

    @Override
    public List<Consultation> findPage(int limit, int offset) {
        if (limit <= 0) limit = 20;
        if (offset < 0) offset = 0;
        return consultationRepository.findPage(limit, offset);
    }

    @Override
    public Integer countTermineesPourMedecin(Long medecinId, LocalDateTime start, LocalDateTime end) {
        if (medecinId == null || start == null || end == null) return 0;
        return consultationRepository.countTermineesPourMedecin(medecinId, start, end);
    }

    @Override
    public Integer countEnCoursPourMedecin(Long medecinId, LocalDateTime start, LocalDateTime end) {
        if (medecinId == null || start == null || end == null) return 0;
        return consultationRepository.countEnCoursPourMedecin(medecinId, start, end);
    }
}
