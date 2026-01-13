package ma.dentalTech.mvc.controllers.modules.dossierMedicale.impl;

import ma.dentalTech.common.exceptions.ControllerException;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.CertificatController;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatDetailDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatListRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.SaveCertificatRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.ActorDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.IdRequestDTO;
import ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepository;
import ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.dentalTech.repository.modules.dossierMedical.impl.CertificatRepositoryImpl;
import ma.dentalTech.repository.modules.dossierMedical.impl.DossierMedicalRepositoryImpl;
import ma.dentalTech.service.modules.dossierMedical.api.CertificatService;
import ma.dentalTech.service.modules.dossierMedical.exception.ServiceException;

import java.util.List;

public class CertificatControllerImpl implements CertificatController {

    private final CertificatRepository repository;
    private final CertificatService service;
    private final DossierMedicalRepository dossierRepo;

    public CertificatControllerImpl() {
        this(new CertificatRepositoryImpl(), null);
    }

    public CertificatControllerImpl(CertificatService service) {
        this(null, service);
    }

    public CertificatControllerImpl(CertificatRepository repository, CertificatService service) {
        this.repository = repository;
        this.service = service != null ? service : new ma.dentalTech.service.modules.dossierMedical.impl.CertificatServiceImpl();
        this.dossierRepo = new DossierMedicalRepositoryImpl();
    }

    @Override
    public List<CertificatListItemDTO> searchForList(CertificatListRequestDTO in) {
        try {
            if (in == null) throw new IllegalArgumentException("CertificatListRequestDTO null");
            if (repository == null) throw new IllegalStateException("repository null");
            return repository.searchForList(in);
        } catch (Exception e) {
            throw new ControllerException("Erreur lors de la recherche de certificats", e);
        }
    }

    @Override
    public Long create(CertificatDTO certificat, String username) {
        try {
            if (certificat == null) throw new IllegalArgumentException("CertificatDTO null");
            if (username == null || username.isBlank()) throw new IllegalArgumentException("username obligatoire");

            SaveCertificatRequestDTO req = new SaveCertificatRequestDTO(
                    certificat,
                    new ActorDTO(username)
            );
            return service.create(req).id();
        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de la création du certificat", e);
        }
    }

    @Override
    public void update(CertificatDTO certificat, String username) {
        try {
            if (certificat == null) throw new IllegalArgumentException("CertificatDTO null");
            if (certificat.id() == null) throw new IllegalArgumentException("id obligatoire pour update");
            if (username == null || username.isBlank()) throw new IllegalArgumentException("username obligatoire");

            SaveCertificatRequestDTO req = new SaveCertificatRequestDTO(
                    certificat,
                    new ActorDTO(username)
            );
            service.update(req);
        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de la mise à jour du certificat", e);
        }
    }

    @Override
    public void delete(Long certificatId) {
        try {
            if (certificatId == null) throw new IllegalArgumentException("certificatId null");
            service.delete(new IdRequestDTO(certificatId));
        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de la suppression du certificat", e);
        }
    }

    @Override
    public CertificatDTO getById(Long certificatId) {
        try {
            if (certificatId == null) throw new IllegalArgumentException("certificatId null");
            return service.getById(new IdRequestDTO(certificatId));
        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de la récupération du certificat", e);
        }
    }

    @Override
    public CertificatDetailDTO getDetail(Long certificatId) {
        try {
            if (certificatId == null) throw new IllegalArgumentException("certificatId null");
            
            // Récupérer le certificat
            CertificatDTO certificat = service.getById(new IdRequestDTO(certificatId));
            if (certificat == null) throw new RuntimeException("Certificat introuvable id=" + certificatId);
            
            // Récupérer le dossier pour avoir le patient
            ma.dentalTech.entities.dossierMedical.DossierMedical dossier = dossierRepo.findById(certificat.dossierId());
            if (dossier == null) throw new RuntimeException("Dossier introuvable id=" + certificat.dossierId());
            
            // Récupérer le patient
            ma.dentalTech.repository.modules.patient.api.PatientRepository patientRepo = 
                new ma.dentalTech.repository.modules.patient.impl.PatientRepositoryImpl();
            ma.dentalTech.entities.patient.Patient patient = patientRepo.findById(dossier.getPatientId());
            String patientNom = (patient != null) ? patient.getNom() + " " + patient.getPrenom() : "Inconnu";
            
            // Récupérer le médecin (via dossier)
            String medecinNom = "Médecin";
            if (dossier.getMedecinId() != null) {
                // TODO: récupérer le nom du médecin depuis le repository
                medecinNom = "Médecin #" + dossier.getMedecinId();
            }
            
            // Construire le DTO de détail
            CertificatDetailDTO detail = new CertificatDetailDTO();
            detail.setCertificatId(certificat.id());
            detail.setDossierId(certificat.dossierId());
            detail.setPatientId(patient != null ? patient.getId() : null);
            detail.setPatientNomComplet(patientNom);
            detail.setDateDebut(certificat.dateDebut());
            detail.setDateFin(certificat.dateFin());
            detail.setDuree(certificat.duree());
            detail.setNoteMedecin(certificat.noteMedecin());
            detail.setMedecinNom(medecinNom);
            
            return detail;
            
        } catch (ServiceException e) {
            throw new ControllerException(e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new ControllerException("Erreur UI: récupération détails certificat", e);
        }
    }
}
