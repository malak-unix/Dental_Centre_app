package ma.dentalTech.mvc.controllers.modules.dossierMedicale.impl;

import ma.dentalTech.common.exceptions.ControllerException;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ConsultationController;
import ma.dentalTech.mvc.dto.dossierMedicale.common.ActorDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.IdRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeInterventionDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationDetailDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationListRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.SaveConsultationRequestDTO;
import ma.dentalTech.repository.modules.dossierMedical.api.*;
import ma.dentalTech.repository.modules.dossierMedical.impl.*;
import ma.dentalTech.service.modules.dossierMedical.api.ConsultationService;
import ma.dentalTech.service.modules.dossierMedical.exception.ServiceException;

import java.util.List;

public class ConsultationControllerImpl implements ConsultationController {

    private final ConsultationService service;
    private final InterventionMedecinRepository interventionRepo;
    private final ActeRepository acteRepo;
    private final OrdonnanceRepository ordonnanceRepo;
    private final CertificatRepository certificatRepo;
    private final DossierMedicalRepository dossierRepo;

    public ConsultationControllerImpl(ConsultationService service) {
        this.service = service;
        this.interventionRepo = new InterventionMedecinRepositoryImpl();
        this.acteRepo = new ActeRepositoryImpl();
        this.ordonnanceRepo = new OrdonnanceRepositoryImpl();
        this.certificatRepo = new CertificatRepositoryImpl();
        this.dossierRepo = new DossierMedicalRepositoryImpl();
    }

    @Override
    public List<ConsultationListItemDTO> searchForList(ConsultationListRequestDTO in) {
        try {
            return service.searchForList(in).items();
        } catch (ServiceException e) {
            throw new ControllerException(e.getMessage(), e);
        } catch (RuntimeException e) {
            // repository peut lever RuntimeException("Erreur SQL...")
            throw new ControllerException("Erreur UI: chargement consultations", e);
        }
    }

    @Override
    public Long create(ConsultationDTO consultation, String username) {
        try {
            SaveConsultationRequestDTO request = new SaveConsultationRequestDTO(
                    consultation,
                    new ActorDTO(username)
            );
            return service.create(request).id();
        } catch (ServiceException e) {
            throw new ControllerException(e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new ControllerException("Erreur UI: création consultation", e);
        }
    }

    @Override
    public void delete(Long consultationId) {
        try {
            service.delete(new IdRequestDTO(consultationId));
        } catch (ServiceException e) {
            throw new ControllerException(e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new ControllerException("Erreur UI: suppression consultation", e);
        }
    }

    @Override
    public ConsultationDetailDTO getDetail(Long consultationId) {
        try {
            if (consultationId == null) throw new IllegalArgumentException("consultationId null");
            
            // Récupérer la consultation
            ConsultationDTO consultation = service.getById(new IdRequestDTO(consultationId));
            if (consultation == null) throw new RuntimeException("Consultation introuvable id=" + consultationId);
            
            // Récupérer le dossier pour avoir le patientId
            ma.dentalTech.entities.dossierMedical.DossierMedical dossier = dossierRepo.findById(consultation.dossierId());
            if (dossier == null) throw new RuntimeException("Dossier introuvable id=" + consultation.dossierId());
            
            // Récupérer le patient via repository
            ma.dentalTech.repository.modules.patient.api.PatientRepository patientRepo = 
                new ma.dentalTech.repository.modules.patient.impl.PatientRepositoryImpl();
            ma.dentalTech.entities.patient.Patient patient = patientRepo.findById(dossier.getPatientId());
            String patientNom = (patient != null) ? patient.getNom() + " " + patient.getPrenom() : "Inconnu";
            
            // Récupérer les interventions (actes)
            List<ma.dentalTech.entities.dossierMedical.InterventionMedecin> interventions = 
                interventionRepo.findByConsultationId(consultationId);
            
            List<ActeInterventionDTO> actesDTO = new java.util.ArrayList<>();
            double totalActes = 0.0;
            
            for (ma.dentalTech.entities.dossierMedical.InterventionMedecin inter : interventions) {
                ma.dentalTech.entities.dossierMedical.Acte acte = acteRepo.findById(inter.getActeId());
                ActeInterventionDTO dto = new ActeInterventionDTO();
                dto.setInterventionId(inter.getId());
                dto.setActeId(inter.getActeId());
                dto.setActeLibelle(acte != null ? acte.getLibelle() : "Acte inconnu");
                dto.setPrixPatient(inter.getPrixDePatient());
                dto.setNumDent(inter.getNumDent());
                actesDTO.add(dto);
                if (inter.getPrixDePatient() != null) {
                    totalActes += inter.getPrixDePatient();
                }
            }
            
            // Récupérer les ordonnances liées
            List<ma.dentalTech.entities.dossierMedical.Ordonnance> ordonnances = 
                ordonnanceRepo.findByConsultationId(consultationId);
            
            List<ConsultationDetailDTO.OrdonnanceSimpleDTO> ordonnancesDTO = ordonnances.stream()
                .map(o -> new ConsultationDetailDTO.OrdonnanceSimpleDTO(o.getId(), o.getDate()))
                .toList();
            
            // Récupérer les certificats liés (via dossier)
            List<ma.dentalTech.entities.dossierMedical.Certificat> certificats = 
                certificatRepo.findByDossierId(consultation.dossierId());
            
            List<ConsultationDetailDTO.CertificatSimpleDTO> certificatsDTO = certificats.stream()
                .map(c -> new ConsultationDetailDTO.CertificatSimpleDTO(
                    c.getId(), c.getDateDebut(), c.getDateFin(), c.getDuree()))
                .toList();
            
            // Construire le DTO de détail
            ConsultationDetailDTO detail = new ConsultationDetailDTO();
            detail.setConsultationId(consultation.id());
            detail.setDossierId(consultation.dossierId());
            detail.setPatientId(patient != null ? patient.getId() : null);
            detail.setPatientNomComplet(patientNom);
            // ConsultationDTO.date est LocalDate, on le convertit en LocalDateTime
            detail.setDateConsultation(consultation.date() != null ? 
                consultation.date().atStartOfDay() : null);
            detail.setStatut(consultation.statut());
            detail.setObservationMedecin(consultation.observationMedecin());
            detail.setActes(actesDTO);
            detail.setTotalActes(totalActes);
            detail.setOrdonnances(ordonnancesDTO);
            detail.setCertificats(certificatsDTO);
            
            return detail;
            
        } catch (ServiceException e) {
            throw new ControllerException(e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new ControllerException("Erreur UI: récupération détails consultation", e);
        }
    }
}
