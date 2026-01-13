package ma.dentalTech.mvc.dto.dossierMedicale.ordonnance;

import java.time.LocalDate;

/**
 * DTO pour les filtres de recherche d'ordonnances.
 */
public class OrdonnanceListRequestDTO {
    private Long medecinId; // Pour filtrer par médecin
    private String patientKeyword; // Recherche par nom/prénom patient
    private LocalDate dateFrom; // Filtre date (début période)
    private LocalDate dateTo; // Filtre date (fin période)

    // Getters and Setters
    public Long getMedecinId() {
        return medecinId;
    }

    public void setMedecinId(Long medecinId) {
        this.medecinId = medecinId;
    }

    public String getPatientKeyword() {
        return patientKeyword;
    }

    public void setPatientKeyword(String patientKeyword) {
        this.patientKeyword = patientKeyword;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }
}
