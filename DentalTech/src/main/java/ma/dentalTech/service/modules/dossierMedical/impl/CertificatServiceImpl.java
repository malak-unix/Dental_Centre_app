package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepository;
import ma.dentalTech.service.modules.dossierMedical.api.CertificatService;

import java.time.LocalDate;
import java.util.List;

public class CertificatServiceImpl implements CertificatService {

    private final CertificatRepository certificatRepository;

    public CertificatServiceImpl(CertificatRepository certificatRepository) {
        this.certificatRepository = certificatRepository;
    }

    @Override
    public List<Certificat> getAll() {
        return certificatRepository.findAll();
    }

    @Override
    public Certificat getById(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");
        return certificatRepository.findById(id);
    }

    @Override
    public void create(Certificat cert) {
        if (cert == null) throw new IllegalArgumentException("Certificat null");
        if (cert.getDossierId() == null) throw new IllegalArgumentException("dossierId obligatoire");
        if (cert.getDuree() < 0) cert.setDuree(0);

        certificatRepository.create(cert);
    }

    @Override
    public void update(Certificat cert) {
        if (cert == null) throw new IllegalArgumentException("Certificat null");
        if (cert.getId() == null) throw new IllegalArgumentException("id obligatoire");
        if (cert.getDossierId() == null) throw new IllegalArgumentException("dossierId obligatoire");
        if (cert.getDuree() < 0) cert.setDuree(0);

        certificatRepository.update(cert);
    }

    @Override
    public void delete(Certificat cert) {
        certificatRepository.delete(cert);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");
        certificatRepository.deleteById(id);
    }

    @Override
    public List<Certificat> getByDossierId(Long dossierId) {
        if (dossierId == null) throw new IllegalArgumentException("dossierId obligatoire");
        return certificatRepository.findByDossierId(dossierId);
    }

    @Override
    public List<Certificat> getByDateDebut(LocalDate dateDebut) {
        if (dateDebut == null) throw new IllegalArgumentException("dateDebut obligatoire");
        return certificatRepository.findByDateDebut(dateDebut);
    }

    @Override
    public List<Certificat> getByDateBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new IllegalArgumentException("start/end obligatoires");
        if (end.isBefore(start)) throw new IllegalArgumentException("end doit être >= start");
        return certificatRepository.findByDateBetween(start, end);
    }

    @Override
    public List<Certificat> searchByNote(String keyword) {
        return certificatRepository.searchByNote(keyword == null ? "" : keyword);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;
        return certificatRepository.existsById(id);
    }

    @Override
    public long count() {
        return certificatRepository.count();
    }

    @Override
    public List<Certificat> findPage(int limit, int offset) {
        if (limit <= 0) throw new IllegalArgumentException("limit > 0");
        if (offset < 0) throw new IllegalArgumentException("offset >= 0");
        return certificatRepository.findPage(limit, offset);
    }
}
