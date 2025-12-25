package ma.dentalTech.service.modules.patient.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.common.exceptions.ValidationException;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.dto.patient.PatientFormDto;
import ma.dentalTech.mvc.dto.patient.PatientListDto;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.modules.patient.api.PatientAppService;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class PatientAppServiceImpl implements PatientAppService {

    private final PatientRepository patientRepo;

    public PatientAppServiceImpl(PatientRepository patientRepo) {
        this.patientRepo = patientRepo;
    }

    @Override
    public List<PatientListDto> listerPatients() throws ServiceException {
        try {
            return patientRepo.findAll().stream().map(this::toListDto).toList();
        } catch (Exception e) {
            throw new ServiceException("Erreur listing patients", e);
        }
    }

    @Override
    public PatientFormDto consulterPatient(Long id) throws ValidationException, ServiceException {
        if (id == null) throw new ValidationException("id patient obligatoire");
        try {
            Optional<Patient> opt = patientRepo.findById(id);
            Patient p = opt.orElseThrow(() -> new ValidationException("Patient introuvable"));
            return toFormDto(p);
        } catch (ValidationException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ServiceException("Erreur consultation patient", e);
        }
    }

    @Override
    public PatientFormDto creerPatient(PatientFormDto dto) throws ValidationException, ServiceException {
        validatePatient(dto);

        try {
            if (dto.getTelephone() != null && !dto.getTelephone().isBlank()) {
                Optional<Patient> exist = patientRepo.findByTelephone(dto.getTelephone());
                if (exist.isPresent()) throw new ValidationException("Téléphone déjà utilisé");
            }
        } catch (ValidationException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ServiceException("Erreur vérification téléphone", e);
        }

        Patient p = toEntity(dto);

        try {
            patientRepo.create(p);
            return toFormDto(p);
        } catch (Exception e) {
            throw new ServiceException("Erreur création patient", e);
        }
    }

    @Override
    public PatientFormDto modifierPatient(Long id, PatientFormDto dto) throws ValidationException, ServiceException {
        if (id == null) throw new ValidationException("id patient obligatoire");
        validatePatient(dto);

        try {
            Optional<Patient> existingOpt = patientRepo.findById(id);
            if (existingOpt.isEmpty()) throw new ValidationException("Patient introuvable");

            // Vérif téléphone unique (si changé)
            if (dto.getTelephone() != null && !dto.getTelephone().isBlank()) {
                Optional<Patient> existTel = patientRepo.findByTelephone(dto.getTelephone());
                if (existTel.isPresent() && !existTel.get().getId().equals(id)) {
                    throw new ValidationException("Téléphone déjà utilisé");
                }
            }

            Patient updated = toEntity(dto);
            updated.setId(id);

            // pas de baseEntityId (n'existe pas dans ton Patient)

            patientRepo.update(updated);
            return toFormDto(updated);

        } catch (ValidationException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ServiceException("Erreur modification patient", e);
        }
    }

    @Override
    public void supprimerPatient(Long id) throws ValidationException, ServiceException {
        if (id == null) throw new ValidationException("id patient obligatoire");
        try {
            patientRepo.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Erreur suppression patient", e);
        }
    }

    @Override
    public List<PatientListDto> rechercherParNom(String nom) throws ValidationException, ServiceException {
        if (nom == null || nom.isBlank()) throw new ValidationException("nom obligatoire");
        try {
            String needle = normalize(nom);
            return patientRepo.findAll().stream()
                    .filter(p -> normalize(p.getNom()).contains(needle))
                    .map(this::toListDto)
                    .toList();
        } catch (Exception e) {
            throw new ServiceException("Erreur recherche patient par nom", e);
        }
    }

    @Override
    public PatientFormDto rechercherParTelephone(String telephone) throws ValidationException, ServiceException {
        if (telephone == null || telephone.isBlank()) throw new ValidationException("telephone obligatoire");
        try {
            Optional<Patient> opt = patientRepo.findByTelephone(telephone);
            Patient p = opt.orElseThrow(() -> new ValidationException("Aucun patient trouvé pour ce téléphone"));
            return toFormDto(p);
        } catch (ValidationException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ServiceException("Erreur recherche patient par téléphone", e);
        }
    }

    // =========================
    // Validation + mapping
    // =========================
    private void validatePatient(PatientFormDto dto) throws ValidationException {
        if (dto == null) throw new ValidationException("DTO patient null");
        if (dto.getNom() == null || dto.getNom().isBlank()) throw new ValidationException("Nom obligatoire");
        if (dto.getPrenom() == null || dto.getPrenom().isBlank()) throw new ValidationException("Prénom obligatoire");
        if (dto.getTelephone() == null || dto.getTelephone().isBlank()) throw new ValidationException("Téléphone obligatoire");
    }

    private Patient toEntity(PatientFormDto dto) {
        return Patient.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .dateNaissance(dto.getDateNaissance())
                .sexe(dto.getSexe())
                .telephone(dto.getTelephone())
                .adresse(dto.getAdresse())
                .assurance(dto.getAssurance())
                .build();
    }

    private PatientFormDto toFormDto(Patient p) {
        return PatientFormDto.builder()
                .id(p.getId())
                .nom(p.getNom())
                .prenom(p.getPrenom())
                .dateNaissance(p.getDateNaissance())
                .sexe(p.getSexe())
                .telephone(p.getTelephone())
                .adresse(p.getAdresse())
                .assurance(p.getAssurance())
                .build();
    }

    private PatientListDto toListDto(Patient p) {
        String nomComplet = (p.getNom() != null ? p.getNom() : "") + " " + (p.getPrenom() != null ? p.getPrenom() : "");
        return PatientListDto.builder()
                .id(p.getId())
                .nomComplet(nomComplet.trim())
                .telephone(p.getTelephone())
                .build();
    }

    private String normalize(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase(Locale.ROOT);
    }
}
