package ma.dentalTech.mvc.dto.dossierMedicale.certificat;

import java.time.LocalDate;

/**
 * DTO pour les filtres de recherche de certificats.
 */
public class CertificatListRequestDTO {
    private Long medecinId; // Pour filtrer par médecin
    private String patientKeyword; // Recherche par nom/prénom patient
    private LocalDate dateDebutFrom; // Filtre date début (début période)
    private LocalDate dateDebutTo; // Filtre date début (fin période)
    private LocalDate dateFinFrom; // Filtre date fin (début période)
    private LocalDate dateFinTo; // Filtre date fin (fin période)
    private String noteKeyword; // Recherche dans les notes

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

    public LocalDate getDateDebutFrom() {
        return dateDebutFrom;
    }

    public void setDateDebutFrom(LocalDate dateDebutFrom) {
        this.dateDebutFrom = dateDebutFrom;
    }

    public LocalDate getDateDebutTo() {
        return dateDebutTo;
    }

    public void setDateDebutTo(LocalDate dateDebutTo) {
        this.dateDebutTo = dateDebutTo;
    }

    public LocalDate getDateFinFrom() {
        return dateFinFrom;
    }

    public void setDateFinFrom(LocalDate dateFinFrom) {
        this.dateFinFrom = dateFinFrom;
    }

    public LocalDate getDateFinTo() {
        return dateFinTo;
    }

    public void setDateFinTo(LocalDate dateFinTo) {
        this.dateFinTo = dateFinTo;
    }

    public String getNoteKeyword() {
        return noteKeyword;
    }

    public void setNoteKeyword(String noteKeyword) {
        this.noteKeyword = noteKeyword;
    }
}
