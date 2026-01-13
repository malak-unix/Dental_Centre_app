package ma.dentalTech.mvc.controllers.modules.dossierMedicale.impl;

import ma.dentalTech.common.exceptions.ControllerException;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.OrdonnanceController;
import ma.dentalTech.mvc.dto.dossierMedicale.common.ActorDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.IdRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceDetailDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceListRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.SaveOrdonnanceRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.prescription.PrescriptionDetailDTO;
import ma.dentalTech.repository.modules.dossierMedical.api.*;
import ma.dentalTech.repository.modules.dossierMedical.impl.*;
import ma.dentalTech.service.modules.dossierMedical.api.OrdonnanceService;
import ma.dentalTech.service.modules.dossierMedical.exception.ServiceException;

import java.util.List;

public class OrdonnanceControllerImpl implements OrdonnanceController {

    private final OrdonnanceRepository repository;
    private final OrdonnanceService service;
    private final PrescriptionRepository prescriptionRepo;
    private final MedicamentRepository medicamentRepo;
    private final DossierMedicalRepository dossierRepo;
    private final ConsultationRepository consultationRepo;

    public OrdonnanceControllerImpl() {
        this(new OrdonnanceRepositoryImpl(), null);
    }

    public OrdonnanceControllerImpl(OrdonnanceService service) {
        this(null, service);
    }

    public OrdonnanceControllerImpl(OrdonnanceRepository repository, OrdonnanceService service) {
        this.repository = repository;
        this.service = service != null ? service : new ma.dentalTech.service.modules.dossierMedical.impl.OrdonnanceServiceImpl();
        this.prescriptionRepo = new PrescriptionRepositoryImpl();
        this.medicamentRepo = new MedicamentRepositoryImpl();
        this.dossierRepo = new DossierMedicalRepositoryImpl();
        this.consultationRepo = new ConsultationRepositoryImpl();
    }

    @Override
    public List<OrdonnanceListItemDTO> searchForList(OrdonnanceListRequestDTO in) {
        try {
            if (in == null) throw new IllegalArgumentException("OrdonnanceListRequestDTO null");
            if (repository == null) throw new IllegalStateException("repository null");
            return repository.searchForList(in);
        } catch (Exception e) {
            throw new ControllerException("Erreur lors de la recherche d'ordonnances", e);
        }
    }

    @Override
    public Long create(OrdonnanceDTO ordonnance, String username) {
        try {
            if (ordonnance == null) throw new IllegalArgumentException("OrdonnanceDTO null");
            if (username == null || username.isBlank()) throw new IllegalArgumentException("username obligatoire");

            SaveOrdonnanceRequestDTO req = new SaveOrdonnanceRequestDTO(
                    ordonnance,
                    new ActorDTO(username)
            );
            return service.create(req).id();
        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de la création de l'ordonnance", e);
        }
    }

    @Override
    public void update(OrdonnanceDTO ordonnance, String username) {
        try {
            if (ordonnance == null) throw new IllegalArgumentException("OrdonnanceDTO null");
            if (ordonnance.id() == null) throw new IllegalArgumentException("id obligatoire pour update");
            if (username == null || username.isBlank()) throw new IllegalArgumentException("username obligatoire");

            SaveOrdonnanceRequestDTO req = new SaveOrdonnanceRequestDTO(
                    ordonnance,
                    new ActorDTO(username)
            );
            service.update(req);
        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de la mise à jour de l'ordonnance", e);
        }
    }

    @Override
    public void delete(Long ordonnanceId) {
        try {
            if (ordonnanceId == null) throw new IllegalArgumentException("ordonnanceId null");
            service.delete(new IdRequestDTO(ordonnanceId));
        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de la suppression de l'ordonnance", e);
        }
    }

    @Override
    public OrdonnanceDTO getById(Long ordonnanceId) {
        try {
            if (ordonnanceId == null) throw new IllegalArgumentException("ordonnanceId null");
            return service.getById(new IdRequestDTO(ordonnanceId));
        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de la récupération de l'ordonnance", e);
        }
    }

