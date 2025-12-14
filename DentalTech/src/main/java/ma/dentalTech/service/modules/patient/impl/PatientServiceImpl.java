package ma.dentalTech.service.modules.patient.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.dto.PatientDTO;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.modules.patient.api.PatientService;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation de base du service Patient.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientServiceImpl implements PatientService {

    private PatientRepository repository;

    @Override
    public List<PatientDTO> getTodayPatientsAsDTO() {
        LocalDate today = LocalDate.now();

        return repository.findAll().stream()
                .filter(p -> p.getDateCreation() != null
                        && p.getDateCreation().toLocalDate().equals(today))
                .sorted(Comparator.comparing(Patient::getDateCreation).reversed())
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private PatientDTO toDto(Patient p) {
        String nom = p.getNom() != null ? p.getNom().trim() : "";
        String prenom = p.getPrenom() != null ? p.getPrenom().trim() : "";
        String nomComplet = (nom + " " + prenom).trim();

        int age = computeAge(p.getDateNaissance());
        String dateFormatee = formatDate(p.getDateCreation());

        return PatientDTO.builder()
                .nomComplet(nomComplet)
                .age(age)
                .dateCreationFormatee(dateFormatee)
                .build();
    }

    private int computeAge(LocalDate dateNaissance) {
        if (dateNaissance == null) {
            return 0;
        }
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    private String formatDate(java.time.LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }
}
