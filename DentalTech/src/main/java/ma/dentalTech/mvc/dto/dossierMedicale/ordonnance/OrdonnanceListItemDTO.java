package ma.dentalTech.mvc.dto.dossierMedicale.ordonnance;

import java.time.LocalDate;

/**
 * DTO pour afficher les ordonnances dans la liste (tableau).
 * Inclut le nom du patient pour l'affichage.
 */
public class OrdonnanceListItemDTO {
    private Long ordonnanceId;
    private Long dossierId;
    private Long consultationId;
    private Long patientId;
    private String patientNomComplet; // "Nom Prénom"

    private LocalDate date;

    // Getters and Setters
    public Long getOrdonnanceId() {
        return ordonnanceId;
    }

    public void setOrdonnanceId(Long ordonnanceId) {
        this.ordonnanceId = ordonnanceId;
    }

    public Long getDossierId() {
        return dossierId;
    }

    public void setDossierId(Long dossierId) {
        this.dossierId = dossierId;
    }

    public Long getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(Long consultationId) {
        this.consultationId = consultationId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientNomComplet() {
        return patientNomComplet;
    }

    public void setPatientNomComplet(String patientNomComplet) {
        this.patientNomComplet = patientNomComplet;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
