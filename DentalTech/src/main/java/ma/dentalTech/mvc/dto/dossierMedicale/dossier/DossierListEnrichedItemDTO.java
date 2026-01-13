package ma.dentalTech.mvc.dto.dossierMedicale.dossier;

import java.time.LocalDateTime;

/**
 * DTO enrichi pour afficher un dossier médical dans la liste.
 * Inclut les informations du patient (nom, téléphone) et la dernière consultation.
 */
public class DossierListEnrichedItemDTO {
    private Long dossierId;
    private Long patientId;
    private Long medecinId;
    private String patientNomComplet;
    private String patientTelephone;
    private LocalDateTime derniereConsultation; // Date de la dernière consultation
    private Long derniereConsultationId; // ID de la dernière consultation pour affichage

    // Getters and Setters
    public Long getDossierId() {
        return dossierId;
    }

    public void setDossierId(Long dossierId) {
        this.dossierId = dossierId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getMedecinId() {
        return medecinId;
    }

    public void setMedecinId(Long medecinId) {
        this.medecinId = medecinId;
    }

    public String getPatientNomComplet() {
        return patientNomComplet;
    }

    public void setPatientNomComplet(String patientNomComplet) {
        this.patientNomComplet = patientNomComplet;
    }

    public String getPatientTelephone() {
        return patientTelephone;
    }

    public void setPatientTelephone(String patientTelephone) {
        this.patientTelephone = patientTelephone;
    }

    public LocalDateTime getDerniereConsultation() {
        return derniereConsultation;
    }

    public void setDerniereConsultation(LocalDateTime derniereConsultation) {
        this.derniereConsultation = derniereConsultation;
    }

    public Long getDerniereConsultationId() {
        return derniereConsultationId;
    }

    public void setDerniereConsultationId(Long derniereConsultationId) {
        this.derniereConsultationId = derniereConsultationId;
    }
}