    @Override
    public OrdonnanceDetailDTO getDetail(Long ordonnanceId) {
        try {
            if (ordonnanceId == null) throw new IllegalArgumentException("ordonnanceId null");
            
            // Récupérer l'ordonnance
            OrdonnanceDTO ordonnance = service.getById(new IdRequestDTO(ordonnanceId));
            if (ordonnance == null) throw new RuntimeException("Ordonnance introuvable id=" + ordonnanceId);
            
            // Récupérer le dossier pour avoir le patient
            ma.dentalTech.entities.dossierMedical.DossierMedical dossier = dossierRepo.findById(ordonnance.dossierId());
            if (dossier == null) throw new RuntimeException("Dossier introuvable id=" + ordonnance.dossierId());
            
            // Récupérer le patient
            ma.dentalTech.repository.modules.patient.api.PatientRepository patientRepo = 
                new ma.dentalTech.repository.modules.patient.impl.PatientRepositoryImpl();
            ma.dentalTech.entities.patient.Patient patient = patientRepo.findById(dossier.getPatientId());
            String patientNom = (patient != null) ? patient.getNom() + " " + patient.getPrenom() : "Inconnu";
            
            // Récupérer la consultation
            String consultationLibelle = "Consultation";
            if (ordonnance.consultationId() != null) {
                ma.dentalTech.entities.dossierMedical.Consultation consultation = 
                    consultationRepo.findById(ordonnance.consultationId());
                if (consultation != null && consultation.getDate() != null) {
                    consultationLibelle = "Consultation du " + consultation.getDate().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
            }
            
            // Récupérer le médecin (via dossier)
            String medecinNom = "Médecin";
            if (dossier.getMedecinId() != null) {
                // TODO: récupérer le nom du médecin depuis le repository
                medecinNom = "Médecin #" + dossier.getMedecinId();
            }
            
            // Récupérer les prescriptions
            List<ma.dentalTech.entities.dossierMedical.Prescription> prescriptions = 
                prescriptionRepo.findByOrdonnanceId(ordonnanceId);
            
            List<PrescriptionDetailDTO> prescriptionsDTO = new java.util.ArrayList<>();
            for (ma.dentalTech.entities.dossierMedical.Prescription p : prescriptions) {
                ma.dentalTech.entities.dossierMedical.Medicament medicament = 
                    medicamentRepo.findById(p.getMedicamentId());
                
                PrescriptionDetailDTO dto = new PrescriptionDetailDTO();
                dto.setPrescriptionId(p.getId());
                dto.setMedicamentId(p.getMedicamentId());
                dto.setMedicamentNom(medicament != null ? medicament.getNom() : "Médicament inconnu");
                dto.setMedicamentForme(medicament != null && medicament.getForme() != null ? 
                    medicament.getForme().name() : "");
                dto.setQuantite(p.getQuantite());
                dto.setFrequence(p.getFrequence());
                dto.setDureeEnJours(p.getDureeEnJours());
                prescriptionsDTO.add(dto);
            }
            
            // Construire le DTO de détail
            OrdonnanceDetailDTO detail = new OrdonnanceDetailDTO();
            detail.setOrdonnanceId(ordonnance.id());
            detail.setDossierId(ordonnance.dossierId());
            detail.setConsultationId(ordonnance.consultationId());
            detail.setDate(ordonnance.date());
            detail.setPatientId(patient != null ? patient.getId() : null);
            detail.setPatientNomComplet(patientNom);
            detail.setConsultationLibelle(consultationLibelle);
            detail.setMedecinNom(medecinNom);
            detail.setPrescriptions(prescriptionsDTO);
            
            return detail;
            
        } catch (ServiceException e) {
            throw new ControllerException(e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new ControllerException("Erreur UI: récupération détails ordonnance", e);
        }
    }
}
