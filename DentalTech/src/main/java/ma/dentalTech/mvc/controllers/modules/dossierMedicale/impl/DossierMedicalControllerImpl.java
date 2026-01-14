package ma.dentalTech.mvc.controllers.modules.dossierMedicale.impl;

import ma.dentalTech.common.exceptions.ControllerException;
import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.entities.patient.Antecedents;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.DossierMedicalController;
import ma.dentalTech.mvc.dto.dossierMedicale.dossier.*;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.dentalTech.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl;
import ma.dentalTech.repository.modules.dossierMedical.impl.DossierMedicalRepositoryImpl;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.repository.modules.patient.impl.AntecedentRepositoryImpl;
import ma.dentalTech.repository.modules.patient.impl.PatientRepositoryImpl;
import ma.dentalTech.service.modules.dossierMedical.api.DossierMedicalService;
import ma.dentalTech.service.modules.dossierMedical.exception.ServiceException;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DossierMedicalControllerImpl implements DossierMedicalController {

    private final DossierMedicalRepository dossierRepo;
    private final PatientRepository patientRepo;
    private final ConsultationRepository consultationRepo;
    private final AntecedentRepository antecedentRepo;
    private final DossierMedicalService dossierService;

    public DossierMedicalControllerImpl() {
        this.dossierRepo = new DossierMedicalRepositoryImpl();
        this.patientRepo = new PatientRepositoryImpl();
        this.consultationRepo = new ConsultationRepositoryImpl();
        this.antecedentRepo = new AntecedentRepositoryImpl();
        this.dossierService = new ma.dentalTech.service.modules.dossierMedical.impl.DossierMedicalServiceImpl();
    }

    @Override
    public List<DossierListEnrichedItemDTO> searchForList(DossierListRequestDTO request) {
        try {
            // Récupérer les dossiers
            List<DossierMedical> dossiers;
            if (request.keyword() != null && !request.keyword().trim().isEmpty()) {
                dossiers = dossierRepo.searchByNotes(request.keyword());
            } else if (request.medecinId() != null) {
                dossiers = dossierRepo.findByMedecinId(request.medecinId());
            } else {
                int limit = (request.page() != null && request.page().limit() != null) ? request.page().limit() : 50;
                int offset = (request.page() != null && request.page().offset() != null) ? request.page().offset() : 0;
                dossiers = dossierRepo.findPage(limit, offset);
            }

            List<DossierListEnrichedItemDTO> result = new ArrayList<>();

            for (DossierMedical dossier : dossiers) {
                Patient patient = patientRepo.findById(dossier.getPatientId());
                if (patient == null) continue;

                DossierListEnrichedItemDTO item = new DossierListEnrichedItemDTO();
                item.setDossierId(dossier.getId());
                item.setPatientId(dossier.getPatientId());
                item.setMedecinId(dossier.getMedecinId());
                item.setPatientNomComplet(patient.getNom() + " " + patient.getPrenom());
                item.setPatientTelephone(patient.getTelephone());

                // Récupérer la dernière consultation
                List<Consultation> consultations = consultationRepo.findByDossierId(dossier.getId());
                if (!consultations.isEmpty()) {
                    Consultation derniereConsultation = consultations.stream()
                            .sorted((c1, c2) -> {
                                if (c1.getDate() == null) return 1;
                                if (c2.getDate() == null) return -1;
                                return c2.getDate().compareTo(c1.getDate());
                            })
                            .findFirst()
                            .orElse(null);

                    if (derniereConsultation != null) {
                        item.setDerniereConsultation(derniereConsultation.getDate());
                        item.setDerniereConsultationId(derniereConsultation.getId());
                    }
                }

                result.add(item);
            }

            return result;

        } catch (Exception e) {
            throw new ControllerException("Erreur lors de la recherche des dossiers médicaux", e);
        }
    }

    @Override
    public DossierDetailEnrichedDTO getDetail(Long dossierId) {
        try {
            DossierMedical dossier = dossierRepo.findById(dossierId);
            if (dossier == null) {
                throw new ControllerException("Dossier médical introuvable avec l'ID: " + dossierId);
            }

            Patient patient = patientRepo.findById(dossier.getPatientId());
            if (patient == null) {
                throw new ControllerException("Patient introuvable");
            }

            // Calculer l'âge
            Integer age = null;
            if (patient.getDateNaissance() != null) {
                age = Period.between(patient.getDateNaissance(), LocalDate.now()).getYears();
            }

            // Récupérer toutes les données via le service existant
            ma.dentalTech.mvc.dto.dossierMedicale.dossier.DossierDetailsDTO details = dossierService.details(
                    new ma.dentalTech.mvc.dto.dossierMedicale.common.IdRequestDTO(dossierId)
            );

            // Récupérer les antécédents
            List<Antecedents> antecedents = antecedentRepo.findByPatientId(patient.getId());
            List<AntecedentDTO> antecedentDTOs = antecedents.stream()
                    .map(a -> new AntecedentDTO(
                            a.getId(),
                            a.getPatientId(),
                            a.getNom(),
                            a.getCategorie(),
                            a.getNiveauDeRisque(),
                            a.getDescription()
                    ))
                    .collect(Collectors.toList());

            return new DossierDetailEnrichedDTO(
                    details.dossier(),
                    patient.getId(),
                    patient.getNom() + " " + patient.getPrenom(),
                    patient.getTelephone(),
                    patient.getEmail(),
                    age,
                    null, // Pas de groupe sanguin dans le schéma
                    details.consultations(),
                    details.ordonnances(),
                    details.certificats(),
                    details.documents(),
                    details.factures(),
                    details.situationFinanciere(),
                    antecedentDTOs
            );

        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de la récupération des détails: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ControllerException("Erreur lors de la récupération des détails: " + e.getMessage(), e);
        }
    }

    @Override
    public Long create(DossierDTO dossier, String username) {
        try {
            if (dossier == null) {
                throw new ControllerException("DossierDTO ne peut pas être null");
            }
            if (dossier.patientId() == null) {
                throw new ControllerException("Le patient est obligatoire pour créer un dossier médical");
            }
            if (username == null || username.trim().isEmpty()) {
                throw new ControllerException("Le nom d'utilisateur est obligatoire");
            }

            // Validation : notes ne doit pas dépasser 5000 caractères
            if (dossier.notes() != null && dossier.notes().length() > 5000) {
                throw new ControllerException("Les notes ne peuvent pas dépasser 5000 caractères");
            }

            SaveDossierRequestDTO request = new SaveDossierRequestDTO(
                    dossier,
                    new ma.dentalTech.mvc.dto.dossierMedicale.common.ActorDTO(username)
            );
            return dossierService.create(request).id();
        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de la création: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(DossierDTO dossier, String username) {
        try {
            if (dossier == null) {
                throw new ControllerException("DossierDTO ne peut pas être null");
            }
            if (dossier.id() == null) {
                throw new ControllerException("L'ID du dossier est obligatoire pour la modification");
            }
            if (dossier.patientId() == null) {
                throw new ControllerException("Le patient est obligatoire");
            }
            if (username == null || username.trim().isEmpty()) {
                throw new ControllerException("Le nom d'utilisateur est obligatoire");
            }

            // Validation : notes ne doit pas dépasser 5000 caractères
            if (dossier.notes() != null && dossier.notes().length() > 5000) {
                throw new ControllerException("Les notes ne peuvent pas dépasser 5000 caractères");
            }

            SaveDossierRequestDTO request = new SaveDossierRequestDTO(
                    dossier,
                    new ma.dentalTech.mvc.dto.dossierMedicale.common.ActorDTO(username)
            );
            dossierService.update(request);
        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de la mise à jour: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long dossierId, String username) {
        try {
            dossierService.delete(new ma.dentalTech.mvc.dto.dossierMedicale.common.IdRequestDTO(dossierId));
        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de la suppression: " + e.getMessage(), e);
        }
    }
}
